# Client Application for gRPC Gateway

This client application acts as a gateway to make HTTP calls to a gRPC server. Below are the instructions for setting up and using the client.

## Prerequisites

- Java 11 or higher
- Maven

## Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd grpc-gateway-app/client
   ```

2. **Build the Project**
   Use Maven to build the project:
   ```bash
   mvn clean install
   ```

3. **Configure Application Properties**
   Update the `src/main/resources/application.properties` file with the appropriate server address and port.

4. **Run the Client Application**
   You can run the client application using the following command:
   ```bash
   mvn spring-boot:run
   ```

## Usage

Once the client application is running, you can make HTTP calls to the gRPC server. The client will handle the conversion of HTTP requests to gRPC calls.

## Example

To make a request, you can use tools like `curl` or Postman to send HTTP requests to the configured endpoint.

## Additional Information

For more details on the gRPC service and methods available, please refer to the server documentation.