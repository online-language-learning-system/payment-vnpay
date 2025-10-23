package com.hub.payment_vnpay.controller;

import com.hub.payment_vnpay.kafka.event.OrderPlacedEvent;
import com.hub.payment_vnpay.model.dto.VnPayResponseDto;
import com.hub.payment_vnpay.service.VnPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/vnpay")
@RequiredArgsConstructor
public class VnPayController {

    private final VnPayService vnPayService;
    @PostMapping("/create-payment")
    public Mono<VnPayResponseDto> createPayment(@RequestBody OrderPlacedEvent orderPlacedEvent) {
        return vnPayService.createPaymentRequest(orderPlacedEvent);
    }
//    @PostMapping("/ipn")
//    public Mono<String> handleIpn(@RequestParam Map<String, String> params) {
//        return vnPayService.handlePaymentCallback(params);
//    }
    @GetMapping("/callback")
    public Mono<String> handleCallback(@RequestParam Map<String, String> params) {
        return vnPayService.handlePaymentCallback(params);
    }
}

