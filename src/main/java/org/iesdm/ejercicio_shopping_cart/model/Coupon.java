package org.iesdm.ejercicio_shopping_cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.plaf.basic.BasicButtonUI;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coupon {

    private Integer id;
    private String code;
    private String description;
    private String discountType;   // o un enum si el enunciado lo pide
    private Double discountValue;
    private Boolean active;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
}