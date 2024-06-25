To ensure that updating the `~/.aws/credentials` file does not affect other profiles, you need to carefully update only the specified profile while leaving other profiles unchanged. Below is an enhanced version of the Jenkins pipeline to ensure only the specified profile is updated.

### First Job: Credential Refresh Job

#### Jenkinsfile (First Job):

```groovy
pipeline {
    agent any
    environment {
        AWS_ROLE_ARN = 'arn:aws:iam::123456789012:role/my-role'
        AWS_ROLE_SESSION_NAME = 'my-session'
        DURATION_SECONDS = 3600
        PROFILE_NAME = 'my-profile'
    }
    stages {
        stage('Refresh AWS Credentials') {
            steps {
                script {
                    def credentials = getAWSCredentials(env.AWS_ROLE_ARN, env.AWS_ROLE_SESSION_NAME, env.DURATION_SECONDS)
                    updateAWSCredentialsFile(env.PROFILE_NAME, credentials)
                }
            }
        }
    }
}

def getAWSCredentials(roleArn, roleSessionName, durationSeconds) {
    def assumeRoleCmd = """
        aws sts assume-role \
            --role-arn ${roleArn} \
            --role-session-name ${roleSessionName} \
            --duration-seconds ${durationSeconds}
    """
    def assumeRoleResult = sh(script: assumeRoleCmd, returnStdout: true).trim()
    def credentials = readJSON text: assumeRoleResult

    return credentials.Credentials
}

def updateAWSCredentialsFile(profileName, credentials) {
    def credentialsFile = readFile("${env.HOME}/.aws/credentials")
    def profileRegex = "(?s)\\[${profileName}\\].*?(?=\\[|\\z)"
    def newProfileContent = """
        [${profileName}]
        aws_access_key_id=${credentials.AccessKeyId}
        aws_secret_access_key=${credentials.SecretAccessKey}
        aws_session_token=${credentials.SessionToken}
    """

    if (credentialsFile =~ profileRegex) {
        credentialsFile = credentialsFile.replaceAll(profileRegex, newProfileContent)
    } else {
        credentialsFile += "\n" + newProfileContent
    }

    writeFile file: "${env.HOME}/.aws/credentials", text: credentialsFile
}
```

### Second Job: Job Using Refreshed Credentials

#### Jenkinsfile (Second Job):

```groovy
pipeline {
    agent any
    environment {
        PROFILE_NAME = 'my-profile'
        FIRST_JOB_NAME = 'credential-refresh-job'
        FIRST_JOB_CRON = 'H/50 * * * *'
    }
    stages {
        stage('Schedule First Job') {
            steps {
                script {
                    // Schedule the first job
                    scheduleFirstJob(env.FIRST_JOB_NAME, env.FIRST_JOB_CRON)
                    // Trigger the first job immediately
                    build job: env.FIRST_JOB_NAME, wait: true
                }
            }
        }
        stage('Use Refreshed AWS Credentials') {
            steps {
                withAWSProfile(env.PROFILE_NAME) {
                    sh '''
                        # Your long-running operations using the refreshed credentials
                        aws s3 ls
                    '''
                }
            }
        }
    }
    post {
        always {
            script {
                // Remove the schedule for the first job
                unscheduleFirstJob(env.FIRST_JOB_NAME)
            }
        }
    }
}

def scheduleFirstJob(jobName, cronSchedule) {
    def job = Jenkins.instance.getItemByFullName(jobName)
    if (job) {
        def trigger = new hudson.triggers.TimerTrigger(cronSchedule)
        job.addTrigger(trigger)
        trigger.start(job, true)
        job.save()
    }
}

def unscheduleFirstJob(jobName) {
    def job = Jenkins.instance.getItemByFullName(jobName)
    if (job) {
        job.triggers.clear()
        job.save()
    }
}

def withAWSProfile(profileName, closure) {
    withEnv(["AWS_PROFILE=${profileName}"]) {
        closure()
    }
}
```

### Explanation:

- **First Job:**
  - Assumes an AWS role and refreshes credentials.
  - Updates only the specified profile in `~/.aws/credentials` without affecting other profiles.
  - The `updateAWSCredentialsFile` function ensures that only the specified profile is updated or added if it doesn't exist.

- **Second Job:**
  - Schedules the first job to run every 50 minutes using a cron schedule.
  - Immediately triggers the first job to refresh credentials.
  - Uses the refreshed AWS credentials by setting the `AWS_PROFILE` environment variable.
  - Performs its tasks using the refreshed credentials.
  - Stops the first job's schedule after completing its tasks.

### Key Points:

- **Cron Schedule:** The `FIRST_JOB_CRON` environment variable sets the cron expression to run the first job every 50 minutes. Adjust the cron expression as needed.
- **Scheduling and Unscheduling:** The `scheduleFirstJob` and `unscheduleFirstJob` functions manage the cron schedule for the first job.
- **Profile Update:** The `updateAWSCredentialsFile` function ensures only the specified profile in the AWS credentials file is updated with new credentials, leaving other profiles unaffected.

This setup ensures that the second job schedules and uses the first job for credential refresh, performs its tasks, and then cleans up by stopping the first job's schedule, all while ensuring that only the specified profile in the credentials file is updated.
