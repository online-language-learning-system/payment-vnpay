package com.hub.payment_vnpay.kafka.processor;

import com.hub.payment_vnpay.kafka.event.OrderPlacedEvent;
import com.hub.payment_vnpay.kafka.event.PaymentInitiatedEvent;
import com.hub.payment_vnpay.kafka.event.PaymentFailedEvent;
import com.hub.payment_vnpay.model.enumeration.PaymentStatus;
import com.hub.payment_vnpay.model.dto.VnpayRequestDto;
import com.hub.payment_vnpay.service.VnPayService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Function;

@Component
public class PaymentProducer {

    private final VnPayService vnpayService;

    public PaymentProducer(VnPayService vnpayService) {
        this.vnpayService = vnpayService;
    }

    @Bean
    public Function<OrderPlacedEvent, Object> processOrder() {
        return order -> {
            System.out.println("[Kafka] Get OrderPlacedEvent: " + order);

            BigDecimal amount = order.getTotalPrice();
            Long orderId = order.getOrderId();
            String studentId = order.getStudentId();

            try {
                var request = new VnpayRequestDto(
                        orderId,
                        studentId,
                        amount,
                        "Thanh toán đơn hàng của " + studentId + " - Order #" + orderId,
                        "https://return-url.com",
                        1
                );

                var paymentResponse = vnpayService.createPaymentUrl(request);

                if (paymentResponse != null && paymentResponse.paymentUrl() != null) {

                    var initiatedEvent = new PaymentInitiatedEvent(
                            orderId,
                            amount,
                            order.getPaymentMethod(),
                            PaymentStatus.PENDING,
                            paymentResponse.transactionId(),
                            studentId,
                            paymentResponse.paymentUrl()
                    );

                    System.out.println("[Kafka] Send PaymentInitiatedEvent: " + initiatedEvent);
                    return initiatedEvent;

                } else {
                    var failedEvent = new PaymentFailedEvent(
                            orderId,
                            studentId,
                            amount,
                            PaymentStatus.FAILED,
                            order.getPaymentMethod(),
                            "N/A",
                            "Unable to create VNPay payment link"
                    );
                    System.out.println("[Kafka] Send PaymentFailedEvent: " + failedEvent);
                    return failedEvent;
                }

            } catch (Exception e) {
                var failedEvent = new PaymentFailedEvent(
                        orderId,
                        studentId,
                        amount,
                        PaymentStatus.FAILED,
                        order.getPaymentMethod(),
                        "N/A",
                        "Error creating payment: " + e.getMessage()
                );
                System.err.println("[Kafka] Send PaymentFailedEvent (Exception): " + failedEvent);
                return failedEvent;
            }
        };
    }
}
