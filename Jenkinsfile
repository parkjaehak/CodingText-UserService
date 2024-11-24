pipeline {
    agent any
    tools {
        gradle 'gradle'
    }

    environment {
        IMAGE_NAME = "atom8426/ct-userservice-dev"
        APP_NAME = "ct-userservice-app"
        TARGET_HOST = "config@172.16.211.116"
        SSH_CREDENTIALS = "jenkins-ssh"
        ACTIVE_PROFILE = 'dev'
        CONFIG_SERVER_URL = 'http://172.16.211.116:9000'
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
                    sh "docker build -t ${IMAGE_NAME}:latest ."
                    withCredentials([usernamePassword(credentialsId: 'docker_credential_id', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        sh '''
                            echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
                            docker push "${IMAGE_NAME}:latest"
                        '''
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
                                docker pull ${IMAGE_NAME}:latest
                                docker stop ${APP_NAME} || true
                                docker rm ${APP_NAME} || true
                                docker run -d --restart always --network host --name ${APP_NAME} \
                                  --env ACTIVE_PROFILE=${ACTIVE_PROFILE} \
                                  --env CONFIG_SERVER_URL=${CONFIG_SERVER_URL} \
                                  ${IMAGE_NAME}:latest
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