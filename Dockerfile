# Base image 설정
FROM openjdk:17-jdk-slim

# JAR 파일을 컨테이너에 복사
ARG JAR_FILE=build/libs/user-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app.jar

# Timezone 설정
ENV TZ=Asia/Seoul

# start.sh 스크립트 생성 및 환경 변수 적용
RUN mkdir -p /app \
    && echo "#!/bin/sh" > /app/start.sh \
    && echo "java -Dspring.profiles.active=\${SPRING_PROFILE} -jar /app.jar" >> /app/start.sh \
    && chmod +x /app/start.sh

# ENTRYPOINT 설정
ENTRYPOINT ["/app/start.sh"]