# gRPC E-commerce Demo

This project demonstrates a gRPC-based e-commerce application that allows users to fetch their order history. The application is structured into two main components: a gRPC server and a gRPC client. The server handles the business logic and data retrieval, while the client interacts with the server to display the order history.

## Project Structure

```
grpc-ecommerce-demo
├── client                # gRPC client application
│   ├── src
│   │   └── main
│   │       └── java
│   │           └── com
│   │               └── example
│   │                   └── client
│   │                       └── ClientApplication.java
│   └── README.md
├── server                # gRPC server application
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
│   │       └── resources
│   │           └── application.yml
├── proto                 # Protocol Buffers schema
│   └── order_history.proto
└── README.md             # Project overview and documentation
```

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- gRPC and Protocol Buffers dependencies

### Setup Instructions

1. Clone the repository:
   ```
   git clone <repository-url>
   cd grpc-ecommerce-demo
   ```

2. Build the project:
   ```
   mvn clean install
   ```

3. Run the gRPC server:
   ```
   cd server
   mvn spring-boot:run
   ```

4. Run the gRPC client:
   ```
   cd client
   mvn spring-boot:run
   ```

### Usage

- The gRPC server exposes an internal service for fetching user order history.
- The HTTP endpoint provided by the `OrderHistoryController` allows external clients to retrieve order history via RESTful calls.

## Architecture Overview

- The server application is built using Spring Boot and gRPC, providing a robust backend for handling order history requests.
- The client application connects to the server using gRPC, allowing for efficient communication and data retrieval.
- Protocol Buffers are used to define the data structures and service methods for the order history functionality.

## Contributing

Contributions are welcome! Please submit a pull request or open an issue for any enhancements or bug fixes.