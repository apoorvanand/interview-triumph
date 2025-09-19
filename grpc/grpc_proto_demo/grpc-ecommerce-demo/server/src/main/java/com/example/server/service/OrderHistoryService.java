package com.example.server.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import com.example.proto.OrderHistoryProto;
import com.example.proto.OrderHistoryServiceGrpc;

@GrpcService
public class OrderHistoryService extends OrderHistoryServiceGrpc.OrderHistoryServiceImplBase {

    @Override
    public void getOrderHistory(OrderHistoryProto.UserId request, StreamObserver<OrderHistoryProto.OrderHistoryResponse> responseObserver) {
        // Logic to fetch order history based on user ID
        OrderHistoryProto.OrderHistoryResponse response = OrderHistoryProto.OrderHistoryResponse.newBuilder()
                .addOrders(OrderHistoryProto.Order.newBuilder().setOrderId("123").setProductName("Sample Product").setQuantity(2).build())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}