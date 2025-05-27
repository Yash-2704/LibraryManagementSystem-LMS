package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import Database.DBConnection;
import GUI.utils.TableUtils;

public class DeleteUserDialog extends JDialog {
    public DeleteUserDialog(JFrame parent) {
        super(parent, "Delete Librarian/User", true);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Table to display users
        String[] columnNames = {"User ID", "Name", "Email", "Role"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        TableUtils.styleTable(userTable, scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch users from the database
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT user_id, name, email, role FROM Users")) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String role = rs.getString("role");

                tableModel.addRow(new Object[]{userId, name, email, role});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Delete button
        JButton deleteButton = new JButton("Delete Selected User");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setBackground(new Color(220, 53, 69)); // Example red color
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        deleteButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            String role = tableModel.getValueAt(selectedRow, 3).toString();

            if (role.equalsIgnoreCase("admin")) {
                JOptionPane.showMessageDialog(this, "Admin cannot be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false); // Start transaction

                // Delete dependent rows from ReturnedBooks table
                String deleteReturnedBooksQuery = "DELETE FROM ReturnedBooks WHERE user_id = ?";
                try (PreparedStatement deleteReturnedBooksStmt = conn.prepareStatement(deleteReturnedBooksQuery)) {
                    deleteReturnedBooksStmt.setInt(1, userId);
                    deleteReturnedBooksStmt.executeUpdate();
                }

                // Delete dependent rows from IssueRequests table
                String deleteRequestsQuery = "DELETE FROM IssueRequests WHERE user_id = ?";
                try (PreparedStatement deleteRequestsStmt = conn.prepareStatement(deleteRequestsQuery)) {
                    deleteRequestsStmt.setInt(1, userId);
                    deleteRequestsStmt.executeUpdate();
                }

                // Delete the user from Users table
                String deleteUserQuery = "DELETE FROM Users WHERE user_id = ?";
                try (PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery)) {
                    deleteUserStmt.setInt(1, userId);
                    int rowsDeleted = deleteUserStmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        conn.commit(); // Commit transaction
                        JOptionPane.showMessageDialog(this, "User deleted successfully!");
                        tableModel.removeRow(selectedRow); // Remove from table
                    } else {
                        JOptionPane.showMessageDialog(this, "User ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        conn.rollback(); // Rollback transaction
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