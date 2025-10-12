package com.hub.payment_vnpay.kafka.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.hub.payment_vnpay.model.enumeration.PaymentStatus;
public class PaymentFailedEvent {

    private Long orderId;
    private String studentId;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private String transactionId;
    private String failureReason;
    private LocalDateTime createdAt;

    public PaymentFailedEvent(Long orderId, String studentId, BigDecimal amount,PaymentStatus paymentStatus, String paymentMethod, String transactionId, String failureReason) {
        this.orderId = orderId;
        this.studentId = studentId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.failureReason = failureReason;
        this.createdAt = LocalDateTime.now();
    }

    public Long getOrderId() { return orderId; }
    public String getStudentId() { return studentId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "PaymentFailedEvent{" +
                "orderId=" + orderId +
                ", studentId='" + studentId + '\'' +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus=" + paymentStatus +
                ", transactionId='" + transactionId + '\'' +
                ", failureReason='" + failureReason + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
