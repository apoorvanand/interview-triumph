package com.ecommerce.services;

import com.ecommerce.models.Payment;
import com.ecommerce.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment processPayment(Payment payment) {
        // Logic to process payment
        // This could include payment gateway integration
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public void updatePaymentStatus(Long id, String status) {
        Payment payment = getPaymentById(id);
        if (payment != null) {
            payment.setStatus(status);
            paymentRepository.save(payment);
        }
    }
}