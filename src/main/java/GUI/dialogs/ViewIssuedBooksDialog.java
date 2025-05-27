package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import Database.DBConnection;
import GUI.utils.TableUtils;

public class ViewIssuedBooksDialog extends JDialog {
    public ViewIssuedBooksDialog(JFrame parent) {
        super(parent, "View Issued Books & Fines", true);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Table to display issued books
        String[] columnNames = {"Issue ID", "User ID", "Book ID", "Issue Date", "Expected Return Date", "Return Date", "Fine"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable issuedBooksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(issuedBooksTable);
        TableUtils.styleTable(issuedBooksTable, scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch issued books from the database
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT issue_id, user_id, book_id, issue_date, return_date, " +
                     "DATE_ADD(issue_date, INTERVAL 15 DAY) AS expected_return_date " +
                     "FROM IssuedBooks")) {

            while (rs.next()) {
                int issueId = rs.getInt("issue_id");
                int userId = rs.getInt("user_id");
                int bookId = rs.getInt("book_id");
                Date issueDate = rs.getDate("issue_date");
                Date expectedReturnDate = rs.getDate("expected_return_date");
                Date returnDate = rs.getDate("return_date");

                // Calculate fine
                double fine = 0;
                if (returnDate == null) { // Book not yet returned
                    Date currentDate = new Date(System.currentTimeMillis());
                    if (currentDate.after(expectedReturnDate)) {
                        long overdueDays = (currentDate.getTime() - expectedReturnDate.getTime()) / (1000 * 60 * 60 * 24);
                        fine = overdueDays * 50; // 50 rupees per day
                    }
                } else if (returnDate.after(expectedReturnDate)) { // Book returned late
                    long overdueDays = (returnDate.getTime() - expectedReturnDate.getTime()) / (1000 * 60 * 60 * 24);
                    fine = overdueDays * 50; // 50 rupees per day
                }

                tableModel.addRow(new Object[]{issueId, userId, bookId, issueDate, expectedReturnDate, returnDate, fine});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching issued books: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBackground(new Color(108, 117, 125)); // Neutral color
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(parent);
        setVisible(true);
    }
}