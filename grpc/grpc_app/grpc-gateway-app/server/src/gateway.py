from flask import Flask, request, jsonify
import grpc
import service_pb2
import service_pb2_grpc

app = Flask(__name__)

@app.route('/<path:path>', methods=['GET', 'POST', 'PUT', 'DELETE'])
def gateway(path):
    channel = grpc.insecure_channel('localhost:50051')
    stub = service_pb2_grpc.YourServiceStub(channel)

    if request.method == 'GET':
        response = stub.YourGetMethod(service_pb2.YourRequest(param=request.args.get('param')))
    elif request.method == 'POST':
        response = stub.YourPostMethod(service_pb2.YourRequest(param=request.json.get('param')))
    elif request.method == 'PUT':
        response = stub.YourPutMethod(service_pb2.YourRequest(param=request.json.get('param')))
    elif request.method == 'DELETE':
        response = stub.YourDeleteMethod(service_pb2.YourRequest(param=request.args.get('param')))
    
    return jsonify(response)

if __name__ == '__main__':
    app.run(port=8080)