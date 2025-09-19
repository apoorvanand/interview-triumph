# gRPC Gateway Server Application

This document provides an overview of the gRPC Gateway Server application, including setup instructions and usage examples.

## Overview

The gRPC Gateway Server acts as a gateway that allows HTTP calls to be translated into gRPC calls to another microservice. This enables seamless communication between HTTP clients and gRPC services.

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd grpc-gateway-app/server
   ```

2. **Build the Project**
   Use Maven to build the project:
   ```bash
   mvn clean install
   ```

3. **Configuration**
   Update the `src/main/resources/application.properties` file with the necessary configurations, such as the port number and service settings.

4. **Run the Server**
   You can run the server application using the following command:
   ```bash
   mvn spring-boot:run
   ```

## Usage

Once the server is running, it will listen for incoming gRPC calls. You can test the server using an HTTP client or by sending gRPC requests from the client application.

## Example

To make a gRPC call from the client, ensure that the client application is configured to point to the correct server address and port as specified in its `application.properties`.

## Additional Information

For more details on the gRPC service methods and their usage, refer to the service definition files and the client application documentation.