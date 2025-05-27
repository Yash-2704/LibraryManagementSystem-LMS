-- SQL Schema for Library System GUI (LibraryDB2)

-- Ensure the database 'LibraryDB2' is created and selected before running this script.
-- Example: CREATE DATABASE IF NOT EXISTS LibraryDB2;
--          USE LibraryDB2;

-- Table: Users
CREATE TABLE IF NOT EXISTS Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'librarian', 'user') NOT NULL
);

-- Table: Books
CREATE TABLE IF NOT EXISTS Books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    publisher VARCHAR(255),
    quantity INT NOT NULL,
    available INT NOT NULL,
    CONSTRAINT chk_available_qty CHECK (available >= 0 AND available <= quantity)
);

-- Table: BookRequests (for users to request new books to be added to the library)
CREATE TABLE IF NOT EXISTS BookRequests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    publisher VARCHAR(255),
    request_date DATE NOT NULL,
    status ENUM('pending', 'approved', 'denied') NOT NULL DEFAULT 'pending',
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL -- or ON DELETE CASCADE if user must exist
);

-- Table: IssueRequests (for users to request issuing an existing book)
CREATE TABLE IF NOT EXISTS IssueRequests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    request_date DATE NOT NULL,
    status ENUM('pending', 'approved', 'denied', 'issued', 'returned') NOT NULL DEFAULT 'pending',
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES Books(book_id) ON DELETE CASCADE
);

-- Table: IssuedBooks (tracks books currently issued to users)
CREATE TABLE IF NOT EXISTS IssuedBooks (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL, -- Calculated as issue_date + 15 days typically
    return_date DATE NULL, -- Will be NULL until the book is returned
    fine DOUBLE DEFAULT 0.0,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES Books(book_id) ON DELETE CASCADE,
    CONSTRAINT chk_return_after_issue CHECK (return_date IS NULL OR return_date >= issue_date)
);

-- Table: ReturnedBooks (logs books that have been returned)
CREATE TABLE IF NOT EXISTS ReturnedBooks (
    return_id INT AUTO_INCREMENT PRIMARY KEY,
    issue_id INT NOT NULL, -- Foreign key to the original issue record in IssuedBooks
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    return_date DATE NOT NULL,
    fine_paid DOUBLE DEFAULT 0.0, -- Store the actual fine amount that was paid for this return
    FOREIGN KEY (issue_id) REFERENCES IssuedBooks(issue_id) ON DELETE CASCADE, -- This ensures referential integrity
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE, -- Can be useful for quick lookups if issue_id is not directly available
    FOREIGN KEY (book_id) REFERENCES Books(book_id) ON DELETE CASCADE  -- Same as above
);

-- Initial Data Inserts --

-- Default Users (passwords are placeholders, recommend changing them after initial setup)
INSERT INTO Users (name, email, password, role)
VALUES 
    ('Admin One', 'admin@example.com', 'adminpass', 'admin'),
    ('Librarian One', 'librarian@example.com', 'libpass', 'librarian'),
    ('User One', 'user1@example.com', 'user1pass', 'user1'),
    ('User Two', 'user2@example.com', 'user2pass', 'user2')
ON DUPLICATE KEY UPDATE name = VALUES(name), email = VALUES(email), password = VALUES(password), role = VALUES(role);

-- Sample Books
INSERT INTO Books (title, author, publisher, quantity, available)
VALUES
    ('Clean Code', 'Robert C. Martin', 'Prentice Hall', 10, 10),
    ('Effective Java', 'Joshua Bloch', 'Addison-Wesley', 8, 8),
    ('The Pragmatic Programmer', 'Andrew Hunt', 'Addison-Wesley', 5, 5),
    ('Design Patterns', 'Erich Gamma', 'Addison-Wesley', 7, 7),
    ('Refactoring', 'Martin Fowler', 'Addison-Wesley', 3, 3),
    ('Head First Java', 'Kathy Sierra', 'O'Reilly Media', 12, 12),
    ('Java Concurrency in Practice', 'Brian Goetz', 'Addison-Wesley', 4, 4),
    ('You Don\'t Know JS: Up & Going', 'Kyle Simpson', 'O'Reilly Media', 10, 10),
    ('Eloquent JavaScript', 'Marijn Haverbeke', 'No Starch Press', 9, 9)
ON DUPLICATE KEY UPDATE title = VALUES(title), author = VALUES(author), publisher = VALUES(publisher), quantity = VALUES(quantity), available = VALUES(available);

-- Note: The ENUM types and default values are based on common usage in the application.
-- Adjust foreign key constraints (ON DELETE behavior) based on your specific requirements.
-- For example, if a user is deleted, what should happen to their requests or issued books?
-- Current setup uses ON DELETE CASCADE for most user-related records, meaning if a user is deleted, their related records are also deleted.
-- For BookRequests, user_id is SET NULL if the user is deleted, to keep the request history. 