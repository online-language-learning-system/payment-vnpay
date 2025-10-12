package com.hub.payment_vnpay.kafka.event;

import com.hub.payment_vnpay.model.enumeration.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentSucceededEvent {
    private Long orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private LocalDateTime createdAt;
    private String studentId;

    public PaymentSucceededEvent(Long orderId, BigDecimal amount, String paymentMethod,
                                 PaymentStatus paymentStatus, String transactionId, String studentId) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.createdAt = LocalDateTime.now();
        this.studentId = studentId;
    }


    public Long getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getStudentId() { return studentId; }

    @Override
    public String toString() {
        return "PaymentSucceededEvent{" +
                "orderId=" + orderId +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus=" + paymentStatus +
                ", transactionId='" + transactionId + '\'' +
                ", createdAt=" + createdAt +
                ", studentId='" + studentId + '\'' +
                '}';
    }
}
