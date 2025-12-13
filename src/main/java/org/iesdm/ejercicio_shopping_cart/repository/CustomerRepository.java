package org.iesdm.ejercicio_shopping_cart.repository;

import org.iesdm.ejercicio_shopping_cart.model.CustomerOrder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository

public class CustomerRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<CustomerOrder> mapper = (rs, rowNum) -> CustomerOrder.builder()
            .id(rs.getInt("id"))
            .orderNumber(rs.getString("order_number"))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
            .status(CustomerOrder.Status.valueOf(rs.getString("status").toUpperCase()))
            .grossTotal(rs.getBigDecimal("gross_total"))
            .discountTotal(rs.getBigDecimal("discount_total"))
            .finalTotal(rs.getBigDecimal("final_total"))
            .couponId((Integer) rs.getObject("coupon_id"))
            .paymentMethod(CustomerOrder.PaymentMethod.valueOf(rs.getString("payment_method").toUpperCase()))
            .paymentStatus(CustomerOrder.PaymentStatus.valueOf(rs.getString("payment_status").toUpperCase()))
            .paymentDetails(rs.getString("payment_details"))
            .billingName(rs.getString("billing_name"))
            .billingTaxId(rs.getString("billing_tax_id"))
            .billingStreet(rs.getString("billing_street"))
            .billingCity(rs.getString("billing_city"))
            .billingPostalCode(rs.getString("billing_postal_code"))
            .billingCountry(rs.getString("billing_country"))
            .shippingName(rs.getString("shipping_name"))
            .shippingStreet(rs.getString("shipping_street"))
            .shippingCity(rs.getString("shipping_city"))
            .shippingPostalCode(rs.getString("shipping_postal_code"))
            .shippingCountry(rs.getString("shipping_country"))
            .build();

    public CustomerRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public CustomerOrder save(CustomerOrder order) {
        if (order.getId() == null) {

            if (order.getOrderNumber() == null) {
                order.setOrderNumber("ORD-" + System.currentTimeMillis());
            }

            if (order.getCreatedAt() == null) {
                order.setCreatedAt(LocalDateTime.now());
            }

            if (order.getBillingName() == null) {
                order.setBillingName("Sin nombre");
            }
            if (order.getBillingStreet() == null) {
                order.setBillingStreet("Sin calle");
            }
            if (order.getBillingCity() == null) {
                order.setBillingCity("Sin ciudad");
            }
            if (order.getBillingPostalCode() == null) {
                order.setBillingPostalCode("00000");
            }
            if (order.getBillingCountry() == null) {
                order.setBillingCountry("Sin país");
            }

            if (order.getShippingName() == null) {
                order.setShippingName(order.getBillingName());
            }
            if (order.getShippingStreet() == null) {
                order.setShippingStreet(order.getBillingStreet());
            }
            if (order.getShippingCity() == null) {
                order.setShippingCity(order.getBillingCity());
            }
            if (order.getShippingPostalCode() == null) {
                order.setShippingPostalCode(order.getBillingPostalCode());
            }
            if (order.getShippingCountry() == null) {
                order.setShippingCountry(order.getBillingCountry());
            }


            String sql = """
                INSERT INTO customer_order (
                  order_number, created_at, status,
                  gross_total, discount_total, final_total, coupon_id,
                  payment_method, payment_status, payment_details,
                  billing_name, billing_tax_id, billing_street, billing_city,
                  billing_postal_code, billing_country,
                  shipping_name, shipping_street, shipping_city,
                  shipping_postal_code, shipping_country
                ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                """;

            KeyHolder kh = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                CustomerOrder.PaymentMethod pm =
                        order.getPaymentMethod() != null
                                ? order.getPaymentMethod()
                                : CustomerOrder.PaymentMethod.CREDIT_CARD;

                CustomerOrder.PaymentStatus psStatus =
                        order.getPaymentStatus() != null
                                ? order.getPaymentStatus()
                                : CustomerOrder.PaymentStatus.PENDING;

                ps.setString(1, order.getOrderNumber());
                ps.setTimestamp(2, Timestamp.valueOf(order.getCreatedAt()));
                ps.setString(3, order.getStatus().name().toLowerCase());
                ps.setBigDecimal(4, order.getGrossTotal());
                ps.setBigDecimal(5, order.getDiscountTotal());
                ps.setBigDecimal(6, order.getFinalTotal());
                if (order.getCouponId() != null) {
                    ps.setInt(7, order.getCouponId());
                } else {
                    ps.setNull(7, Types.INTEGER);
                }
                ps.setString(8, pm.name().toLowerCase());        // CORREGIDO
                ps.setString(9, psStatus.name().toLowerCase());  // CORREGIDO
                ps.setString(10, order.getPaymentDetails());
                ps.setString(11, order.getBillingName());
                ps.setString(12, order.getBillingTaxId());
                ps.setString(13, order.getBillingStreet());
                ps.setString(14, order.getBillingCity());
                ps.setString(15, order.getBillingPostalCode());
                ps.setString(16, order.getBillingCountry());
                ps.setString(17, order.getShippingName());
                ps.setString(18, order.getShippingStreet());
                ps.setString(19, order.getShippingCity());
                ps.setString(20, order.getShippingPostalCode());
                ps.setString(21, order.getShippingCountry());
                return ps;
            }, kh);

            order.setId(kh.getKey().intValue());

        } else {
            CustomerOrder.PaymentMethod pm =
                    order.getPaymentMethod() != null
                            ? order.getPaymentMethod()
                            : CustomerOrder.PaymentMethod.CREDIT_CARD;

            CustomerOrder.PaymentStatus psStatus =
                    order.getPaymentStatus() != null
                            ? order.getPaymentStatus()
                            : CustomerOrder.PaymentStatus.PENDING;

            String sql = """
        UPDATE customer_order
        SET gross_total = ?, discount_total = ?, final_total = ?, coupon_id = ?,
            payment_method = ?, payment_status = ?, payment_details = ?
        WHERE id = ?
        """;
            jdbc.update(sql,
                    order.getGrossTotal(),
                    order.getDiscountTotal(),
                    order.getFinalTotal(),
                    order.getCouponId(),
                    pm.name().toLowerCase(),          // CORREGIDO
                    psStatus.name().toLowerCase(),    // CORREGIDO
                    order.getPaymentDetails(),
                    order.getId());
        }
        return order;
    }

    public Optional<CustomerOrder> findById(int id) {
        String sql = "SELECT * FROM customer_order WHERE id = ?";
        return jdbc.query(sql, mapper, id).stream().findFirst();
    }
}

