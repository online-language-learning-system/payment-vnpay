package com.hub.payment_vnpay.kafka.event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent {

    private Long orderId;
    private String studentId;
    private List<Long> courseIds;
    private BigDecimal totalPrice;
    private String orderStatus;
    private OffsetDateTime createdOn;
    private String paymentMethod;
}
