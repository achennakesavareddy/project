pipeline {
  agent { label 'docker-azure-cli' }
  environment {
    REGISTRY = 'finalproject-gsg3c5awh3cehkc5.azurecr.io'
    IMAGE = 'day1-demo'
    JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
    PATH = "${JAVA_HOME}/bin:${env.PATH}"
    TRIVY_CACHE_DIR = '/var/lib/jenkins/trivy-cache'
    TMPDIR = '/var/lib/jenkins/trivy-tmp'
  }
  stages {
    stage('Checkout') { steps { checkout scm } }
    stage('Test')     { steps { sh 'mvn -B clean test' } }
    stage('Package')  { steps { sh 'mvn -B package -DskipTests' } }
    stage('Build Image') {
      steps { sh 'docker build -t $REGISTRY/$IMAGE:$BUILD_NUMBER .' }
    }
    stage('Security Scan') {
      steps {
        sh '''
          mkdir -p security-reports
          trivy image --scanners vuln --timeout 20m --format json \
            --output security-reports/trivy-image.json "$REGISTRY/$IMAGE:$BUILD_NUMBER"
          trivy image --scanners vuln --severity HIGH,CRITICAL --timeout 20m \
            --exit-code 1 "$REGISTRY/$IMAGE:$BUILD_NUMBER"
        '''
      }
      post {
        always {
          archiveArtifacts artifacts: 'security-reports/*.json', allowEmptyArchive: true
        }
      }
    }
    stage('Push to ACR') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'acr-push',
            usernameVariable: 'ACR_USER', passwordVariable: 'ACR_PASS')]) {
          sh '''
            set +x
            echo "$ACR_PASS" | docker login "$REGISTRY" -u "$ACR_USER" --password-stdin
            docker push "$REGISTRY/$IMAGE:$BUILD_NUMBER"
            docker logout "$REGISTRY"
          '''
        }
      }
    }
  }
  post {
    always { archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true }
  }
}
