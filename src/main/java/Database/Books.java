package Database;
import java.sql.*;

public class Books {
    public static void main(String[] args) throws Exception {
        String driverClassName = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/LibraryDB"; // Change DB name if needed
        String user = "root";
        String pwd = "yash2704"; // Use your own password

        Class.forName(driverClassName);

        Connection con = DriverManager.getConnection(url, user, pwd);
        System.out.println("Connection Success ---> " + con);

        Statement st = con.createStatement();

        // Insert 50 books into the Books table
        String[] sqlStatements = {
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Clean Code', 'Robert C. Martin', 'Prentice Hall', 10, 10)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('The Pragmatic Programmer', 'Andrew Hunt', 'Addison-Wesley', 8, 8)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Design Patterns', 'Erich Gamma', 'Addison-Wesley', 5, 5)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Refactoring', 'Martin Fowler', 'Addison-Wesley', 7, 7)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Effective Java', 'Joshua Bloch', 'Addison-Wesley', 6, 6)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Head First Java', 'Kathy Sierra', 'O''Reilly Media', 12, 12)", // Escaped single quote
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Java Concurrency in Practice', 'Brian Goetz', 'Addison-Wesley', 4, 4)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('You Don''t Know JS', 'Kyle Simpson', 'O''Reilly Media', 10, 10)", // Escaped single quote
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Eloquent JavaScript', 'Marijn Haverbeke', 'No Starch Press', 9, 9)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('JavaScript: The Good Parts', 'Douglas Crockford', 'O''Reilly Media', 5, 5)", // Escaped single quote
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Python Crash Course', 'Eric Matthes', 'No Starch Press', 8, 8)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Automate the Boring Stuff with Python', 'Al Sweigart', 'No Starch Press', 10, 10)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Fluent Python', 'Luciano Ramalho', 'O''Reilly Media', 6, 6)", // Escaped single quote
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Learning Python', 'Mark Lutz', 'O''Reilly Media', 7, 7)", // Escaped single quote
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Python Cookbook', 'David Beazley', 'O''Reilly Media', 5, 5)", // Escaped single quote
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('C Programming Language', 'Brian W. Kernighan', 'Prentice Hall', 10, 10)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Introduction to Algorithms', 'Thomas H. Cormen', 'MIT Press', 4, 4)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Cracking the Coding Interview', 'Gayle Laakmann McDowell', 'CareerCup', 8, 8)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Algorithms', 'Robert Sedgewick', 'Addison-Wesley', 6, 6)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Data Structures and Algorithms in Java', 'Robert Lafore', 'Sams Publishing', 7, 7)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Operating System Concepts', 'Abraham Silberschatz', 'Wiley', 5, 5)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Computer Networking', 'James F. Kurose', 'Pearson', 6, 6)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Database System Concepts', 'Abraham Silberschatz', 'McGraw-Hill', 5, 5)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Modern Operating Systems', 'Andrew S. Tanenbaum', 'Pearson', 4, 4)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Artificial Intelligence: A Modern Approach', 'Stuart Russell', 'Pearson', 6, 6)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Deep Learning', 'Ian Goodfellow', 'MIT Press', 5, 5)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Pattern Recognition and Machine Learning', 'Christopher M. Bishop', 'Springer', 4, 4)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Hands-On Machine Learning with Scikit-Learn and TensorFlow', 'Aurélien Géron', 'O''Reilly Media', 8, 8)", // Escaped single quote
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Machine Learning Yearning', 'Andrew Ng', 'DeepLearning.AI', 7, 7)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('The Art of Computer Programming', 'Donald E. Knuth', 'Addison-Wesley', 3, 3)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Compilers: Principles, Techniques, and Tools', 'Alfred V. Aho', 'Pearson', 5, 5)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Code Complete', 'Steve McConnell', 'Microsoft Press', 6, 6)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('The Mythical Man-Month', 'Frederick P. Brooks Jr.', 'Addison-Wesley', 4, 4)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('The Phoenix Project', 'Gene Kim', 'IT Revolution Press', 7, 7)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('The DevOps Handbook', 'Gene Kim', 'IT Revolution Press', 6, 6)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Continuous Delivery', 'Jez Humble', 'Addison-Wesley', 5, 5)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Site Reliability Engineering', 'Niall Richard Murphy', 'O''Reilly Media', 4, 4)", // Escaped single quote
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('The Lean Startup', 'Eric Ries', 'Crown Business', 8, 8)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Hooked', 'Nir Eyal', 'Portfolio', 7, 7)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('The Art of Scalability', 'Martin L. Abbott', 'Addison-Wesley', 5, 5)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Building Microservices', 'Sam Newman', 'O''Reilly Media', 6, 6)", // Escaped single quote
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Microservices Patterns', 'Chris Richardson', 'Manning Publications', 5, 5)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Domain-Driven Design', 'Eric Evans', 'Addison-Wesley', 4, 4)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('RESTful Web APIs', 'Leonard Richardson', 'O''Reilly Media', 6, 6)", // Escaped single quote
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Clean Architecture', 'Robert C. Martin', 'Prentice Hall', 7, 7)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('The Pragmatic Programmer (20th Anniversary Edition)', 'Andrew Hunt', 'Addison-Wesley', 8, 8)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Software Engineering', 'Ian Sommerville', 'Pearson', 5, 5)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Agile Estimating and Planning', 'Mike Cohn', 'Prentice Hall', 6, 6)",
            "INSERT INTO Books (title, author, publisher, quantity, available) VALUES ('Extreme Programming Explained', 'Kent Beck', 'Addison-Wesley', 4, 4)"
        };

        for (String sql : sqlStatements) {
            st.executeUpdate(sql);
        }

        System.out.println("--- 50 Books inserted successfully ---");

        st.close();
        con.close();
    }
}
