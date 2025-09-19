from flask import Flask, request, jsonify
import grpc
import service_pb2
import service_pb2_grpc

app = Flask(__name__)

# Load configuration
GRPC_SERVER_ADDRESS = "localhost:50051"

# Create a gRPC channel
channel = grpc.insecure_channel(GRPC_SERVER_ADDRESS)
stub = service_pb2_grpc.YourServiceStub(channel)

@app.route('/your-endpoint', methods=['POST'])
def your_endpoint():
    data = request.json
    # Convert HTTP request data to gRPC request
    grpc_request = service_pb2.YourRequestType(**data)
    
    # Call the gRPC service
    grpc_response = stub.YourMethod(grpc_request)
    
    # Convert gRPC response to HTTP response
    return jsonify(grpc_response)

if __name__ == "__main__":
    app.run(port=5000)