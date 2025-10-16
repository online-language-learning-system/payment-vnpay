package com.hub.payment_vnpay.kafka.processor;

import com.hub.payment_vnpay.kafka.event.OrderPlacedEvent;
import com.hub.payment_vnpay.model.dto.VnPayRequestDto;
import com.hub.payment_vnpay.model.dto.VnPayResponseDto;
import com.hub.payment_vnpay.service.VnPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final VnPayService vnPayService;
    private final RedisTemplate<Long, String> redisTemplate;

    @Bean
    private Consumer<OrderPlacedEvent> handleOrderPlaced() {

        return orderPlacedEvent -> {
            VnPayRequestDto vnpayRequestDto = VnPayRequestDto.builder()
                    .orderId(orderPlacedEvent.getOrderId())
                    .amount(orderPlacedEvent.getTotalPrice())
                    .orderInfo("Order payment #" + orderPlacedEvent.getOrderId())
                    .returnUrl("https://return-url.com")
                    .build();

            VnPayResponseDto vnpayResponseDto = vnPayService.createPaymentUrl(vnpayRequestDto);
            redisTemplate.opsForValue().set(orderPlacedEvent.getOrderId(), vnpayResponseDto.paymentUrl());
        };
    }

}
