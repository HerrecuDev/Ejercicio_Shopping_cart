package org.iesdm.ejercicio_shopping_cart.repository;

import org.iesdm.ejercicio_shopping_cart.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

        private final JdbcTemplate jdbc;

        public ProductRepository(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }



    private RowMapper<Product> mapper = (rs, rowNum) -> Product.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .price(rs.getBigDecimal("price"))
                .description(rs.getString("description"))
                .active(rs.getBoolean("active"))
                .build();



        public Optional<Product> findById(int id) {
            String sql = "SELECT * FROM product WHERE id = ?";
            return jdbc.query(sql, mapper, id).stream().findFirst();
        }


    public List<Product> findAll() {
        String sql = "SELECT * FROM product";
        return jdbc.query(sql, mapper);
    }


}
