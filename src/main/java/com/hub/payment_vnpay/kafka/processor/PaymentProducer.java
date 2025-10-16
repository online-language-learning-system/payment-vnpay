package com.hub.payment_vnpay.kafka.processor;

import com.hub.payment_vnpay.kafka.event.OrderPlacedEvent;
import com.hub.payment_vnpay.kafka.event.PaymentSucceededEvent;
import com.hub.payment_vnpay.kafka.event.PaymentFailedEvent;
import com.hub.payment_vnpay.model.dto.VnPayRequestDto;
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
            System.out.println("[Kafka] Get  OrderPlacedEvent: " + order);

            BigDecimal amount = order.getTotalPrice();
            Long orderId = order.getOrderId();

            try {

                var request = new VnPayRequestDto(
                        orderId,
                        amount,
                        "Order payment #" + orderId,
                        "https://return-url.com"
                );

                var paymentResponse = vnpayService.createPaymentUrl(request);

                if (paymentResponse != null && paymentResponse.paymentUrl() != null) {
                    PaymentSucceededEvent succeededEvent = new PaymentSucceededEvent(
                            orderId,
                            amount,
                            order.getPaymentMethod(),
                            paymentResponse.transactionId()
                    );
                    System.out.println("[Kafka]  Send PaymentSucceededEvent: " + succeededEvent);
                    return succeededEvent;
                } else {

                    PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                            orderId,
                            amount,
                            order.getPaymentMethod(),
                            "N/A",
                            "Unable to create VNPay payment link"
                    );
                    System.out.println("[Kafka]  Gửi PaymentFailedEvent: " + failedEvent);
                    return failedEvent;
                }

            } catch (Exception e) {

                PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                        orderId,
                        amount,
                        order.getPaymentMethod(),
                        "N/A",
                        "Error creating payment " + e.getMessage()
                );
                System.err.println("[Kafka] Send PaymentFailedEvent (Exception): " + failedEvent);
                return failedEvent;
            }
        };
    }
}
