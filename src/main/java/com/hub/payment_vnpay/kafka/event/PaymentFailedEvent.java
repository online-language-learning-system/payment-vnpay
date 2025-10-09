package com.hub.payment_vnpay.kafka.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentFailedEvent {

    private Long orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionId;
    private String failureReason;
    private LocalDateTime createdAt;

    public PaymentFailedEvent(Long orderId, BigDecimal amount, String paymentMethod, String transactionId, String failureReason) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.failureReason = failureReason;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "PaymentFailedEvent{" +
                "orderId=" + orderId +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", failureReason='" + failureReason + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
