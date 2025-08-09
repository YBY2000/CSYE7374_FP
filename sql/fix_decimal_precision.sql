-- Fix decimal precision for orders table
-- Change total field from decimal(10,0) to decimal(10,2) to support 2 decimal places

USE canteen;

-- Modify the total column to support 2 decimal places
ALTER TABLE orders MODIFY COLUMN total decimal(10,2) COMMENT 'Total price with 2 decimal places';

-- Verify the change
DESCRIBE orders;

-- Show some recent orders to verify the change
SELECT id, content, total, time, status FROM orders ORDER BY id DESC LIMIT 5; 