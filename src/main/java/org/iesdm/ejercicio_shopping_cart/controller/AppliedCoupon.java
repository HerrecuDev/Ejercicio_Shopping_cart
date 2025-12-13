package org.iesdm.ejercicio_shopping_cart.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppliedCoupon {
    private String code;
    private BigDecimal discount;
}
