package com.hub.payment_vnpay.controller;

import com.hub.payment_vnpay.model.dto.VnPayRequestDto;
import com.hub.payment_vnpay.model.dto.VnPayResponseDto;
import com.hub.payment_vnpay.service.VnPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class VnPayController {

    private final VnPayService vnpayService;

    @GetMapping("payment-url")
    public ResponseEntity<String> callbackPaymentUrl(
            @RequestParam(name = "orderId") Long orderId
    ) {
        String paymentUrl = vnpayService.callbackPaymentUrlByOrderId(orderId);
        if (paymentUrl != null) {
            return ResponseEntity.ok(paymentUrl);
        } else {
            return ResponseEntity.badRequest().body("Lỗi thanh toán");
        }
    }

//    @PostMapping("/create-payment")
//    public VnPayResponseDto createPayment(@RequestBody VnPayRequestDto requestDto) {
//        return vnpayService.createPaymentUrl(requestDto);
//    }

    // Callback client
    @GetMapping("/callback")
    public void callback(@RequestParam Map<String, String> params) {
         vnpayService.handlePayment(params);
    }

    // IPN server
    @PostMapping("/ipn")
    public void ipn(@RequestParam Map<String, String> params) {
         vnpayService.handlePayment(params);
    }
}