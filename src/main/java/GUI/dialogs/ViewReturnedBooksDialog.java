package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import Database.DBConnection;
import GUI.utils.TableUtils;

public class ViewReturnedBooksDialog extends JDialog {
    public ViewReturnedBooksDialog(JFrame parent) {
        super(parent, "View Returned Books", true);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Table to display returned books
        String[] columnNames = {"Return ID", "Issue ID", "User ID", "Book ID", "Return Date", "Fine"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable returnedBooksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(returnedBooksTable);
        TableUtils.styleTable(returnedBooksTable, scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch returned books from the database
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ReturnedBooks")) {

            while (rs.next()) {
                int returnId = rs.getInt("return_id");
                int issueId = rs.getInt("issue_id");
                int userId = rs.getInt("user_id");
                int bookId = rs.getInt("book_id");
                Date returnDate = rs.getDate("return_date");
                double fine = rs.getDouble("fine");

                tableModel.addRow(new Object[]{returnId, issueId, userId, bookId, returnDate, fine});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching returned books: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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