INIT_DIR=../services/catalog-service/src/main/resources/db/init

# перезаписываем файл
cat > "$INIT_DIR/01_products.sql" <<'SQL'
\set ON_ERROR_STOP on

CREATE TABLE IF NOT EXISTS products (
  id BIGSERIAL PRIMARY KEY,
  sku TEXT UNIQUE NOT NULL,
  name TEXT NOT NULL,
  price_cents INTEGER NOT NULL CHECK (price_cents >= 0),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO products (sku, name, price_cents) VALUES
('SKU-001','Demo product A', 1999),
('SKU-002','Demo product B', 2999)
ON CONFLICT DO NOTHING;
SQL

# на всякий случай убрать CRLF/BOM
sed -i 's/\r$//' "$INIT_DIR/01_products.sql"
