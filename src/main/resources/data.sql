-- Sample daily specials (update date to today's date when testing)
-- Format: YYYY-MM-DD
INSERT INTO daily_special (id, date, item_name, description, price) VALUES
  (1, CURRENT_DATE, 'Truffle Pizza',       'White pizza with black truffle and mozzarella', 22.00),
  (2, CURRENT_DATE, 'Lobster Linguine',    'Fresh lobster with garlic butter sauce',        28.00),
  (3, CURRENT_DATE, 'Cannoli del Giorno',  'Chef''s special ricotta cannoli',                7.00);

-- Sample daily offers
INSERT INTO offer (id, date, title, description, valid_until) VALUES
  (1, CURRENT_DATE, 'Happy Hour',       '20% off all drinks and appetizers', '7:00 PM'),
  (2, CURRENT_DATE, 'Date Night Deal',  'Free dessert for couples dining after 7pm', '10:00 PM');

-- Sample upcoming events
INSERT INTO event (id, date, title, description, start_time, end_time) VALUES
  (1, CURRENT_DATE,                         'Live Jazz Night',    'Live jazz by Jimmy — perfect for a romantic dinner', '7:00 PM', '10:00 PM'),
  (2, DATEADD(DAY, 2, CURRENT_DATE),        'Wine Tasting Evening', 'Curated Italian wines paired with our signature dishes', '6:00 PM', '9:00 PM'),
  (3, DATEADD(DAY, 4, CURRENT_DATE),        'Trivia Night',       'Fun restaurant trivia with prizes and discounts!', '8:00 PM', '10:00 PM');
