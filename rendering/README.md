# rendering project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/rendering-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.


Build container
```
mvn package -DskipTests
docker build -t rendering -f src/main/docker/Dockerfile.blender.buster.jvm .
docker tag rendering:latest alainpham/rendering:latest

docker push alainpham/rendering:latest


docker run --rm --net primenet --ip 172.18.0.230 -p 8080:8080 -e FILESERVERURL=172.18.0.1:8090/download/ -e UPLOADSERVERURL=172.18.0.1:8090/renders/ alainpham/rendering:latest
```


KAFKABROKER=amqstreams:9092


oc new-app alainpham/rendering:latest -e KAFKABROKER=my-cluster-kafka-bootstrap:9092 -e FILESERVERURL=handyman-ordering:8080/download/ -e UPLOADSERVERURL=handyman-ordering:8080/renders/ 
oc delete service rendering
oc expose deployment/rendering --port=8083
oc expose svc rendering

oc delete all -l app=rendering