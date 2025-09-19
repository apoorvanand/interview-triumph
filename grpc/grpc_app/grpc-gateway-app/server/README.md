# gRPC Gateway Server

This directory contains the server application for the gRPC gateway. The server acts as a bridge between HTTP clients and the gRPC microservice.

## Overview

The server application is responsible for:

- Initializing the gRPC server.
- Registering service implementations.
- Handling incoming HTTP requests and forwarding them to the gRPC service.

## Setup Instructions

1. **Install Dependencies**: Navigate to the `server` directory and install the required Python packages.

   ```bash
   pip install -r requirements.txt
   ```

2. **Run the Server**: Execute the main application file to start the gRPC server.

   ```bash
   python src/main.py
   ```

3. **Configuration**: Modify the `src/config.py` file to adjust settings such as the port number and gRPC service details.

## Usage

Once the server is running, it will listen for HTTP requests and convert them into gRPC calls to the microservice. Ensure that the client application is configured to point to the correct server address.

## Additional Information

Refer to the `proto/service.proto` file for details on the gRPC service definitions and message types used in this application.