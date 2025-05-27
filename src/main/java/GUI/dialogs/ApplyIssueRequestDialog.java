package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import Database.DBConnection;
import GUI.utils.TableUtils;

public class ApplyIssueRequestDialog extends JDialog {
    public ApplyIssueRequestDialog(JFrame parent, int userId) {
        super(parent, "Apply Issue Request", true);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Table to display available books
        String[] columnNames = {"Book ID", "Title", "Author", "Publisher", "Available"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        TableUtils.styleTable(booksTable, scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch available books from the database
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT book_id, title, author, publisher, available FROM Books WHERE available > 0")) {

            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String publisher = rs.getString("publisher");
                int available = rs.getInt("available");

                tableModel.addRow(new Object[]{bookId, title, author, publisher, available});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching books: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Apply button
        JButton applyButton = new JButton("Apply for Issue");
        // add(applyButton, BorderLayout.SOUTH); // Button will be added to a new panel

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        applyButton.setFont(new Font("Arial", Font.BOLD, 14));
        applyButton.setBackground(new Color(0, 122, 255)); // Blue color
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        applyButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        buttonPanel.add(applyButton);
        add(buttonPanel, BorderLayout.SOUTH);

        applyButton.addActionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a book to apply for issue.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int bookId = (int) tableModel.getValueAt(selectedRow, 0);

            try (Connection conn = DBConnection.getConnection()) {
                String query = "INSERT INTO IssueRequests (user_id, book_id, request_date, status) VALUES (?, ?, CURDATE(), 'pending')";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, userId);
                    stmt.setInt(2, bookId);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Issue request submitted successfully!");
                        dispose();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        setLocationRelativeTo(parent);
        setVisible(true);
    }
}