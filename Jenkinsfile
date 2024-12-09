pipeline {
    agent any
    tools {
        gradle 'gradle'
    }

    environment {
        IMAGE_NAME = "hurraypersimmon/codingtext"
        IMAGE_TAG = "userservice"
        APP_NAME = "ct-userservice-app"
        TARGET_HOST = "s112@172.16.211.112"
        SSH_CREDENTIALS = "jenkins_private_key"
        ACTIVE_PROFILE = "dev"
        CONFIG_SERVER_URL = "172.16.211.110:9000"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'develop',
                    credentialsId: 'github_access_token',
                    url: 'https://github.com/Xeat-KEA/UserService.git'
            }
        }

        stage('Build Gradle Project') {
            steps {
                sh '''
                    echo 'gradlew 빌드 시작'
                    chmod +x ./gradlew
                    ./gradlew clean build
                '''
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                script {
                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                    withCredentials([usernamePassword(credentialsId: 'ct-dockerhub', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        docker.withRegistry('https://index.docker.io/v1/', 'ct-dockerhub') {
                            sh '''
                                echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
                                docker push "${IMAGE_NAME}:${IMAGE_TAG}"
                            '''
                        }
                    }
                }
            }
        }

        stage('Deploy to VM') {
            steps {
                script {
                    sshagent(credentials: [SSH_CREDENTIALS]) {
                        sh """
                            ssh -o StrictHostKeyChecking=no ${TARGET_HOST} '
                                docker pull ${IMAGE_NAME}:${IMAGE_TAG}
                                docker stop ${APP_NAME} || true
                                docker rm ${APP_NAME} || true
                                docker run -d --restart always --network host --name ${APP_NAME} \
                                  --env ACTIVE_PROFILE=${ACTIVE_PROFILE} \
                                  --env CONFIG_SERVER_URL=${CONFIG_SERVER_URL} \
                                  ${IMAGE_NAME}:${IMAGE_TAG}
                                docker system prune -a -f
                            '
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs() // 빌드 후 작업 공간 정리
        }
    }
}