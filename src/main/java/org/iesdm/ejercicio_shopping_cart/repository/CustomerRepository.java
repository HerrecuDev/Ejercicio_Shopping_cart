package org.iesdm.ejercicio_shopping_cart.repository;

import org.iesdm.ejercicio_shopping_cart.model.customer_order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbc;

    public CustomerRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }


    public customer_order save(customer_order order) {
        if (order.getId() == null) {
            String sql = "INSERT INTO customer_order (orderNumber, createdAt, status," +
                    " grossTotal, discountTotal, finalTotal, couponId) VALUES (?,?,?,?,?,?,?)";
            KeyHolder kh = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, order.getOrderNumber());
                ps.setTimestamp(2, Timestamp.valueOf(order.getCreatedAt()));
                ps.setString(3, order.getStatus().name());
                ps.setBigDecimal(4, order.getGrossTotal());
                ps.setBigDecimal(5, order.getDiscountTotal());
                ps.setBigDecimal(6, order.getFinalTotal());
                ps.setObject(7, order.getCouponId());
                return ps;
            }, kh);
            order.setId(kh.getKey().intValue());
        } else {
            String sql = "UPDATE customer_order SET grossTotal=?, discountTotal=?, finalTotal=?, couponId=? WHERE id=?";
            jdbc.update(sql, order.getGrossTotal(), order.getDiscountTotal(),
                    order.getFinalTotal(), order.getCouponId(), order.getId());
        }
        return order;
    }


    public Optional<customer_order> findById(int id) {
        String sql = "SELECT * FROM customer_order WHERE id = ?";
        RowMapper<customer_order> mapper = (rs, rowNum) -> customer_order.builder()
                .id(rs.getInt("id"))
                .orderNumber(rs.getString("orderNumber"))
                .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
                .status(customer_order.Status.valueOf(rs.getString("status")))
                .grossTotal(rs.getBigDecimal("grossTotal"))
                .discountTotal(rs.getBigDecimal("discountTotal"))
                .finalTotal(rs.getBigDecimal("finalTotal"))
                .couponId((Integer) rs.getObject("couponId"))
                .build();
        return jdbc.query(sql, mapper, id).stream().findFirst();
    }
}
