# Hoover Navigation API

This project is a RESTful API that simulates the navigation of a robotic hoover within a virtual room. The API accepts a room's dimensions, initial hoover position, dirt patches, and driving instructions, and returns the final position of the hoover along with the number of dirt patches cleaned.

## Table of Contents

- [Technologies](#technologies)
- [Setup](#setup)
- [How to Run](#how-to-run)
- [Endpoints](#endpoints)
- [Sample JSON Payloads](#sample-json-payloads)
- [Swagger Documentation](#swagger-documentation)
- [Testing](#testing)

## Technologies

- Java 21
- Spring Boot 3.3.4
- Spring Validation
- Lombok
- Swagger (via SpringDoc OpenAPI)
- JUnit for testing

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/IliasPapargiris/hoover-nav-app.git


## How to Run

1. You can run the application using the following command:
   ```bash
   mvn spring-boot:run

2. mvn clean package
java -jar target/robotic-hoover-0.0.1-SNAPSHOT.jar

3. The API will be available at http://localhost:8080.

## Endpoints

### POST /hoover/navigate
**Description**: Sends instructions to the hoover to navigate the room and clean dirt patches.

**Request Body**: JSON with room size, initial position, patches, and instructions.

**Response Body**: JSON with the final position of the hoover and number of cleaned patches.

## Sample JSON Payloads

### Request Payload Example
```json
{
  "roomSize": {"x": 5, "y": 5},
  "initialPosition": {"x": 1, "y": 2},
  "patches": [{"x": 1, "y": 0}, {"x": 2, "y": 2}, {"x": 2, "y": 3}],
  "instructions": "NNESEESWNWW"
}
```

### Response Payload Example
```json
{
  "coords": {"x": 1, "y": 3},
  "patches": 1
}
```

### Validation Error Example
```json
{
  "error": "Validation Failed",
  "message": "Instructions must only contain the characters N, E, S, W",
  "status": 400,
  "timestamp": "2024-10-10T11:59:08.487815"
}
```
## Swagger Documentation

Once the application is running, you can access the Swagger UI for exploring the API and its documentation:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)


## Testing

The project includes unit tests for the services and controllers.

To run the tests:
```bash
mvn test
```
The test cases cover scenarios like:

Successful navigation of the hoover.
Validation of room size, patches, and instructions.
Handling invalid inputs like out-of-bounds coordinates and unsupported instructions.

