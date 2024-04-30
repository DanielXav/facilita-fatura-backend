FROM openjdk:17
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} facilitafatura.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/facilitafatura.jar"]