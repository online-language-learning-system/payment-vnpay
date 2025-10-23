package com.hub.payment_vnpay.stream;

import com.hub.payment_vnpay.kafka.event.OrderPlacedEvent;
import com.hub.payment_vnpay.kafka.event.PaymentSucceededEvent;
import com.hub.payment_vnpay.kafka.event.PaymentFailedEvent;
import com.hub.payment_vnpay.model.enumeration.PaymentStatus;
import com.hub.payment_vnpay.service.VnPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private final VnPayService vnPayService;

    @Bean
    public Function<Flux<OrderPlacedEvent>, Flux<Object>> processOrder() {
        return orderFlux -> orderFlux.flatMap(order ->
                vnPayService.createPaymentRequest(order)
                        .flatMapMany(paymentResp ->
                                vnPayService.waitForPaymentResult(order.getOrderId())
                                        .map(status -> {
                                            if (status.equals(PaymentStatus.SUCCESS)) {
                                                return new PaymentSucceededEvent(
                                                        order.getOrderId(),
                                                        order.getTotalPrice(),
                                                        "VNPay",
                                                        PaymentStatus.SUCCESS,
                                                        order.getOrderId().toString(),
                                                        order.getStudentId()
                                                );
                                            } else {
                                                return new PaymentFailedEvent(
                                                        order.getOrderId(),
                                                        order.getStudentId(),
                                                        order.getTotalPrice(),
                                                        PaymentStatus.FAILED,
                                                        "VNPay",
                                                        order.getOrderId().toString(),
                                                        "Payment failed or cancelled by user"
                                                );
                                            }
                                        })
                        )
        );
    }
}
