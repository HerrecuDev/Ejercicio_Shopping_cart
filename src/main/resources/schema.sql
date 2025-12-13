CREATE DATABASE IF NOT EXISTS shopping_cart
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE shopping_cart;

-- Productos
CREATE TABLE IF NOT EXISTS product (
                                       id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                       name VARCHAR(255) NOT NULL,
                                       price DECIMAL(10, 2) NOT NULL,
                                       description TEXT,
                                       active TINYINT(1) NOT NULL DEFAULT 1,
                                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                           ON UPDATE CURRENT_TIMESTAMP,
                                       INDEX idx_product_active (active),
                                       INDEX idx_product_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Cupones de descuento
CREATE TABLE IF NOT EXISTS coupon (
                                      id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                      code VARCHAR(100) NOT NULL UNIQUE,
                                      description TEXT,
                                      discount_type ENUM('percentage','fixed') NOT NULL,
                                      discount_value DECIMAL(10, 2) NOT NULL,
                                      active TINYINT(1) NOT NULL DEFAULT 1,
                                      valid_from DATETIME,
                                      valid_to DATETIME,
                                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                          ON UPDATE CURRENT_TIMESTAMP,
                                      INDEX idx_coupon_active (active),
                                      INDEX idx_coupon_valid_range (valid_from, valid_to)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Pedidos
CREATE TABLE IF NOT EXISTS customer_order (
                                              id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                              order_number VARCHAR(100) NOT NULL UNIQUE,
                                              created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                                  ON UPDATE CURRENT_TIMESTAMP,
                                              status ENUM('pending', 'completed', 'canceled', 'refunded')
                                                                        NOT NULL DEFAULT 'pending',
                                              gross_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                                              discount_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                                              final_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                                              coupon_id INT UNSIGNED NULL,
                                              payment_method ENUM('credit_card', 'paypal', 'bank_transfer') NOT NULL,
                                              payment_status ENUM('pending', 'completed', 'failed')
                                                  NOT NULL DEFAULT 'pending',
                                              payment_details TEXT,
                                              billing_name VARCHAR(255) NOT NULL,
                                              billing_tax_id VARCHAR(100),
                                              billing_street VARCHAR(255) NOT NULL,
                                              billing_city VARCHAR(100) NOT NULL,
                                              billing_postal_code VARCHAR(20) NOT NULL,
                                              billing_country VARCHAR(100) NOT NULL,
                                              shipping_name VARCHAR(255) NOT NULL,
                                              shipping_street VARCHAR(255) NOT NULL,
                                              shipping_city VARCHAR(100) NOT NULL,
                                              shipping_postal_code VARCHAR(20) NOT NULL,
                                              shipping_country VARCHAR(100) NOT NULL,
                                              CONSTRAINT fk_customer_order_coupon
                                                  FOREIGN KEY (coupon_id)
                                                      REFERENCES coupon(id)
                                                      ON DELETE SET NULL,
                                              INDEX idx_customer_order_status (status),
                                              INDEX idx_customer_order_created_at (created_at),
                                              INDEX idx_customer_order_coupon (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Líneas de pedido
CREATE TABLE IF NOT EXISTS order_item (
                                          id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                          order_id INT UNSIGNED NOT NULL,
                                          product_id INT UNSIGNED NULL,
                                          product_name VARCHAR(255) NOT NULL,
                                          unit_price DECIMAL(10, 2) NOT NULL,
                                          quantity INT NOT NULL,
                                          line_total DECIMAL(10, 2) NOT NULL,
                                          CONSTRAINT fk_order_item_order
                                              FOREIGN KEY (order_id)
                                                  REFERENCES customer_order(id)
                                                  ON DELETE CASCADE,
                                          CONSTRAINT fk_order_item_product
                                              FOREIGN KEY (product_id)
                                                  REFERENCES product(id)
                                                  ON DELETE SET NULL,
                                          INDEX idx_order_item_order (order_id),
                                          INDEX idx_order_item_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
