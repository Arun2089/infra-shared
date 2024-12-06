def call(Map config) {
    def dockerImageName = config.DOCKER_IMAGE_NAME
    stage('Push to ECR') {
        steps {
            withCredentials([string(credentialsId: "${config.SECRET_TOKEN}", variable: "AWS_SECRET_TOKEN")]) {
                script {
                   
                    def AWS_CREDENTIALS = sh(
                        script: """
                            set +x; echo ${AWS_SECRET_TOKEN} | awk '{print "-e", \$1, "-e", \$2}'
                        """,
                        returnStdout: true
                    ).trim()
                    
                 
                    sh """
                        echo "Using credentials: ${AWS_CREDENTIALS}"
                        set +x; docker run --rm ${AWS_CREDENTIALS} -v ${workspace}/:/code -v /var/run/docker.sock:/var/run/docker.sock -w /code amazon/aws-cli ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 286291623788.dkr.ecr.ap-south-1.amazonaws.com/dev ;docker push ${dockerImageName}:${BUILD_NUMBER}
                    """
                }
            }
        }
    }
}
