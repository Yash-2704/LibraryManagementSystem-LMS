# Library System GUI

A Java Swing application for managing a library system, including user roles (admin, librarian, user), book management, issue requests, and a themeable user interface with light and dark modes powered by FlatLaf.

## Features

*   User authentication (Admin, Librarian, User roles).
*   **Admin Dashboard:**
    *   User Management: Add/Delete Librarians and Users.
    *   Book Management: Add/Edit/Delete books, View all books, Approve/Deny book addition requests from users.
*   **Librarian Dashboard:**
    *   Approve/Deny book issue requests from users.
    *   View issued books and calculated fines.
    *   View returned books.
    *   View available books.
*   **User Dashboard:**
    *   Apply for book issue requests.
    *   Return books.
    *   View their own issued books, requests, and fines.
    *   Request new books to be added to the library.
    *   View all available books.
*   **Common Features:**
    *   View user profile.
    *   Toggle between Light and Dark themes (FlatLaf).
    *   Styled tables and dialogs for a consistent user experience.

## Prerequisites

*   Java Development Kit (JDK) 8 or higher.
*   MySQL Server.
*   The following JAR files (included in the `lib/` directory):
    *   `flatlaf-3.6.jar` (or newer compatible version for themes)
    *   `mysql-connector-j-9.3.0.jar` (or newer compatible version for MySQL connection)

## Setup Instructions

1.  **Clone the Repository (or Download a Zip and Extract):**
    ```bash
    # git clone <repository-url>
    # cd LibrarySystem-GUI
    ```

2.  **Database Setup:**
    *   Ensure your MySQL server is running.
    *   Connect to your MySQL server using a client (e.g., MySQL command-line client, MySQL Workbench).
    *   Create the database for the application:
        ```sql
        CREATE DATABASE IF NOT EXISTS LibraryDB2;
        ```
    *   Use the created database:
        ```sql
        USE LibraryDB2;
        ```
    *   Run the `schema.sql` file provided in the root of the project to create the necessary tables. From the project root directory in your terminal:
        ```bash
        mysql -u YOUR_MYSQL_USERNAME -p LibraryDB2 < schema.sql
        ```
        (Replace `YOUR_MYSQL_USERNAME` with your MySQL username. You will be prompted for your password.)
    *   **Note on Database Credentials:** The application connects to the database using credentials defined in `src/main/java/Database/DBConnection.java`. Currently, it expects the database `LibraryDB2` on `localhost:3306` with username `root` and password `YOUR_DB_PASSWORD`. **You MUST update the `DB_PASSWORD` in `DBConnection.java` to match your MySQL root password or the password of the user you intend to use.**
        ```java
        // In DBConnection.java:
        private static final String DB_URL = "jdbc:mysql://localhost:3306/LibraryDB2";
        private static final String DB_USER = "root";
        private static final String DB_PASSWORD = "YOUR_DB_PASSWORD"; // <-- UPDATE THIS
        ```

3.  **Ensure JAR Dependencies are Present:**
    *   The `lib/` directory in the project should contain `flatlaf-3.6.jar` and `mysql-connector-j-9.3.0.jar`. If you downloaded these separately, place them here.

4.  **Compile and Run:**
    *   Navigate to the project root directory in your terminal.
    *   **For Linux/macOS users:**
        *   Make the `compile_and_run.sh` script executable (if it isn't already):
            ```bash
            chmod +x compile_and_run.sh
            ```
        *   Run the script:
            ```bash
            ./compile_and_run.sh
            ```
        This script will clean old class files, compile all Java source files into the `bin/` directory, and then run the application (`GUI.Main`).
    *   **For Windows users:**
        *   Open Command Prompt or PowerShell in the project root directory.
        *   **Clean old class files (optional but recommended):**
            ```batch
            del /s /q "src\main\java\**\*.class"
            del /s /q "bin\*.class"
            rd /s /q "bin"
            ```
        *   **Create the output directory if it doesn't exist:**
            ```batch
            if not exist bin mkdir bin
            ```
        *   **Compile the Java source files:**
            You'll need to list all `.java` files. An easy way to get all `.java` files recursively from `src/main/java` is to use a `for` loop in the command prompt or `Get-ChildItem` in PowerShell.

            *   **Command Prompt:**
                ```batch
                javac -cp "lib\flatlaf-3.6.jar;lib\mysql-connector-j-9.3.0.jar;src\main\resources" -d bin @(for /R "src\main\java" %f in (*.java) do @echo "%f")
                ```
                *(Note: If running this directly in a `.bat` script, replace `%f` with `%%f`)*
            *   **PowerShell:**
                ```powershell
                $javaFiles = Get-ChildItem -Path "src\main\java" -Recurse -Filter *.java | ForEach-Object { $_.FullName }
                javac -cp "lib\flatlaf-3.6.jar;lib\mysql-connector-j-9.3.0.jar;src\main\resources" -d bin $javaFiles
                ```
        *   **Run the application:**
            ```batch
            java -cp "bin;lib\flatlaf-3.6.jar;lib\mysql-connector-j-9.3.0.jar;src\main\resources" GUI.Main
            ```
        *   **Alternative for Windows users:** Consider using Git Bash or Windows Subsystem for Linux (WSL) to use the `./compile_and_run.sh` script directly.

## Project Structure

*   `src/main/java/`: Contains the Java source code.
    *   `Database/`: Database connection logic (`DBConnection.java`).
    *   `GUI/`: Main GUI frames (Dashboards, LoginFrame, Main).
    *   `GUI/dialogs/`: Various dialog windows used in the application.
    *   `GUI/utils/`: Utility classes for GUI elements (`TableUtils.java`, `ThemeManager.java`).
*   `src/main/resources/`: Contains resource files.
    *   `icons/`: Application icons.
*   `lib/`: Contains external JAR dependencies (FlatLaf, MySQL Connector).
*   `bin/`: Compiled `.class` files (created by the compile script, ignored by Git).
*   `compile_and_run.sh`: Shell script to compile and run the application.
*   `schema.sql`: SQL script to set up the database tables.
*   `.gitignore`: Specifies intentionally untracked files that Git should ignore.

## Notes

*   The application uses the FlatLaf library for theming. The theme can be toggled between light and dark mode using the button in the header of each dashboard.
*   Fines for overdue books are calculated at a rate of 50 (currency unit) per day. 