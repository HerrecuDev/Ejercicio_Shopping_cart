package org.iesdm.ejercicio_shopping_cart.repository;

import lombok.RequiredArgsConstructor;
import org.iesdm.ejercicio_shopping_cart.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class OrderItemRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<OrderItem> mapper = (rs, rowNum) -> OrderItem.builder()
            .id(rs.getInt("id"))
            .order_Id(rs.getInt("order_id"))
            .product_Id(rs.getInt("product_id"))
            .productName(rs.getString("product_name"))
            .unitPrice(rs.getBigDecimal("unit_price"))
            .quantity(rs.getInt("quantity"))
            .lineTotal(rs.getBigDecimal("line_total"))
            .build();

    public List<OrderItem> findByOrderId(int orderId) {
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        return jdbc.query(sql, mapper, orderId);
    }

    public Optional<OrderItem> findById(int id) {
        String sql = "SELECT * FROM order_item WHERE id = ?";
        return jdbc.query(sql, mapper, id).stream().findFirst();
    }

    public OrderItem save(OrderItem item) {
        if (item.getId() == null) {
            String sql = "INSERT INTO order_item " +
                    "(order_id, product_id, product_name, unit_price, quantity, line_total) " +
                    "VALUES (?,?,?,?,?,?)";
            KeyHolder kh = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, item.getOrder_Id());
                if (item.getProduct_Id() != null) {
                    ps.setInt(2, item.getProduct_Id());
                } else {
                    ps.setNull(2, Types.INTEGER);
                }
                ps.setString(3, item.getProductName());
                ps.setBigDecimal(4, item.getUnitPrice());
                ps.setInt(5, item.getQuantity());
                ps.setBigDecimal(6, item.getLineTotal());
                return ps;
            }, kh);
            item.setId(kh.getKey().intValue());
        } else {
            String sql = "UPDATE order_item SET quantity = ?, line_total = ? WHERE id = ?";
            jdbc.update(sql, item.getQuantity(), item.getLineTotal(), item.getId());
        }
        return item;
    }

    public void deleteById(int id) {
        jdbc.update("DELETE FROM order_item WHERE id = ?", id);
    }
}