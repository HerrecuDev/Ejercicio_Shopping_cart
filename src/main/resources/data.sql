
-- Insert para Product

INSERT INTO product (name, price, description, active) VALUES
   ('Laptop Gamer', 1499.99, 'Laptop gamer 15" con GPU dedicada', TRUE),
   ('Smartphone Pro', 899.50, 'Smartphone gama alta 128GB', TRUE),
   ('Auriculares Bluetooth', 79.90, 'Auriculares inalámbricos con cancelación de ruido', TRUE),
   ('Teclado Mecánico', 59.99, 'Teclado mecánico retroiluminado', TRUE),
   ('Monitor 27"', 249.00, 'Monitor 27 pulgadas 144Hz', TRUE),
   ('Mouse Inalámbrico', 29.99, 'Mouse óptico inalámbrico', TRUE),
   ('Impresora Multifunción', 129.00, 'Impresora y escáner WiFi', TRUE),
   ('Silla Ergonómica', 199.00, 'Silla de oficina ergonómica', TRUE),
   ('Disco SSD 1TB', 110.00, 'Unidad SSD 1TB SATA', TRUE),
   ('Cable HDMI 2m', 9.99, 'Cable HDMI alta velocidad 2 metros', TRUE);


-- Inserts para Coupon


INSERT INTO coupon (code, description, discount_type, discount_value, active, valid_from, valid_to) VALUES
    ('WELCOME10', 'Descuento de bienvenida 10%', 'percentage', 10.00, TRUE, '2025-01-01 00:00:00', '2025-12-31 23:59:59'),
    ('FREESHIP', 'Envío gratis equivalente a 15', 'fixed', 15.00, TRUE, '2025-01-01 00:00:00', '2025-06-30 23:59:59'),
    ('BLACKFRIDAY', 'Descuento 20% Black Friday', 'percentage', 20.00, TRUE, '2025-11-20 00:00:00', '2025-11-30 23:59:59'),
    ('SUMMER5', 'Descuento verano 5%', 'percentage', 5.00, TRUE, '2025-06-01 00:00:00', '2025-08-31 23:59:59'),
    ('EXPIRED15', 'Cupón expirado 15%', 'percentage', 15.00, FALSE, '2024-01-01 00:00:00', '2024-12-31 23:59:59');



-- Insert para customer_order
INSERT INTO customer_order (
    order_number, status, gross_total, discount_total, final_total,
    coupon_id, payment_method, payment_status, payment_details,
    billing_name, billing_tax_id, billing_street, billing_city,
    billing_postal_code, billing_country,
    shipping_name, shipping_street, shipping_city,
    shipping_postal_code, shipping_country
) VALUES (
             'ORD-TEST-001', 'completed', 100.00, 0.00, 100.00,
             NULL, 'credit_card', 'completed', 'Prueba sin cupón',
             'Cliente Test', NULL, 'Calle Test 1', 'Madrid',
             '28000', 'España',
             'Cliente Test', 'Calle Test 1', 'Madrid',
             '28000', 'España'
         );


-- Insert para order_item


-- Pedido 1 (ORD-20250001)
INSERT INTO order_item (order_id, product_name, unit_price, quantity, line_total) VALUES
      (1, 'Laptop Gamer', 1499.99, 1, 1499.99),
      (1, 'Cable HDMI 2m', 9.90, 1, 9.90),
      (1, 'Mouse Inalámbrico', 29.99, 2, 59.98);

-- Pedido 2 (ORD-20250002)
INSERT INTO order_item (order_id, product_name, unit_price, quantity, line_total) VALUES
      (2, 'Auriculares Bluetooth', 79.90, 2, 159.80),
      (2, 'Teclado Mecánico', 59.99, 3, 179.97);

-- Pedido 3 (ORD-20250003)
INSERT INTO order_item (order_id, product_name, unit_price, quantity, line_total) VALUES
      (3, 'Monitor 27"', 249.00, 1, 249.00),
      (3, 'Cable HDMI 2m', 9.99, 4, 39.96);

-- Pedido 4 (ORD-20250004)
INSERT INTO order_item (order_id, product_name, unit_price, quantity, line_total) VALUES
    (4, 'Silla Ergonómica', 199.00, 1, 199.00);

-- Pedido 5 (ORD-20250005)
INSERT INTO order_item (order_id, product_name, unit_price, quantity, line_total) VALUES
    (5, 'Disco SSD 1TB', 110.00, 1, 110.00);


