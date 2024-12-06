def call(Map config) {
    def SECRET_TOKEN = config.SECRET_TOKEN ?: error("No token Provided")
    def dockerImageName = config.DOCKER_IMAGE_NAME ?: error("No image Provided")
    
   
   sh """ 
          echo "SECRET_TOKEN: ${SECRET_TOKEN}"
          echo "dockerImageName: ${dockerImageName}"
    """
    withCredentials([string(credentialsId: "${SECRET_TOKEN}", variable: "setup")]) {
        def AWS_CREDENTIALS = sh(
            script: """
                set +x; echo ${setup} | awk '{print "-e", \$1, "-e", \$2}'
            """,
            returnStdout: true
        ).trim()
        
        sh """
            echo "Using credentials: ${AWS_CREDENTIALS}"
            set +x; docker run --rm ${AWS_CREDENTIALS} -v ${workspace}/:/code -v /var/run/docker.sock:/var/run/docker.sock -w /code amazon/aws-cli ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 286291623788.dkr.ecr.ap-south-1.amazonaws.com/dev ;docker push ${dockerImageName}:${BUILD_NUMBER}
        """
    }
}
