# gRPC E-commerce Demo

This project demonstrates a simple e-commerce application using gRPC for internal communication and Spring Boot for the server and client applications. The application allows users to fetch their order history through a gRPC service and exposes an HTTP endpoint for easy access.

## Project Structure

```
grpc-ecommerce-demo
├── client                  # gRPC client application
│   ├── src
│   │   └── main
│   │       └── java
│   │           └── com
│   │               └── example
│   │                   └── client
│   │                       └── ClientApplication.java
│   └── README.md
├── server                  # gRPC server application
│   ├── src
│   │   └── main
│   │       └── java
│   │           └── com
│   │               └── example
│   │                   └── server
│   │                       ├── ServerApplication.java
│   │                       ├── service
│   │                       │   └── OrderHistoryService.java
│   │                       └── controller
│   │                           └── OrderHistoryController.java
│   └── resources
│       └── application.yml
├── proto                   # Protocol Buffers schema
│   └── order_history.proto
└── README.md               # Project documentation
```

## Setup Instructions

1. **Clone the repository:**
   ```
   git clone <repository-url>
   cd grpc-ecommerce-demo
   ```

2. **Build the project:**
   Use Maven or Gradle to build the project. Ensure you have the necessary dependencies for Spring Boot and gRPC.

3. **Run the server:**
   Navigate to the `server` directory and run the `ServerApplication.java` file to start the gRPC server.

4. **Run the client:**
   Navigate to the `client` directory and run the `ClientApplication.java` file to start the gRPC client.

## Usage Examples

- To fetch user order history, you can make a gRPC call from the client application or access the HTTP endpoint exposed by the server.

## Architecture Overview

- The server application handles gRPC requests and serves as the backend for fetching order history.
- The client application communicates with the server using gRPC and can also expose an HTTP interface for easier access.
- The Protocol Buffers schema defines the structure of the data exchanged between the client and server.

## License

This project is licensed under the MIT License. See the LICENSE file for more details.