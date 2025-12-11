
CREATE DATABASE IF NOT EXISTS shopping_cart;
use shopping_cart;


CREATE TABLE IF NOT EXISTS product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS coupon (
                                      id INT PRIMARY KEY AUTO_INCREMENT,
                                      code VARCHAR(100) NOT NULL UNIQUE,
                                      description TEXT,
                                      discount_type VARCHAR(50) NOT NULL,
                                      discount_value DECIMAL(10, 2) NOT NULL,
                                      active BOOLEAN NOT NULL DEFAULT TRUE,
                                      valid_from DATETIME,
                                      valid_to DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



CREATE TABLE IF NOT EXISTS customer_order (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('pending', 'completed', 'canceled', 'refunded') NOT NULL DEFAULT 'pending',
    gross_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    final_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    coupon_id INT,
    payment_method ENUM('credit_card', 'paypal', 'bank_transfer') NOT NULL,
    payment_status ENUM('pending', 'completed', 'failed') NOT NULL DEFAULT 'pending',
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
    FOREIGN KEY (coupon_id) REFERENCES coupon(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS order_item (
                                          id INT PRIMARY KEY AUTO_INCREMENT,
                                          order_id INT NOT NULL,
                                          product_name VARCHAR(255) NOT NULL,
                                          unit_price DECIMAL(10, 2) NOT NULL,
                                          quantity INT NOT NULL,
                                          line_total DECIMAL(10, 2) NOT NULL,
                                          FOREIGN KEY (order_id) REFERENCES customer_order(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


