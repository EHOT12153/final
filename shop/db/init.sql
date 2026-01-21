-- PRODUCTS
CREATE TABLE IF NOT EXISTS products (
    id           BIGSERIAL PRIMARY KEY,
    sku          VARCHAR(100) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    price_cents  INTEGER      NOT NULL
);

INSERT INTO products (sku, name, description, price_cents)
VALUES
  ('LAPTOP-15-PRO', 'Laptop Pro 15', 'Мощный ноутбук для разработчиков', 150000),
  ('KEYB-MECH-RGB', 'Mechanical Keyboard', 'Механическая клавиатура с подсветкой', 7000),
  ('MOUSE-GAME',    'Gaming Mouse', 'Игровая мышь с высоким DPI', 4500),
  ('MON-27-4K',     '4K Monitor 27"', 'Монитор 27 дюймов, 4K-разрешение', 35000),
  ('HUB-USBC-8IN1', 'USB-C Hub 8-in-1', 'Хаб с USB-C, HDMI и USB-A', 5000)
ON CONFLICT DO NOTHING;

-- ORDERS
CREATE TABLE IF NOT EXISTS orders (
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT NOT NULL,
    total_price_cents  BIGINT NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);

CREATE TABLE IF NOT EXISTS order_items (
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id  BIGINT NOT NULL,
    quantity    INTEGER NOT NULL,
    price_cents INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
