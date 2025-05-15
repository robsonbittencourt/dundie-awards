FROM amazoncorretto:21.0.7

WORKDIR /app
COPY build/libs/dundie-awards.jar dundie-awards.jar

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "dundie-awards.jar"]