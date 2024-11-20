pipeline {
    agent any
    tools {
        gradle 'gradle'
    }

    environment {
        IMAGE_NAME = "atom8426/ct-userservice-dev"  // Docker Hub ID와 리포지토리 이름
        DOCKER_COMPOSE_FILE = "/home/pjh1/spring-cloud/docker-compose.yml" // Docker Compose 파일 경로
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
                   // Docker Compose를 사용하여 배포
                   sh """
                        docker-compose -f ${DOCKER_COMPOSE_FILE} pull
                        docker-compose -f ${DOCKER_COMPOSE_FILE} up -d
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