package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import Database.DBConnection;
import GUI.utils.TableUtils;

public class ViewBooksDialog extends JDialog {
    public ViewBooksDialog(JFrame parent) {
        super(parent, "View Available Books", true);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Table to display books
        String[] columnNames = {"Book ID", "Title", "Author", "Publisher", "Quantity", "Available"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        JTable booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        
        // Apply the new styling
        TableUtils.styleTable(booksTable, scrollPane);
        
        add(scrollPane, BorderLayout.CENTER);

        // Fetch available books from the database
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Books WHERE available > 0")) {

            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String publisher = rs.getString("publisher");
                int quantity = rs.getInt("quantity");
                int available = rs.getInt("available");

                tableModel.addRow(new Object[]{bookId, title, author, publisher, quantity, available});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching books: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        // add(closeButton, BorderLayout.SOUTH); // Button will be added to a new panel

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        // Using a neutral color for Close button
        closeButton.setBackground(new Color(108, 117, 125)); 
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