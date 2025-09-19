# gRPC Gateway Application

This project implements a gRPC gateway that allows HTTP calls to be routed to a gRPC microservice. It consists of two main components: a client application and a server application.

## Project Structure

```
grpc-gateway-app
├── client
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   │       └── example
│   │   │   │           └── client
│   │   │   │               └── ClientApplication.java
│   │   │   └── resources
│   │   │       └── application.properties
│   ├── pom.xml
│   └── README.md
├── server
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   │       └── example
│   │   │   │           └── server
│   │   │   │               └── ServerApplication.java
│   │   │   └── resources
│   │   │       └── application.properties
│   ├── pom.xml
│   └── README.md
└── README.md
```

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven

### Setup Instructions

1. **Clone the repository:**
   ```
   git clone <repository-url>
   cd grpc-gateway-app
   ```

2. **Build the project:**
   Navigate to both the `client` and `server` directories and run:
   ```
   mvn clean install
   ```

3. **Configure the applications:**
   Update the `application.properties` files in both the `client` and `server` directories to set the appropriate server address, port, and other configurations.

4. **Run the server:**
   Navigate to the `server` directory and run:
   ```
   mvn spring-boot:run
   ```

5. **Run the client:**
   In a new terminal, navigate to the `client` directory and run:
   ```
   mvn spring-boot:run
   ```

### Usage Examples

- The client application can make HTTP requests that are translated into gRPC calls to the server.
- Refer to the specific `README.md` files in the `client` and `server` directories for detailed usage instructions and examples.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License. See the LICENSE file for details.