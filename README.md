pipeline {
    agent any

    stages {
        stage('Display .env File Content') {
            steps {
                script {
                    // Path to the .env file
                    def envFile = "/test/test.env"

                    // Read and print the content of the .env file
                    sh """
                    if [[ -f "${envFile}" ]]; then
                        echo "Contents of ${envFile}:"
                        cat "${envFile}" | while read line; do
                            if [[ ! -z "\$line" && "\$line" != \#* ]]; then
                                echo "\$line"
                            fi
                        done
                    else
                        echo "Error: .env file not found at ${envFile}"
                    fi
                    """
                }
            }
        }
    }
}
