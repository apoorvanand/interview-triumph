from flask import Flask, request, jsonify
import grpc
import service_pb2
import service_pb2_grpc
from config import GRPC_SERVER_ADDRESS

app = Flask(__name__)

def get_grpc_channel():
    return grpc.insecure_channel(GRPC_SERVER_ADDRESS)

@app.route('/<path:endpoint>', methods=['GET', 'POST'])
def gateway(endpoint):
    channel = get_grpc_channel()
    stub = service_pb2_grpc.YourServiceStub(channel)

    if request.method == 'POST':
        data = request.json
        grpc_request = service_pb2.YourRequestType(**data)
        grpc_response = stub.YourMethod(grpc_request)
        return jsonify(grpc_response), 200

    elif request.method == 'GET':
        grpc_response = stub.YourMethod(service_pb2.YourRequestType())
        return jsonify(grpc_response), 200

if __name__ == '__main__':
    app.run(port=5000)