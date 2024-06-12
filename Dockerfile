FROM amazoncorretto:17-alpine-jdk
LABEL maintainer="jeongin <jeonginflow@gmail.com>"

WORKDIR /app

COPY ./build/libs/*.jar ./
COPY ./src/main/resources/static ./static
COPY ./build/api-spec ./static/docs
COPY ./build/swagger-ui-sample ./static/swagger-ui-sample

ENTRYPOINT ["java","-jar","/app/Market-Bridge-BE-0.0.1-SNAPSHOT.jar"]