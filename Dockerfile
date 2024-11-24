# Base image 설정
FROM openjdk:17-jdk-slim

# JAR 파일을 컨테이너에 복사
ARG JAR_FILE=build/libs/user-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app.jar

# Timezone 설정
ENV TZ=Asia/Seoul

# ENTRYPOINT 설정 (java 실행 명령어 직접 정의)
ENTRYPOINT ["java", "-jar", "/app.jar"]
