FROM maven:3.8.6-eclipse-temurin-11 AS builder

WORKDIR /build

# Copier tous les fichiers nécessaires
COPY pom-docker.xml pom.xml
COPY pdf-server/ pdf-server/
COPY pdf-client-web/backend/ pdf-client-web/backend/
COPY idl/ idl/

# Générer les stubs IDL
RUN apt-get update -q && apt-get install -y -q wget && \
    wget -q "https://repo.maven.apache.org/maven2/org/glassfish/corba/idlj/4.2.4/idlj-4.2.4.jar" \
         -O /tmp/idlj.jar && \
    java -jar /tmp/idlj.jar -fall \
         -td pdf-server/src/main/generated \
         idl/PDFService.idl

# Build Maven
RUN mvn clean package -DskipTests \
    -pl pdf-server,"pdf-client-web/backend" -am

FROM eclipse-temurin:11-jre
WORKDIR /app

COPY --from=builder \
    /build/pdf-client-web/backend/target/pdf-client-web-backend-1.0.0.jar \
    app.jar

EXPOSE 8080

CMD ["java", \
     "--add-opens", "java.base/java.lang=ALL-UNNAMED", \
     "--add-opens", "java.base/java.util=ALL-UNNAMED", \
     "-Dorg.omg.CORBA.ORBClass=com.sun.corba.ee.impl.orb.ORBImpl", \
     "-Dorg.omg.CORBA.ORBSingletonClass=com.sun.corba.ee.impl.orb.ORBSingleton", \
     "-jar", "app.jar"]
