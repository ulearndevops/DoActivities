pipeline {
    agent {
        kubernetes {
            yaml """
            apiVersion: v1
            kind: Pod
            metadata:
              name: jenkins-pod
            spec:
              containers:
              - name: jenkins-container
                image: jenkins/inbound-agent
            """
            defaultContainer 'jenkins-container'
            kubeconfigId 'your-kubeconfig-credential-id' // Specify your kubeconfig credential ID here
        }
    }
    stages {
        stage('Assume Role and Configure AWS CLI') {
            steps {
                script {
                    // Specify the ARN of the role to assume in account B
                    def roleArn = 'arn:aws:iam::accountB-id:role/role-name'

                    // Specify the session name for the assumed role
                    def sessionName = 'jenkins-session'

                    // Use AWS CLI to assume the role
                    def credentials = sh(
                        script: "aws sts assume-role --role-arn ${roleArn} --role-session-name ${sessionName} --output json",
                        returnStdout: true
                    ).trim()

                    // Parse the assumed role credentials
                    def json = new groovy.json.JsonSlurper().parseText(credentials)
                    def accessKeyId = json.Credentials.AccessKeyId
                    def secretAccessKey = json.Credentials.SecretAccessKey
                    def sessionToken = json.Credentials.SessionToken

                    // Set environment variables for the assumed role credentials
                    env.AWS_ACCESS_KEY_ID = accessKeyId
                    env.AWS_SECRET_ACCESS_KEY = secretAccessKey
                    env.AWS_SESSION_TOKEN = sessionToken

                    // Configure kubectl to use the assumed role credentials
                    // Update the kubeconfig with the credentials for the EKS cluster in account B
                    sh """
                    aws eks update-kubeconfig --name your-eks-cluster-name \
                    --kubeconfig /path/to/your/kubeconfig \
                    --role-arn ${roleArn} \
                    --access-key ${accessKeyId} \
                    --secret-key ${secretAccessKey} \
                    --session-token ${sessionToken}
                    """
                }
            }
        }
        stage('Create Namespace') {
            steps {
                script {
                    // Command to create a namespace
                    sh 'kubectl create namespace my-namespace --kubeconfig /path/to/your/kubeconfig'
                }
            }
        }
        stage('Create Pod') {
            steps {
                script {
                    // Command to create a pod in the specified namespace
                    sh 'kubectl run my-pod --image=nginx --namespace=my-namespace --kubeconfig /path/to/your/kubeconfig'
                }
            }
        }
        // Add more stages for other Kubernetes objects as needed
    }
}
