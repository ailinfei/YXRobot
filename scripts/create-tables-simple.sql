USE YXRobot;

DROP TABLE IF EXISTS product_media;
DROP TABLE IF EXISTS products;

CREATE TABLE products (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  model VARCHAR(100) NOT NULL,
  description LONGTEXT,
  price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  cover_image_url VARCHAR(500) DEFAULT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'draft',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_model (model),
  KEY idx_status (status),
  KEY idx_price (price),
  KEY idx_created_at (created_at),
  KEY idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_media (
  id BIGINT NOT NULL AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  media_type VARCHAR(20) NOT NULL,
  media_url VARCHAR(500) NOT NULL,
  sort_order INT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_product_id (product_id),
  KEY idx_media_type (media_type),
  KEY idx_sort_order (sort_order),
  KEY idx_is_deleted (is_deleted),
  CONSTRAINT fk_product_media_product_id FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO products (name, model, description, price, cover_image_url, status, created_at, updated_at, is_deleted) VALUES
('Home Robot', 'YX-HOME-001', 'Smart writing robot for home use', 2999.00, 'https://via.placeholder.com/300x200/4CAF50/FFFFFF?text=Home', 'published', '2024-01-15 10:30:00', '2024-01-20 14:20:00', 0),
('Business Robot', 'YX-BUSINESS-001', 'High-performance writing robot for business', 8999.00, 'https://via.placeholder.com/300x200/2196F3/FFFFFF?text=Business', 'published', '2024-01-10 09:15:00', '2024-01-18 16:45:00', 0),
('Education Robot', 'YX-EDU-001', 'Writing robot designed for educational institutions', 5999.00, 'https://via.placeholder.com/300x200/FF9800/FFFFFF?text=Education', 'published', '2024-01-12 11:20:00', '2024-01-19 13:30:00', 0),
('Portable Robot', 'YX-PORTABLE-001', 'Lightweight and portable writing robot', 1999.00, 'https://via.placeholder.com/300x200/9C27B0/FFFFFF?text=Portable', 'draft', '2024-01-20 15:45:00', '2024-01-20 15:45:00', 0),
('Professional Robot', 'YX-PRO-001', 'Professional writing robot with advanced AI', 12999.00, 'https://via.placeholder.com/300x200/F44336/FFFFFF?text=Professional', 'archived', '2024-01-08 08:30:00', '2024-01-25 10:15:00', 0);

INSERT INTO product_media (product_id, media_type, media_url, sort_order, created_at, updated_at) VALUES
(1, 'image', 'https://via.placeholder.com/800x600/4CAF50/FFFFFF?text=YX-HOME-001-Image1', 1, NOW(), NOW()),
(1, 'image', 'https://via.placeholder.com/800x600/4CAF50/FFFFFF?text=YX-HOME-001-Image2', 2, NOW(), NOW()),
(1, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1, NOW(), NOW()),
(2, 'image', 'https://via.placeholder.com/800x600/2196F3/FFFFFF?text=YX-BUSINESS-001-Image1', 1, NOW(), NOW()),
(2, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1, NOW(), NOW()),
(3, 'image', 'https://via.placeholder.com/800x600/FF9800/FFFFFF?text=YX-EDU-001-Image1', 1, NOW(), NOW()),
(3, 'image', 'https://via.placeholder.com/800x600/FF9800/FFFFFF?text=YX-EDU-001-Image2', 2, NOW(), NOW()),
(4, 'image', 'https://via.placeholder.com/800x600/9C27B0/FFFFFF?text=YX-PORTABLE-001-Image1', 1, NOW(), NOW()),
(5, 'image', 'https://via.placeholder.com/800x600/F44336/FFFFFF?text=YX-PRO-001-Image1', 1, NOW(), NOW()),
(5, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1, NOW(), NOW());

SELECT 'Database initialization completed!' as message;
SELECT COUNT(*) as product_count FROM products WHERE is_deleted = 0;
SELECT COUNT(*) as media_count FROM product_media WHERE is_deleted = 0;