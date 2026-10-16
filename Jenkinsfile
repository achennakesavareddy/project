
pipeline {
    agent {
        label 'docker-azure-cli'
    }

    environment {
        REGISTRY = 'finalproject.azurecr.io'
        IMAGE = 'day1-demo'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B clean test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B package -DskipTests'
            }
        }

        stage('Build Image') {
            steps {
                sh 'docker build -t $REGISTRY/$IMAGE:$BUILD_NUMBER .'
            }
        }

        stage('Push to ACR') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'acr-push',
                        usernameVariable: 'ACR_USER',
                        passwordVariable: 'ACR_PASS'
                    )
                ]) {
                    sh '''
                        set +x
                        echo "$ACR_PASS" | docker login "$REGISTRY" \
                            -u "$ACR_USER" --password-stdin
                        docker push "$REGISTRY/$IMAGE:$BUILD_NUMBER"
                        docker logout "$REGISTRY"
                    '''
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/*.jar',
                allowEmptyArchive: true
        }
    }
}
