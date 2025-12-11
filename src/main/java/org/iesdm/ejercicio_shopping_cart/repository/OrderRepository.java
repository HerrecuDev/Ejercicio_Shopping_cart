package org.iesdm.ejercicio_shopping_cart.repository;

import lombok.RequiredArgsConstructor;
import org.iesdm.ejercicio_shopping_cart.model.Order_item;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository

@RequiredArgsConstructor
public class OrderRepository {

    private final JdbcTemplate jdbc;

    private RowMapper<Order_item> mapper = (rs, rowNum) -> Order_item.builder()
            .id(rs.getInt("id"))
            .orderId(rs.getInt("orderId"))
            .productName(rs.getString("productName"))
            .unitPrice(rs.getBigDecimal("unitPrice"))
            .quantity(rs.getInt("quantity"))
            .lineTotal(rs.getBigDecimal("lineTotal"))
            .build();


    public List<Order_item> findByOrderId(int orderId) {
        String sql = "SELECT * FROM order_item WHERE orderId = ?";
        return jdbc.query(sql, mapper, orderId);
    }


    public Optional<Order_item> findById(int id) {
        String sql = "SELECT * FROM order_item WHERE id = ?";
        return jdbc.query(sql, mapper, id).stream().findFirst();
    }


    public Order_item save(Order_item item) {
        if (item.getId() == null) {
            String sql = "INSERT INTO order_item (orderId, productName, unitPrice, quantity, lineTotal)" +
                    " VALUES (?,?,?,?,?)";
            KeyHolder kh = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, item.getOrderId());
                ps.setString(2, item.getProductName());
                ps.setBigDecimal(3, item.getUnitPrice());
                ps.setInt(4, item.getQuantity());
                ps.setBigDecimal(5, item.getLineTotal());
                return ps;
            }, kh);
            item.setId(kh.getKey().intValue());
        } else {
            String sql = "UPDATE order_item SET quantity=?, lineTotal=? WHERE id=?";
            jdbc.update(sql, item.getQuantity(), item.getLineTotal(), item.getId());
        }
        return item;
    }

    public void deleteById(int id) {
        jdbc.update("DELETE FROM order_item WHERE id = ?", id);
    }
}
