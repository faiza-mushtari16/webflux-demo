# WebFlux + DynamoDB Local Product API
This project is a reactive REST API built using Spring Boot WebFlux and AWS DynamoDB Local. It provides a full CRUD interface for managing `Product` entities designed to demonstrate reactive programming, non-blocking I/O with DynamoDB, and working with AWS SDK v2 enhanced client.

## Tech Stack
- **Java 17**
- **Spring Boot 3.4.4**
- **Spring WebFlux**
- **AWS SDK v2 (DynamoDB Enhanced Async Client)**
- **DynamoDB Local**
- **Reactor Core**
- **Project Lombok**

## Features
- Create, retrieve, update, and delete products using a clean REST API
- Reactive and asynchronous operations (non-blocking)
- AWS DynamoDB Local configuration for development
- In-memory testing without AWS cloud access
- Modular architecture with clear separation of concerns

## Project Structure
┌── config/ → DynamoDB client configuration  
├── controller/ → REST endpoints   
├── model/ → Product data model   
├── repository/ → Data access layer (DynamoDB operations)   
├── service/ → Business logic layer   
└── resources/ → Application properties  

## API Endpoints
| Method | Endpoint           | Description           |
|--------|--------------------|-----------------------|
| GET    | `/`                | Welcome endpoint      |
| POST   | `/products`        | Create a new product  |
| GET    | `/products/{id}`   | Get product by ID     |
| GET    | `/products`        | List all products     |
| PUT    | `/products/{id}`   | Update product by ID  |
| DELETE | `/products/{id}`   | Delete product by ID  |

## DynamoDB Local Setup
### 1. Download & Run DynamoDB Local
Start DynamoDB Local using the following command:
```bash
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
```
Make sure it's running on http://localhost:8000.
You can test if DynamoDB Local is running using the following curl command:
```bash
curl http://localhost:8000/
```  
If the DynamoDB Local service is running correctly, it should return a response, confirming the service is up and accessible.

### 2. Create Table
Run the following command to create the Product table:
```bash
aws dynamodb create-table \
  --table-name Product \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
  --endpoint-url http://localhost:8000 \
  --region us-west-2
```  

## AWS SDK Configuration
The app connects to DynamoDB Local using dummy AWS credentials:
```java
AwsBasicCredentials.create("dummyAccessKey", "dummySecretKey")
```
This configuration is set in the DynamoDbConfig.java file.

## Build & Run
### 1. Build the project
To build the project, run the following command:
```bash
gradle clean build
```  
### 2. Run the app
Start the application using:
```bash
gradle bootRun
```  
The app will be running at http://localhost:8080.

## Sample Payload (JSON)
Example request body for creating a product:
```json
{
  "name": "Wireless Mouse",
  "price": 25.99
}
```  

## Summary
- Integrated DynamoDB Local with WebFlux
- Configured AWS SDK with Enhanced Async Client
- Created CRUD endpoints for Product
- Defined schema using DynamoDbBean and annotations
- Added async repository using Mono and Flux
- Connected and tested with local DynamoDB table
- Handled credentials and region setup for local use
