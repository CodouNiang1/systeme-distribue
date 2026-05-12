FROM maven:3.8.6-eclipse-temurin-11 AS builder
WORKDIR /app

COPY pom-docker.xml /app/pom.xml
COPY pdf-server /app/pdf-server
COPY pdf-client-web/backend /app/pdf-client-web/backend
COPY idl/PDFService.idl /app/PDFService.idl

RUN apt-get update && apt-get install -y wget && \
    wget -q https://repo.maven.apache.org/maven2/org/glassfish/corba/idlj/4.2.4/idlj-4.2.4.jar \
         -O /tmp/idlj.jar && \
    java -jar /tmp/idlj.jar -fall \
         -td /app/pdf-server/src/main/generated \
         /app/PDFService.idl && \
    mvn clean package -DskipTests \
        -pl pdf-server,pdf-client-web/backend -am

FROM eclipse-temurin:11-jre
WORKDIR /app

COPY --from=builder \
    /app/pdf-client-web/backend/target/pdf-client-web-backend-1.0.0.jar \
    app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
    "--add-opens", "java.base/java.lang=ALL-UNNAMED", \
    "--add-opens", "java.base/java.util=ALL-UNNAMED", \
    "-Dorg.omg.CORBA.ORBClass=com.sun.corba.ee.impl.orb.ORBImpl", \
    "-Dorg.omg.CORBA.ORBSingletonClass=com.sun.corba.ee.impl.orb.ORBSingleton", \
    "-jar", "app.jar"]
