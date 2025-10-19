-- ======================================================
-- BASE CLIENT DATA (COMMON FIELDS)
-- ======================================================
INSERT INTO client (id, name, email, phone, type)
VALUES
  (RANDOM_UUID(), 'Alice Dupont', 'alice.dupont@email.com', '+33612345678', 'PERSON'),
  (RANDOM_UUID(), 'Bob Martin', 'bob.martin@email.com', '+33687654321', 'PERSON'),
  (RANDOM_UUID(), 'Clara Nguyen', 'clara.nguyen@email.com', '+33699887766', 'PERSON'),
  (RANDOM_UUID(), 'David Smith', 'david.smith@email.com', '+447911223344', 'PERSON'),
  (RANDOM_UUID(), 'Emma Rossi', 'emma.rossi@email.com', '+390212345678', 'PERSON'),
  (RANDOM_UUID(), 'TechCorp', 'contact@techcorp.com', '+33198765432', 'COMPANY'),
  (RANDOM_UUID(), 'NeoBuild', 'contact@neobuild.com', '+33144556677', 'COMPANY'),
  (RANDOM_UUID(), 'GreenLogix', 'info@greenlogix.com', '+33188997766', 'COMPANY');

-- ======================================================
-- PERSON DETAILS (Linked by CLIENT.ID via EMAIL)
-- ======================================================
INSERT INTO person (id, birth_date)
SELECT c.id, '1990-04-12'
FROM client c
WHERE c.email = 'alice.dupont@email.com'
  AND NOT EXISTS (SELECT 1 FROM person p WHERE p.id = c.id);

INSERT INTO person (id, birth_date)
SELECT c.id, '1985-09-23'
FROM client c
WHERE c.email = 'bob.martin@email.com'
  AND NOT EXISTS (SELECT 1 FROM person p WHERE p.id = c.id);

INSERT INTO person (id, birth_date)
SELECT c.id, '1998-01-08'
FROM client c
WHERE c.email = 'clara.nguyen@email.com'
  AND NOT EXISTS (SELECT 1 FROM person p WHERE p.id = c.id);

INSERT INTO person (id, birth_date)
SELECT c.id, '1992-06-15'
FROM client c
WHERE c.email = 'david.smith@email.com'
  AND NOT EXISTS (SELECT 1 FROM person p WHERE p.id = c.id);

INSERT INTO person (id, birth_date)
SELECT c.id, '1987-12-03'
FROM client c
WHERE c.email = 'emma.rossi@email.com'
  AND NOT EXISTS (SELECT 1 FROM person p WHERE p.id = c.id);

-- ======================================================
-- COMPANY DETAILS (Linked by CLIENT.ID via EMAIL)
-- ======================================================
INSERT INTO company (id, company_id)
SELECT c.id, 'abc-123'
FROM client c
WHERE c.email = 'contact@techcorp.com'
  AND NOT EXISTS (SELECT 1 FROM company co WHERE co.id = c.id);

INSERT INTO company (id, company_id)
SELECT c.id, 'xyz-987'
FROM client c
WHERE c.email = 'contact@neobuild.com'
  AND NOT EXISTS (SELECT 1 FROM company co WHERE co.id = c.id);

INSERT INTO company (id, company_id)
SELECT c.id, 'eco-555'
FROM client c
WHERE c.email = 'info@greenlogix.com'
  AND NOT EXISTS (SELECT 1 FROM company co WHERE co.id = c.id);

-- ======================================================
-- SAMPLE CONTRACTS (Linked by CLIENT.ID)
-- ======================================================
-- Active contracts (no end date)
INSERT INTO contract (client_id, start_date, end_date, cost_amount, update_date)
SELECT c.id, DATEADD('DAY', -10, CURRENT_DATE), NULL, 1200.00, CURRENT_DATE
FROM client c WHERE c.email = 'alice.dupont@email.com'
  AND NOT EXISTS (SELECT 1 FROM contract k WHERE k.client_id = c.id);

INSERT INTO contract (client_id, start_date, end_date, cost_amount, update_date)
SELECT c.id, DATEADD('DAY', -30, CURRENT_DATE), NULL, 850.00, CURRENT_DATE
FROM client c WHERE c.email = 'contact@techcorp.com'
  AND NOT EXISTS (SELECT 1 FROM contract k WHERE k.client_id = c.id);

-- Expired contract
INSERT INTO contract (client_id, start_date, end_date, cost_amount, update_date)
SELECT c.id, DATEADD('MONTH', -6, CURRENT_DATE), DATEADD('MONTH', -1, CURRENT_DATE), 300.00, CURRENT_DATE
FROM client c WHERE c.email = 'bob.martin@email.com'
  AND NOT EXISTS (SELECT 1 FROM contract k WHERE k.client_id = c.id);

INSERT INTO contract (client_id, start_date, end_date, cost_amount, update_date)
SELECT c.id, DATEADD('MONTH', -12, CURRENT_DATE), DATEADD('MONTH', -2, CURRENT_DATE), 500.00, CURRENT_DATE
FROM client c WHERE c.email = 'contact@neobuild.com'
  AND NOT EXISTS (SELECT 1 FROM contract k WHERE k.client_id = c.id);
