package com.hub.payment_vnpay.service;

import com.hub.payment_vnpay.config.VnPayConfig;
import com.hub.payment_vnpay.model.enumeration.PaymentStatus;
import com.hub.payment_vnpay.model.dto.VnPayRequestDto;
import com.hub.payment_vnpay.model.dto.VnPayResponseDto;
import com.hub.payment_vnpay.utils.VnpayUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VnPayService {

    private final VnPayConfig config;
    private final RedisTemplate<Long, String> redisTemplate;

    public String callbackPaymentUrlByOrderId(Long orderId) {
        return redisTemplate.opsForValue().get(orderId);
    }

    public VnPayResponseDto createPaymentUrl(VnPayRequestDto requestDto) {
        try {
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

            System.out.println("[VNPay]  Payment URL generated successfully for orderId=" + requestDto.orderId());

            return new VnPayResponseDto(
                    paymentUrl,
                    PaymentStatus.PENDING,
                    "Payment request created successfully!",
                    requestDto.orderId().toString()
            );

        } catch (Exception e) {
            System.err.println("[VNPay] Error creating payment: " + e.getMessage());
            return new VnPayResponseDto(
                    null,
                    PaymentStatus.FAILED,
                    "Error creating payment: " + e.getMessage(),
                    null
            );
        }
    }


    public void handlePayment(Map<String, String> params) {
        try {
            long orderId = Long.parseLong(params.get("vnp_TxnRef"));
            String responseCode = params.get("vnp_ResponseCode");
            String transactionNo = params.get("vnp_TransactionNo");
            BigDecimal amount = new BigDecimal(params.getOrDefault("vnp_Amount", "0")).divide(BigDecimal.valueOf(100));

            if ("00".equals(responseCode)) {
                System.out.println("[VNPay] Thanh toán thành công cho Order ID: " + orderId);
                System.out.println("[VNPay] TransactionNo = " + transactionNo + " | Amount = " + amount);
            } else {
                System.out.println("[VNPay]  Payment failed for Order ID: " + orderId);
                System.out.println("[VNPay] Mã lỗi: " + responseCode);
            }

        } catch (Exception e) {
            System.err.println("[VNPay] ⚠ Callback processing error: " + e.getMessage());
        }
    }

    private boolean verifySignature(Map<String, String> params) {
        String vnpSecureHash = params.remove("vnp_SecureHash");
        String calculatedHash = VnpayUtils.hashAllFields(params, config.getHashSecret());
        return calculatedHash.equalsIgnoreCase(vnpSecureHash);
    }
}
