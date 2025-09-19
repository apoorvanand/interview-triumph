package com.ecommerce.controllers;

import com.ecommerce.models.Cart;
import com.ecommerce.models.Order;
import com.ecommerce.models.Product;
import com.ecommerce.services.OrderService;
import com.ecommerce.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/products")
    public List<Product> viewProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/orders")
    public Order placeOrder(@RequestBody Order order) {
        return orderService.placeOrder(order);
    }

    @GetMapping("/cart")
    public Cart viewCart(@RequestParam Long userId) {
        return orderService.getCartByUserId(userId);
    }

    @PostMapping("/cart/add")
    public Cart addToCart(@RequestParam Long userId, @RequestBody Product product) {
        return orderService.addToCart(userId, product);
    }

    @PostMapping("/cart/checkout")
    public Order checkout(@RequestParam Long userId) {
        return orderService.checkout(userId);
    }
}