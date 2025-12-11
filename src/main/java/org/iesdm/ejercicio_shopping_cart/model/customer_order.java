package org.iesdm.ejercicio_shopping_cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class customer_order {

    private Integer id;
    private String orderNumber;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private Status status;
    private java.math.BigDecimal grossTotal;
    private java.math.BigDecimal discountTotal;
    private java.math.BigDecimal finalTotal;
    private Integer couponId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String paymentDetails;
    private String billingName;
    private String billingTaxId;
    private String billingStreet;
    private String billingCity;
    private String billingPostalCode;
    private String billingCountry;
    private String shippingName;
    private String shippingStreet;
    private String shippingCity;
    private String shippingPostalCode;
    private String shippingCountry;

    public enum Status {
        PENDING,
        COMPLETED,
        CANCELED,
        REFUNDED
    }

    public enum PaymentMethod {
        CREDIT_CARD,
        PAYPAL,
        BANK_TRANSFER
    }

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED
    }
   
}
