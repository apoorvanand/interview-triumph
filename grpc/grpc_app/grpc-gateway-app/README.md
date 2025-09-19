# gRPC Gateway Application

This project implements a gRPC gateway that allows HTTP calls to be forwarded to a microservice using gRPC. It consists of two main components: a client application and a server application.

## Project Structure

```
grpc-gateway-app
├── client
│   ├── src
│   │   ├── main.py          # Entry point for the client application
│   │   └── config.py        # Configuration settings for the client
│   ├── requirements.txt      # Python dependencies for the client
│   └── README.md             # Documentation for the client application
├── server
│   ├── src
│   │   ├── main.py          # Entry point for the server application
│   │   ├── gateway.py       # Gateway functionality for HTTP to gRPC
│   │   └── config.py        # Configuration settings for the server
│   ├── requirements.txt      # Python dependencies for the server
│   └── README.md             # Documentation for the server application
├── proto
│   └── service.proto         # gRPC service definition
└── README.md                 # Overall documentation for the project
```

## Getting Started

### Prerequisites

- Python 3.x
- gRPC and Protocol Buffers installed

### Installation

1. Clone the repository:
   ```
   git clone <repository-url>
   cd grpc-gateway-app
   ```

2. Install the required dependencies for the client:
   ```
   cd client
   pip install -r requirements.txt
   ```

3. Install the required dependencies for the server:
   ```
   cd server
   pip install -r requirements.txt
   ```

### Running the Applications

1. Start the server:
   ```
   cd server/src
   python main.py
   ```

2. Start the client:
   ```
   cd client/src
   python main.py
   ```

### Usage

- The client application will listen for HTTP requests and forward them to the gRPC server.
- The server application will handle gRPC requests and respond accordingly.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

## License

This project is licensed under the MIT License. See the LICENSE file for details.