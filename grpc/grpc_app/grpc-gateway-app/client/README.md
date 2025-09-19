# Client Application for gRPC Gateway

This README provides information about the client application that acts as a gateway for HTTP calls to a gRPC microservice.

## Overview

The client application is designed to handle incoming HTTP requests and forward them to the gRPC service. It serves as an intermediary, allowing clients to interact with the gRPC service using standard HTTP calls.

## Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd grpc-gateway-app/client
   ```

2. **Install Dependencies**
   Ensure you have Python 3.6 or higher installed. Then, install the required packages using pip:
   ```bash
   pip install -r requirements.txt
   ```

3. **Configuration**
   Update the `config.py` file to set the gRPC server address and any other necessary parameters.

4. **Run the Client Application**
   You can start the client application by running:
   ```bash
   python src/main.py
   ```

## Usage

Once the client application is running, you can send HTTP requests to the specified endpoint. The client will forward these requests to the gRPC service and return the response.

### Example

To test the client, you can use tools like `curl` or Postman to send HTTP requests to the client server.

```bash
curl -X GET http://localhost:<port>/your-endpoint
```

Replace `<port>` with the port number specified in your configuration.

## Contributing

If you would like to contribute to this project, please fork the repository and submit a pull request with your changes.

## License

This project is licensed under the MIT License. See the LICENSE file for more details.