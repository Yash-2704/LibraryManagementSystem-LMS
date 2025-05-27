package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import Database.DBConnection;

public class RequestBookDialog extends JDialog {
    public RequestBookDialog(JFrame parent, int userId) {
        super(parent, "Request a New Book", true);
        setSize(400, 250);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField publisherField = new JTextField();
        
        formPanel.add(new JLabel("Book Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Publisher:"));
        formPanel.add(publisherField);

        add(formPanel, BorderLayout.CENTER);

        JButton requestButton = new JButton("Request");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        buttonPanel.add(requestButton);

        add(buttonPanel, BorderLayout.SOUTH);

        requestButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String publisher = publisherField.getText().trim();

            if (title.isEmpty() || author.isEmpty() || publisher.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String query = "INSERT INTO BookRequests (user_id, title, author, publisher, request_date, status) VALUES (?, ?, ?, ?, CURDATE(), 'pending')";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, title);
                    stmt.setString(3, author);
                    stmt.setString(4, publisher);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Book request submitted successfully!");
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