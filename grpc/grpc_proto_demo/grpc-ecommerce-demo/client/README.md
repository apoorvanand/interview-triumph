# gRPC E-commerce Demo Client

This is the client application for the gRPC E-commerce Demo project. It connects to the gRPC server to fetch user order history.

## Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd grpc-ecommerce-demo/client
   ```

2. **Build the project**:
   Ensure you have Maven installed, then run:
   ```bash
   mvn clean install
   ```

3. **Run the client application**:
   Make sure the gRPC server is running, then execute:
   ```bash
   mvn spring-boot:run
   ```

## Usage

The client application connects to the gRPC server running on `localhost:50051`. You can modify the `ClientApplication.java` file to make specific gRPC calls to fetch user order history.

### Example

To fetch user order history, you would typically initialize the gRPC stub and call the appropriate method. Refer to the `ClientApplication.java` for implementation details.

## Dependencies

- Spring Boot
- gRPC
- Protocol Buffers

## Notes

Ensure that the server is running before starting the client application to avoid connection issues.