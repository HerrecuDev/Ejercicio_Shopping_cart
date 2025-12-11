package org.iesdm.ejercicio_shopping_cart.repository;

import lombok.RequiredArgsConstructor;
import org.iesdm.ejercicio_shopping_cart.model.Coupon;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository


/*Para que no sea necesario inyectar el contructor*/

@RequiredArgsConstructor


public class CouponRepository {

    private final JdbcTemplate jdbc;

    private RowMapper<Coupon> mapper = (rs, rowNum) -> Coupon.builder()
            .id(rs.getInt("id"))
            .code(rs.getString("code"))
            .description(rs.getString("description"))
            .discountType(rs.getString("discountType"))
            .discountValue(rs.getDouble("discountValue"))
            .active(rs.getBoolean("active"))
            .validFrom(rs.getTimestamp("validFrom").toLocalDateTime())
            .validTo(rs.getTimestamp("validTo").toLocalDateTime())
            .build();


    public Optional<Coupon> findById(int id) {
        String sql = "SELECT * FROM coupon WHERE id = ?";
        return jdbc.query(sql, mapper, id).stream().findFirst();
    }


}
