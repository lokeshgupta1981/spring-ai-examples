DROP TABLE IF EXISTS orders;

CREATE TABLE orders (
    order_id      VARCHAR(10) PRIMARY KEY,
    customer_name VARCHAR(50) NOT NULL,
    city          VARCHAR(30) NOT NULL,
    status        VARCHAR(20) NOT NULL,
    total_inr     DECIMAL(10, 2) NOT NULL,
    placed_on     DATE NOT NULL,
    delivered_on  DATE
);
