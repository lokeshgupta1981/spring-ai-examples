-- Insert 10 users
INSERT INTO TBL_USER (username, email, password)
VALUES
    ('user1', 'user1@example.com', 'password1'),
    ('user2', 'user2@example.com', 'password2'),
    ('user3', 'user3@example.com', 'password3'),
    ('user4', 'user4@example.com', 'password4'),
    ('user5', 'user5@example.com', 'password5'),
    ('user6', 'user6@example.com', 'password6'),
    ('user7', 'user7@example.com', 'password7'),
    ('user8', 'user8@example.com', 'password8'),
    ('user9', 'user9@example.com', 'password9'),
    ('user10', 'user10@example.com', 'password10');

-- Insert accounts for user 1
INSERT INTO TBL_ACCOUNT (accountNumber, user_id, balance, openDate)
VALUES
    ('ACC001', 1, 1000.00, '2024-07-09'),
    ('ACC002', 1, 500.00, '2024-07-10');

-- Insert accounts for user 2
INSERT INTO TBL_ACCOUNT (accountNumber, user_id, balance, openDate)
VALUES
    ('ACC003', 2, 1500.00, '2024-07-09'),
    ('ACC004', 2, 200.00, '2024-07-10');

-- Insert accounts for user 3
INSERT INTO TBL_ACCOUNT (accountNumber, user_id, balance, openDate)
VALUES
    ('ACC005', 3, 800.00, '2024-07-09');

-- Insert accounts for user 4
INSERT INTO TBL_ACCOUNT (accountNumber, user_id, balance, openDate)
VALUES
    ('ACC006', 4, 3000.00, '2024-07-09'),
    ('ACC007', 4, 100.00, '2024-07-10');

-- Insert accounts for user 5
INSERT INTO TBL_ACCOUNT (accountNumber, user_id, balance, openDate)
VALUES
    ('ACC008', 5, 250.00, '2024-07-09');

-- Insert accounts for user 6
INSERT INTO TBL_ACCOUNT (accountNumber, user_id, balance, openDate)
VALUES
    ('ACC009', 6, 1800.00, '2024-07-09'),
    ('ACC010', 6, 700.00, '2024-07-10');

-- Insert accounts for user 7
INSERT INTO TBL_ACCOUNT (accountNumber, user_id, balance, openDate)
VALUES
    ('ACC011', 7, 500.00, '2024-07-09');

-- Insert accounts for user 8
INSERT INTO TBL_ACCOUNT (accountNumber, user_id, balance, openDate)
VALUES
    ('ACC012', 8, 1200.00, '2024-07-09');

-- Insert accounts for user 9
INSERT INTO TBL_ACCOUNT (accountNumber, user_id, balance, openDate)
VALUES
    ('ACC013', 9, 900.00, '2024-07-09'),
    ('ACC014', 9, 300.00, '2024-07-10');

-- Insert accounts for user 10
INSERT INTO TBL_ACCOUNT (accountNumber, user_id, balance, openDate)
VALUES
    ('ACC015', 10, 2000.00, '2024-07-09');
