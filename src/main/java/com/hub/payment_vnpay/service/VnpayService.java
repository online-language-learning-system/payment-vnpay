package com.hub.payment_vnpay.service;

import com.hub.payment_vnpay.config.VnpayConfig;
import com.hub.payment_vnpay.model.Payment;
import com.hub.payment_vnpay.model.PaymentMethod;
import com.hub.payment_vnpay.model.enumeration.PaymentStatus;
import com.hub.payment_vnpay.model.dto.VnpayRequestDto;
import com.hub.payment_vnpay.model.dto.VnpayResponseDto;
import com.hub.payment_vnpay.repository.PaymentMethodRepository;
import com.hub.payment_vnpay.repository.PaymentRepository;
import com.hub.payment_vnpay.utils.VnpayUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VnpayService {

    private final VnpayConfig config;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Transactional
    public VnpayResponseDto createPaymentUrl(VnpayRequestDto requestDto) {
        try {
            PaymentMethod method = paymentMethodRepository.findById(1)
                    .orElseGet(() -> {
                        PaymentMethod pm = new PaymentMethod();
                        pm.setMethodName("VNPay");
                        pm.setProvider("VNPay");
                        pm.setActive(true);
                        return paymentMethodRepository.save(pm);
                    });
            Map<String, String> params = new LinkedHashMap<>();
            params.put("vnp_Version", config.getVersion());
            params.put("vnp_Command", config.getCommand());
            params.put("vnp_TmnCode", config.getTmnCode());

            BigDecimal amount = requestDto.amount() != null ? requestDto.amount() : BigDecimal.ZERO;
            params.put("vnp_Amount", String.valueOf(amount.multiply(BigDecimal.valueOf(100))));
            params.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            params.put("vnp_CurrCode", config.getCurrency());
            params.put("vnp_IpAddr", "127.0.0.1");
            params.put("vnp_Locale", config.getLocale());
            params.put("vnp_OrderInfo", requestDto.orderInfo());
            params.put("vnp_OrderType", "billpayment");
            params.put("vnp_ReturnUrl", requestDto.returnUrl());
            params.put("vnp_TxnRef", requestDto.orderId().toString());

            String dataToHash = VnpayUtils.getDataToHash(params);
            String secureHash = VnpayUtils.hmacSHA512(config.getHashSecret(), dataToHash);
            String query = VnpayUtils.buildQuery(params);
            String paymentUrl = config.getApiUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;

            Payment payment = new Payment();
            payment.setOrderId(requestDto.orderId());
            payment.setUserId(requestDto.userId() != null ? requestDto.userId() : "anonymous");
            payment.setAmount(amount);
            payment.setCurrency(config.getCurrency());
            payment.setPaymentMethod(method);
            payment.setPaymentStatus(PaymentStatus.PENDING.name());
            payment.setTransactionCode(requestDto.orderId().toString());
            payment.setCreatedAt(LocalDateTime.now());

            paymentRepository.save(payment);

            System.out.println("[VNPay] Payment URL created for orderId=" + requestDto.orderId());

            return new VnpayResponseDto(
                    paymentUrl,
                    PaymentStatus.PENDING,
                    "Payment request created successfully!",
                    requestDto.orderId().toString()
            );

        } catch (Exception e) {
            System.err.println("[VNPay] Error creating payment: " + e.getMessage());
            return new VnpayResponseDto(
                    null,
                    PaymentStatus.FAILED,
                    "Error creating payment: " + e.getMessage(),
                    null
            );
        }
    }

    @Transactional
    public void handlePayment(Map<String, String> params) {
        try {
            long orderId = Long.parseLong(params.get("vnp_TxnRef"));
            String responseCode = params.get("vnp_ResponseCode");
            String transactionNo = params.get("vnp_TransactionNo");
            BigDecimal amount = new BigDecimal(params.getOrDefault("vnp_Amount", "0")).divide(BigDecimal.valueOf(100));

            Payment payment = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Payment not found for orderId=" + orderId));

            payment.setTransactionCode(transactionNo);
            payment.setAmount(amount);
            payment.setUpdatedAt(LocalDateTime.now());

            if ("00".equals(responseCode)) {
                payment.setPaymentStatus(PaymentStatus.SUCCESS.name());
                System.out.println("[VNPay] Payment SUCCESS for orderId=" + orderId);
            } else {
                payment.setPaymentStatus(PaymentStatus.FAILED.name());
                System.out.println("[VNPay] Payment FAILED for orderId=" + orderId + " | Code=" + responseCode);
            }

            paymentRepository.save(payment);

        } catch (Exception e) {
            System.err.println("[VNPay] Callback processing error: " + e.getMessage());
        }
    }
}
