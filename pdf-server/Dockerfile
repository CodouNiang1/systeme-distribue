FROM maven:3.8.6-eclipse-temurin-11 AS builder
WORKDIR /app

COPY pom-server.xml /app/pom.xml
COPY pdf-server /app/pdf-server
COPY idl/PDFService.idl /app/PDFService.idl

RUN apt-get update && apt-get install -y wget && \
    wget -q https://repo.maven.apache.org/maven2/org/glassfish/corba/idlj/4.2.4/idlj-4.2.4.jar \
         -O /tmp/idlj.jar && \
    java -jar /tmp/idlj.jar -fall \
         -td /app/pdf-server/src/main/generated \
         /app/PDFService.idl

RUN mvn clean package -DskipTests -pl pdf-server -am

FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=builder \
    /app/pdf-server/target/pdf-server-1.0.0-jar-with-dependencies.jar \
    app.jar

EXPOSE 1050

ENTRYPOINT ["java", \
    "-Dorg.omg.CORBA.ORBClass=com.sun.corba.ee.impl.orb.ORBImpl", \
    "-Dorg.omg.CORBA.ORBSingletonClass=com.sun.corba.ee.impl.orb.ORBSingleton", \
    "-Dcorba.host=0.0.0.0", \
    "-Dcorba.port=1050", \
    "-jar", "app.jar"]
