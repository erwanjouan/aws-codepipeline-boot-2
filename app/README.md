# Java 17 → 21 Migration Notes

Spring Boot **4.1.0** + Java **21**. Key changes that broke things or required explicit action.

---

## Jackson 3.x — package rename

Spring Boot 4.x ships **Jackson 3.x**, which renamed the root package:

| Before (Jackson 2.x) | After (Jackson 3.x) |
|---|---|
| `com.fasterxml.jackson.databind.ObjectMapper` | `tools.jackson.databind.ObjectMapper` |
| `com.fasterxml.jackson.core.type.TypeReference` | `tools.jackson.core.type.TypeReference` |
| `com.fasterxml.jackson.databind.annotation.JsonDeserialize` | `tools.jackson.databind.annotation.JsonDeserialize` |
| `com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder` | `tools.jackson.databind.annotation.JsonPOJOBuilder` |

Every Jackson import in the codebase must use the `tools.jackson` prefix. The API surface is otherwise unchanged for common usage (`ObjectMapper`, `TypeReference`, `@JsonDeserialize`, `@JsonPOJOBuilder`).

> Lombok is **not** used in this project — no action needed there.

---

## Dockerfile — three changes

### 1. Base images

```dockerfile
# Before
FROM maven:3.9-eclipse-temurin-17 AS maven-build
FROM eclipse-temurin:17-jdk AS layer-extract
FROM eclipse-temurin:17-jre

# After
FROM maven:3.9-eclipse-temurin-21 AS maven-build
FROM eclipse-temurin:21-jdk AS layer-extract
FROM eclipse-temurin:21-jre
```

### 2. Layer extraction — `layertools` removed in Spring Boot 3.2+

`-Djarmode=layertools` was removed. The replacement is `-Djarmode=tools`:

```dockerfile
# Before
RUN java -Djarmode=layertools -jar app.jar extract

# After
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher
```

### 3. Extracted layer paths moved into a subdirectory

`jarmode=tools` extracts into a subdirectory named after the JAR (without extension), not into the working directory directly:

```dockerfile
# Before — layers extracted to /app/{layer}/
COPY --from=layer-extract /app/dependencies ./
COPY --from=layer-extract /app/spring-boot-loader ./
COPY --from=layer-extract /app/snapshot-dependencies ./
COPY --from=layer-extract /app/application ./

# After — layers extracted to /app/app/{layer}/  (app.jar → app/)
COPY --from=layer-extract /app/app/dependencies ./
COPY --from=layer-extract /app/app/spring-boot-loader ./
COPY --from=layer-extract /app/app/snapshot-dependencies ./
COPY --from=layer-extract /app/app/application ./
```

---

## Spring Boot launcher class — changed in 3.2+

```dockerfile
# Before (Spring Boot 2.x)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.JarLauncher"]

# After (Spring Boot 3.2+ / 4.x)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
```

---

## `aws-serverless-java-container` hijacks the embedded web server

`aws-serverless-java-container-springboot4` ships a `ServerlessAutoConfiguration` that replaces embedded Tomcat with a serverless handler. When running as a Docker container (ECS, local), the app starts but **binds to no TCP port** and health checks never respond.

Fix — exclude the auto-configuration in `application.yml`:

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.cloud.function.serverless.web.ServerlessAutoConfiguration
```

The Lambda path (`StreamLambdaHandler` → `SpringBootLambdaContainerHandler`) bootstraps its own Spring context independently and is unaffected by this exclusion.

---

## Maven profiles

| Profile | Command | Use case |
|---|---|---|
| `normal` (default) | `mvn package` | Fat JAR with layers, for Docker / ECS |
| `lambda` | `mvn package -Plambda` | Shaded JAR (no Tomcat), for AWS Lambda |

The `lambda` profile skips Spring Boot repackage and uses `maven-shade-plugin` instead, which excludes `org.apache.tomcat.embed`.

---

## CDK / AWS infrastructure

| Resource | Before | After |
|---|---|---|
| CodeBuild image | `LinuxBuildImage.STANDARD_6_0` (AL2) | `LinuxBuildImage.STANDARD_7_0` (AL2023 + Corretto 21) |
| EC2 Image Builder | `java-17-amazon-corretto-headless` | `java-21-amazon-corretto-headless` |
