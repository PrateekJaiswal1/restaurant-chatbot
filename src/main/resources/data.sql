-- Sample daily specials (update date to today's date when testing)
-- Format: YYYY-MM-DD
INSERT INTO daily_special (id, date, item_name, description, price) VALUES
  (1, CURRENT_DATE, 'Truffle Pizza',       'White pizza with black truffle and mozzarella', 22.00),
  (2, CURRENT_DATE, 'Lobster Linguine',    'Fresh lobster with garlic butter sauce',        28.00),
  (3, CURRENT_DATE, 'Cannoli del Giorno',  'Chef''s special ricotta cannoli',                7.00);
