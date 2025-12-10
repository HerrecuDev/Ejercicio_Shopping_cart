-- =====================================================================
-- SHOPPING CART DATABASE SCHEMA DOCUMENTATION
-- =====================================================================

-- TABLE: order_item
-- DESCRIPTION: Stores individual line items within customer orders.
--              Each record represents a product added to an order with
--              pricing and quantity information.
-- COLUMNS:
--   - id: Unique identifier for the order item (Primary Key, Auto-increment)
--   - product_name: Name of the product ordered (VARCHAR 255)
--   - unit_price: Price per unit of the product (DECIMAL 10,2)
--   - quantity: Number of units ordered (INT)
--   - line_total: Total price for this line item (DECIMAL 10,2)
--   - order_id: Reference to the parent order (Foreign Key → customer_order.id)
--   - product_id: Reference to the product catalog (Foreign Key → product.id)
-- RELATIONSHIPS: Belongs to customer_order and product tables

-- TABLE: customer_order
-- DESCRIPTION: Represents a complete customer order/purchase transaction.
--              Contains order summary, billing/shipping addresses, payment
--              information, and coupon/discount details.
-- COLUMNS:
--   - id: Unique identifier for the order (Primary Key, Auto-increment)
--   - order_number: Unique order reference number (VARCHAR 100, UNIQUE)
--   - create_ad: Timestamp when order was created (DATETIME, defaults to CURRENT_TIMESTAMP)
--   - status: Current order status (VARCHAR 50, defaults to 'pending')
--   - gross_total: Total price before discounts (DECIMAL 10,2)
--   - discount_total: Total discount amount applied (DECIMAL 10,2)
--   - final_total: Final price after discounts (DECIMAL 10,2)
--   - coupon_id: Applied coupon reference (Foreign Key → coupon.id, nullable)
--   - payment_method: Payment method used (VARCHAR 100)
--   - payment_status: Payment processing status (VARCHAR 50)
--   - payment_details: Additional payment information (TEXT)
--   - billing_name: Customer name for billing (VARCHAR 255)
--   - billing_tax_id: Tax/VAT identifier (VARCHAR 100)
--   - billing_street: Billing address street (VARCHAR 255)
--   - billing_city: Billing address city (VARCHAR 100)
--   - billing_postal_code: Billing address postal code (VARCHAR 20)
--   - billing_country: Billing address country (VARCHAR 100)
--   - shipping_name: Recipient name for shipping (VARCHAR 255)
--   - shiping_street: Shipping address street (VARCHAR 255)
--   - shipping_city: Shipping address city (VARCHAR 100)
--   - shipping_postal_cod: Shipping address postal code (VARCHAR 20)
--   - shipping_country: Shipping address country (VARCHAR 100)
-- RELATIONSHIPS: Contains many order_items and references one coupon

-- TABLE: coupon
-- DESCRIPTION: Stores discount coupon/promotional code information.
--              Defines available coupons with validity periods and
--              discount configurations.
-- COLUMNS:
--   - id: Unique identifier for the coupon (Primary Key, Auto-increment)
--   - code: Promotional code entered by customers (VARCHAR 100, UNIQUE)
--   - description: Human-readable coupon description (TEXT)
--   - discount_type: Type of discount (e.g., 'percentage', 'fixed_amount')
--   - discount_value: Discount amount or percentage value (DECIMAL 10,2)
--   - active: Flag indicating if coupon is currently usable (BOOLEAN, defaults to TRUE)
--   - valid_from: Start date/time of coupon validity (DATETIME)
--   - valid_to: End date/time of coupon validity (DATETIME)
-- RELATIONSHIPS: Referenced by customer_order table

-- NOTES:
--   - All tables use InnoDB engine with UTF8MB4 charset
--   - order_item has missing ENGINE specification (should match customer_order)
--   - Potential typos detected: 'shiping_street', 'shipping_postal_cod'
-- =====================================================================
use shopping_cart;

CREATE TABLE IF NOT EXISTS order_item (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(255) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    line_total DECIMAL(10, 2) NOT NULL,
    order_id INT NOT NULL,
    product_id INT NOT NULL,  -- Added product_id as a foreign key
    FOREIGN KEY (order_id) REFERENCES customer_order(id),
    FOREIGN KEY (product_id) REFERENCES product(id)  -- Assuming there is a product table
);

CREATE TABLE IF NOT EXISTS customer_order (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(100) NOT NULL UNIQUE,
    create_ad DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    gross_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    final_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    coupon_id INT,
    payment_method VARCHAR(100),
    payment_status VARCHAR(50),
    payment_details TEXT,
    billing_name VARCHAR(255),
    billing_tax_id VARCHAR(100),
    billing_street VARCHAR(255),
    billing_city VARCHAR(100),
    billing_postal_code VARCHAR(20),
    billing_country VARCHAR(100),
    shipping_name VARCHAR(255),
    shiping_street VARCHAR(255),
    shipping_city VARCHAR(100),
    shipping_postal_cod VARCHAR(20),
    shipping_country VARCHAR(100),
    FOREIGN KEY (coupon_id) REFERENCES coupon(id) ON DELETE SET NULL
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