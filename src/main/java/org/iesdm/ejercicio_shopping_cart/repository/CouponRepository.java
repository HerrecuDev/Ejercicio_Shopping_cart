package org.iesdm.ejercicio_shopping_cart.repository;

import lombok.RequiredArgsConstructor;
import org.iesdm.ejercicio_shopping_cart.model.Coupon;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<Coupon> mapper = (rs, rowNum) -> {
        Timestamp fromTs = rs.getTimestamp("valid_from");
        Timestamp toTs   = rs.getTimestamp("valid_to");

        return Coupon.builder()
                .id(rs.getInt("id"))
                .code(rs.getString("code"))
                .description(rs.getString("description"))
                .discountType(rs.getString("discount_type"))
                .discountValue(rs.getDouble("discount_value"))
                .active(rs.getBoolean("active"))
                .validFrom(fromTs != null ? fromTs.toLocalDateTime() : null)
                .validTo(toTs != null ? toTs.toLocalDateTime() : null)
                .build();
    };

    public Optional<Coupon> findById(int id) {
        String sql = "SELECT * FROM coupon WHERE id = ?";
        return jdbc.query(sql, mapper, id).stream().findFirst();
    }

    public Optional<Coupon> findByCode(String code) {
        String sql = "SELECT * FROM coupon WHERE code = ?";
        return jdbc.query(sql, mapper, code).stream().findFirst();
    }
}