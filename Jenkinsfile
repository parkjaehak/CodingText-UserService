pipeline {
    agent any
    tools {
        gradle 'gradle'
    }

    environment {
        IMAGE_NAME = "atom8426/ct-userservice"  // Docker Hub ID와 리포지토리 이름
        APP_NAME = "ct-userservice-app"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'develop', credentialsId: 'github_access_token', url: 'https://github.com/Xeat-KEA/UserService.git'
            }
        }

        stage('Build Gradle Project') {
            steps {
                // Gradle 실행 권한 부여
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
                    // 도커 이미지 빌드, 도커 허브로 푸시
                    sh 'docker build --build-arg JAR_FILE=build/libs/user-0.0.1-SNAPSHOT.jar -t ${IMAGE_NAME}:latest .'
                    withCredentials([usernamePassword(credentialsId: 'docker_credential_id', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        docker.withRegistry('https://index.docker.io/v1/', 'docker_credential_id') {
                            sh '''
                                echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
                                docker push "${IMAGE_NAME}:latest"
                            '''
                        }
                    }
                }
            }
        }

        stage('Deploy to VM') {
            steps {
                script {
                    sh """
                        docker pull ${IMAGE_NAME}:latest
                        docker stop ${APP_NAME} || true
                        docker rm ${APP_NAME} || true
                        docker run -d --restart always -p 8081:8081 --name ${APP_NAME} \
                          --env SPRING_PROFILE=dev \
                          --env CT_DB_USER=${CT_DB_USER} \
                          --env CT_DB_PASSWORD=${CT_DB_PASSWORD} \
                          --env JWT_SECRET_KEY=${JWT_SECRET_KEY} \
                          --env NAVER_ID=${NAVER_ID} \
                          --env NAVER_SECRET=${NAVER_SECRET} \
                          --env GOOGLE_ID=${GOOGLE_ID} \
                          --env GOOGLE_SECRET=${GOOGLE_SECRET} \
                          --env KAKAO_ID=${KAKAO_ID} \
                          --env KAKAO_SECRET=${KAKAO_SECRET} \
                          --env EUREKA_SERVER_URL=${EUREKA_SERVER_URL}
                          ${IMAGE_NAME}:latest
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()  // 빌드 후 작업 공간 정리
        }
    }
}