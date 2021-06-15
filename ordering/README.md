# ordering project

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

You can then execute your native executable with: `./target/ordering-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

## Related guides


## Local tests

```shell
$ curl http://localhost:8082/upload/test -F 'test=@plans-main.blend' -H 'Content-Type: application/octet-stream'
{"key":"uploaded/test-1623156116533","size":322506273,"etag":"\"1d24c05f7ccd1bfac7431cedab42e031\""}

$ curl -XGET http://localhost:8082/order/options -H 'Content-Type: application/json' -d '{"key":"uploaded/test-1623156116533","size":322506273,"etag":"\"1d24c05f7ccd1bfac7431cedab42e031\""}'
[{"name":"S","cost":4,"samples":4,"frameDividers":2,"resolutionX":1280,"resolutionY":720},{"name":"M","cost":10,"samples":10,"frameDividers":3,"resolutionX":1280,"resolutionY":720},{"name":"L","cost":12,"samples":100,"frameDividers":4,"resolutionX":1280,"resolutionY":720}]

$ curl -XPOST http://localhost:8082/order/rendering -H 'Content-Type: application/json' -d '{"fileObject": {"key":"uploaded/test-1623156116533","size":322506273,"etag":"\"1d24c05f7ccd1bfac7431cedab42e031\""}, "option": {"name":"L","cost":12,"samples":100,"frameDividers":4,"resolutionX":1280,"resolutionY":720}}'
{"responseId":"6011aea4-9c35-4b6a-aa03-24dc4a69b257","createdOn":"2021-06-08T13:15:17.616+00:00","chosenOption":{"name":"L","cost":12,"samples":100,"frameDividers":4,"resolutionX":1280,"resolutionY":720}}
``` 