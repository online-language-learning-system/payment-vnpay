package com.hub.payment_vnpay.kafka.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentSucceededEvent {
    private Long orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime createdAt;

    // Constructor tiện lợi
    public PaymentSucceededEvent(Long orderId, BigDecimal amount, String paymentMethod, String transactionId) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
