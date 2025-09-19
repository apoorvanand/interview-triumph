package com.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import com.example.server.service.OrderHistoryService;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ServerApplication.class, args);
        
        // Start gRPC server
        Server server = ServerBuilder.forPort("localhost", 50051)
                .addService(new OrderHistoryService())
                .build()
                .start();

        System.out.println("gRPC server started on port 50051");
        
        // Keep the server running
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            System.err.println("Server shut down");
        }));

        server.awaitTermination();
    }
}