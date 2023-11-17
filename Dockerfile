FROM openjdk:17-jdk-alpine

ARG JAR_FILE=./build/libs/jikgong-0.0.1-SNAPSHOT.jar

# JAR 파일 메인 디렉토리에 복사
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-Dspring.profiles.active=prod", "-jar","/app.jar"]