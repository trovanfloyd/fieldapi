# Field data API


## Project overview

Field data API is a REST API that manages fields and provide a weather history for a given field with a given boundary using a
third partner API from OpenWeather Agro Monitoring.

### Architecture and technologies used

- Java 16
- Spring Boot
- MongoDB
- REST Assured
- JUnit 5
- Maven

### Build and run

1. Clone the project

2. With a correctly configured Maven, create an executable jar file

```shellscript
$  mvn clean package
```

3. Go to directory `./fieldapi/dev`

4. Build and run docker compose

```shellscript
$  docker-compose up --build
```
> Ps: This command will build the images, create the defined containers and start it in one command

> Ps: This directory also contains the `docker-compose-dependencies-only` in case you want to run the dependencies from docker, but the application from IDE or command prompt

5. Check if the application is up and running:

```
http://localhost:8080/actuator/health
```

### Documentation

Swagger 2 was chosen for the API documentation and it can be accessed by:

```url service one
http://localhost:8080/swagger-ui.html#/field-controller
```
> Ps: The application should up and running
