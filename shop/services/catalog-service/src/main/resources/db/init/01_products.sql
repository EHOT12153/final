-- Полная пересборка демо-данных каталога
TRUNCATE TABLE products RESTART IDENTITY CASCADE;

INSERT INTO products (sku, name, price_cents)
VALUES
    -- Смартфоны
    ('PHONE-PIXEL-8-128',      'Google Pixel 8 128GB',                 79900),
    ('PHONE-IP15-128',         'Apple iPhone 15 128GB',                99900),
    ('PHONE-S23-256',          'Samsung Galaxy S23 256GB',             89900),

    -- Ноутбуки
    ('LAPTOP-MBA-M2-13',       'MacBook Air 13" M2 16GB/512GB',       129900),
    ('LAPTOP-TP-X1C-G11',      'Lenovo ThinkPad X1 Carbon Gen 11',    149900),
    ('LAPTOP-LEGION-5-15',     'Lenovo Legion 5 15"',                 119900),

    -- Наушники
    ('HEADPHONES-SONY-XM5',    'Sony WH-1000XM5',                       39900),
    ('HEADPHONES-APP-APRO2',   'Apple AirPods Pro 2',                   27900),
    ('HEADPHONES-BOS-QC45',    'Bose QuietComfort 45',                  34900),

    -- Мониторы
    ('MONITOR-DELL-U2723',     'Dell UltraSharp 27" 4K',                54900),
    ('MONITOR-LG-27GN850',     'LG 27" QHD 165Hz',                      37900),
    ('MONITOR-SAM-ODYSSEY-G7', 'Samsung Odyssey G7 32"',                69900);
