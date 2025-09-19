package com.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;

public class ClientApplication {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 50051;

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(SERVER_ADDRESS, SERVER_PORT)
                .usePlaintext()
                .build();

        // Initialize your gRPC stub here
        // Example: MyServiceGrpc.MyServiceBlockingStub stub = MyServiceGrpc.newBlockingStub(channel);

        // Make gRPC calls using the stub

        // Shutdown the channel when done
        channel.shutdown();
    }
}