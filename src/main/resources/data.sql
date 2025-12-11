-- Insert sample data into product table
INSERT INTO product (id, name, price, description) VALUES
(1, 'Laptop', 999.99, 'High performance laptop'),
(2, 'Mouse', 29.99, 'Wireless mouse'),
(3, 'Keyboard', 79.99, 'Mechanical keyboard'),
(4, 'Monitor', 299.99, '4K Monitor');

-- Insert sample data into coupon table
INSERT INTO coupon (id, code, discount_type, valid_to) VALUES
(1, 'SAVE10', 10.0, '2025-12-31'),
(2, 'SUMMER20', 20.0, '2025-06-30');

-- Insert sample data into customer_order table
INSERT INTO customer_order (id, shipping_name, created_at, final_total, coupon_id) VALUES
(1, 'john@example.com', '2025-01-15', 1099.98, 1),
(2, 'maria@example.com', '2025-01-20', 379.98, 2);

-- Insert sample data into order_item table
INSERT INTO order_item (id, order_id, quantity, unit_price) VALUES
(1, 1, 1, 1, 999.99),
(2, 1, 2, 1, 29.99),
(3, 2, 3, 1, 79.99),
(4, 2, 4, 1, 299.99);