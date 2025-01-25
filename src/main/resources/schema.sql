drop table if exists parts_sku;
drop table if exists parts_individual;
drop table if exists purchase_order;
drop table if exists po_items;
drop table if exists notes;
drop table if exists devices;
drop table if exists customers;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE customers (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           customer_name VARCHAR(255) NOT NULL,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           phone_number VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE devices (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         device_name VARCHAR(255) NOT NULL,
                         device_num VARCHAR(255) NOT NULL UNIQUE,
                         description VARCHAR(255),
                         status VARCHAR(255),
                         customer_id INT NOT NULL,
                         timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

CREATE TABLE notes (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       note TEXT NOT NULL,
                       timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       device_id INT NOT NULL,
                       created_by INT NOT NULL,
                       FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE,
                       FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE parts_sku (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           part_name VARCHAR(255) NOT NULL,
                           quantity INT NOT NULL
);

CREATE TABLE parts_individual (
                                  id INT PRIMARY KEY AUTO_INCREMENT,
                                  part_sku_id INT NOT NULL,
                                  price DECIMAL(10, 2) NOT NULL,
                                  device_id INT NOT NULL,
                                  FOREIGN KEY (part_sku_id) REFERENCES parts_sku(id) ON DELETE CASCADE,
                                  FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE
);

CREATE TABLE purchase_order (
                                id INT PRIMARY KEY AUTO_INCREMENT,
                                total_price DECIMAL(10, 2),
                                status VARCHAR(255),
                                timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                retailer VARCHAR(255),
                                order_no VARCHAR(255) UNIQUE
);

CREATE TABLE po_items (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          part_sku_id INT NOT NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          quantity INT NOT NULL,
                          po_id INT NOT NULL,
                          FOREIGN KEY (part_sku_id) REFERENCES parts_sku(id) ON DELETE CASCADE,
                          FOREIGN KEY (po_id) REFERENCES purchase_order(id) ON DELETE CASCADE
);