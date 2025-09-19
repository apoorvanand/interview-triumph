package com.example.server.controller;

import com.example.server.service.OrderHistoryServiceGrpc;
import com.example.server.service.OrderHistoryOuterClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderHistoryController {

    private final OrderHistoryServiceGrpc.OrderHistoryServiceBlockingStub orderHistoryServiceStub;

    @Autowired
    public OrderHistoryController(OrderHistoryServiceGrpc.OrderHistoryServiceBlockingStub orderHistoryServiceStub) {
        this.orderHistoryServiceStub = orderHistoryServiceStub;
    }

    @GetMapping("/api/orders/{userId}")
    public OrderHistoryOuterClass.OrderHistoryResponse getOrderHistory(@PathVariable String userId) {
        OrderHistoryOuterClass.OrderHistoryRequest request = OrderHistoryOuterClass.OrderHistoryRequest.newBuilder()
                .setUserId(userId)
                .build();
        return orderHistoryServiceStub.getOrderHistory(request);
    }
}