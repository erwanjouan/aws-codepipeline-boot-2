FROM public.ecr.aws/amazoncorretto/amazoncorretto:17-al2-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]