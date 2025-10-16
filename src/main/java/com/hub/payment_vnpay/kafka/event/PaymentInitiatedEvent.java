package com.hub.payment_vnpay.kafka.event;

import com.hub.payment_vnpay.model.enumeration.PaymentStatus;
import java.math.BigDecimal;

public record PaymentInitiatedEvent(
        Long orderId,
        BigDecimal amount,
        String paymentMethod,
        PaymentStatus status,
        String transactionId,
        String studentId,
        String paymentUrl
) {}
