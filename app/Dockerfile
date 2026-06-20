# ── Stage 1: Maven build ──────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS maven-build
WORKDIR /build

# Copy pom first — dependency layer cached until pom.xml changes
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Then copy source and build (this layer invalidates on code change only)
COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Layer extraction ──────────────────────────────────────
FROM eclipse-temurin:17-jdk AS layer-extract
WORKDIR /app
COPY --from=maven-build /build/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# ── Stage 3: Lean runtime image ────────────────────────────────────
FROM eclipse-temurin:17-jre
WORKDIR /app

# Security: run as non-root
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+ExitOnOutOfMemoryError \
               -XX:+UseG1GC \
               -XX:+HeapDumpOnOutOfMemoryError \
               -XX:HeapDumpPath=/app/dumps \
               -Djava.security.egd=file:/dev/./urandom"

# Copy layers in order of least → most frequently changed
COPY --from=layer-extract /app/dependencies ./
COPY --from=layer-extract /app/spring-boot-loader ./
COPY --from=layer-extract /app/snapshot-dependencies ./
COPY --from=layer-extract /app/application ./

EXPOSE 8080

# Spring Boot 2.7 → JarLauncher package has NO ".launch" segment (that's 3.2+)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.JarLauncher"]