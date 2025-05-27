package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import Database.DBConnection;
import GUI.utils.TableUtils;

public class EditBookDialog extends JDialog {
    public EditBookDialog(JFrame parent) {
        super(parent, "Edit Book", true);
        setSize(800, 500);
        setLayout(new BorderLayout());

        // Table to display books
        String[] columnNames = {"Book ID", "Title", "Author", "Publisher", "Quantity", "Available"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            // ... existing code ...
        };
        JTable booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        TableUtils.styleTable(booksTable, scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch books from the database
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Books")) {

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

        // Form to edit book details
        JPanel formPanel = new JPanel(new GridLayout(6, 2));
        JTextField bookIdField = new JTextField();
        bookIdField.setEditable(false);
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField publisherField = new JTextField();
        JTextField quantityField = new JTextField();
        JButton saveButton = new JButton("Save Changes");

        formPanel.add(new JLabel("Book ID:"));
        formPanel.add(bookIdField);
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Publisher:"));
        formPanel.add(publisherField);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);

        // Button panel for "Save Changes"
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(new Color(0, 122, 255)); // Example blue color
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15) 
        ));
        buttonPanel.add(saveButton);

        // Main content panel for form and button panel
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // Populate form when a book is selected
        booksTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow != -1) {
                bookIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                titleField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                authorField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                publisherField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                quantityField.setText(tableModel.getValueAt(selectedRow, 4).toString());
            }
        });

        // Save changes to the database
        saveButton.addActionListener(e -> {
            int bookId = Integer.parseInt(bookIdField.getText().trim());
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String publisher = publisherField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());

            try (Connection conn = DBConnection.getConnection()) {
                String query = "UPDATE Books SET title = ?, author = ?, publisher = ?, quantity = ?, available = ? WHERE book_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, title);
                    stmt.setString(2, author);
                    stmt.setString(3, publisher);
                    stmt.setInt(4, quantity);
                    stmt.setInt(5, quantity); // Assuming available = quantity for simplicity
                    stmt.setInt(6, bookId);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Book updated successfully!");
                        tableModel.setValueAt(title, booksTable.getSelectedRow(), 1);
                        tableModel.setValueAt(author, booksTable.getSelectedRow(), 2);
                        tableModel.setValueAt(publisher, booksTable.getSelectedRow(), 3);
                        tableModel.setValueAt(quantity, booksTable.getSelectedRow(), 4);
                        tableModel.setValueAt(quantity, booksTable.getSelectedRow(), 5);
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