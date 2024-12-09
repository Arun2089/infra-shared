

def call(Map config) {
  
    if (!config.dockerimagename || !config.secretToken || !config.region || !config.ecrRepo) {
        error "Missing required parameters: dockerimagename, secretToken, region, or ecrRepo"
    }

    withCredentials([string(credentialsId: config.secretToken, variable: 'setup')]) {
       
        def AWS_CREDENTIALS = sh (
            script: """
                set +x; echo ${setup} | awk '{print "-e", \$1, "-e", \$2}'
            """,
            returnStdout: true
        ).trim()

        sh """
            echo "${AWS_CREDENTIALS}"
            set +x; docker run --rm ${AWS_CREDENTIALS} -v ${workspace}/:/code -v /var/run/docker.sock:/var/run/docker.sock -w /code amazon/aws-cli ecr get-login-password --region ${config.region} | docker login --username AWS --password-stdin ${config.ecrRepo}
            docker push ${config.dockerimagename}
        """
    }
}
