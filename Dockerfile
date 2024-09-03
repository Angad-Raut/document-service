FROM openjdk:17
EXPOSE 1991
ADD target/document-details.jar document-details.jar
ENTRYPOINT ["java","-jar","document-details.jar"]