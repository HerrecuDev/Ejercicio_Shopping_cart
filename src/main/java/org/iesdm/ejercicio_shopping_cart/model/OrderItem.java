package org.iesdm.ejercicio_shopping_cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    private Integer id;
    private Integer order_Id;
    private Integer product_Id;
    private String productName;
    private java.math.BigDecimal unitPrice;
    private Integer quantity;
    private java.math.BigDecimal lineTotal;

}
