FROM bellsoft/liberica-openjre-alpine:21

WORKDIR /app

COPY build/libs/*.jar /app/
COPY data/raw /app/data/raw

RUN find /app -maxdepth 1 -name "*-plain.jar" -delete \
    && find /app -maxdepth 1 -name "*.jar" -exec mv {} /app/app.jar \; \
    && mkdir -p /app/storage/leads /app/storage/events /app/storage/exports

ENV APP_DATA_ROOT=/app/data/raw \
    APP_STORAGE_ROOT=/app/storage \
    APP_SITE_BASE_URL=https://septicpath.com

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseSerialGC", "-Xms256m", "-Xmx384m", "-Xss512k", "-jar", "/app/app.jar"]
