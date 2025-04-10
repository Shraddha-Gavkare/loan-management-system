-- Create schema
CREATE DATABASE IF NOT EXISTS LoanDB;
USE LoanDB;

-- Create Customer table
CREATE TABLE IF NOT EXISTS Customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    address VARCHAR(255),
    credit_score INT CHECK (credit_score BETWEEN 300 AND 900)
);

-- Create Loan table
CREATE TABLE IF NOT EXISTS Loan (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    principal_amount DOUBLE NOT NULL CHECK (principal_amount > 0),
    interest_rate DOUBLE NOT NULL CHECK (interest_rate > 0),
    loan_term INT NOT NULL CHECK (loan_term > 0),
    loan_type ENUM('HomeLoan', 'CarLoan') NOT NULL,
    loan_status ENUM('Pending', 'Approved') DEFAULT 'Pending',
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

select * from Customer;
select * from Loan;


USE LoanDB;

-- Insert Customer Data
INSERT INTO Customer (name, email, phone, address, credit_score)
VALUES 
('Aman Kumar', 'aman.kumar@example.com', '9876543210', 'Delhi, India', 720),
('Shravani Gavkare', 'shrau@gmail.com', '8766828200', 'Pune', 680),
('Rohan Sharma', 'rohan.sharma@gmail.com', '9001234567', 'Mumbai', 640);

-- Insert Loan Data
INSERT INTO Loan (customer_id, principal_amount, interest_rate, loan_term, loan_type, loan_status)
VALUES
(1, 500000, 9.2, 10, 'HomeLoan', 'Pending'),
(2, 300000, 10.5, 24, 'CarLoan', 'Pending'),
(3, 200000, 11.0, 12, 'CarLoan', 'Pending');

 
SELECT * FROM Customer;


-- View loans with customer details
SELECT l.loan_id, c.name, l.principal_amount, l.interest_rate, l.loan_term, l.loan_type, l.loan_status
FROM Loan l
JOIN Customer c ON l.customer_id = c.customer_id;