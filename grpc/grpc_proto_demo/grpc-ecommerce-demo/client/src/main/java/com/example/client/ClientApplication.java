package com.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;
import com.example.proto.OrderHistoryServiceGrpc;
import com.example.proto.OrderHistoryRequest;
import com.example.proto.OrderHistoryResponse;

public class ClientApplication {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 50051;

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(SERVER_ADDRESS, SERVER_PORT)
                .usePlaintext()
                .build();

        OrderHistoryServiceGrpc.OrderHistoryServiceBlockingStub stub = OrderHistoryServiceGrpc.newBlockingStub(channel);

        // Example of fetching user order history
        OrderHistoryRequest request = OrderHistoryRequest.newBuilder()
                .setUserId("12345") // Replace with actual user ID
                .build();
        
        OrderHistoryResponse response = stub.getOrderHistory(request);
        System.out.println("Order History: " + response.getOrdersList());

        // Shutdown the channel when done
        channel.shutdown();
    }
}