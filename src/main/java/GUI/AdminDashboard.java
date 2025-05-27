package GUI;

import javax.swing.*;
import java.awt.*;
import GUI.dialogs.*;
import java.sql.*;
import Database.DBConnection;
import GUI.utils.ThemeManager;
import javax.swing.border.TitledBorder;

public class AdminDashboard extends JFrame {
    private JPanel userManagementPanel; // Made field
    private JPanel bookManagementPanel; // Made field

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(750, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // The JFrame itself can use BorderLayout by default, or you can set it explicitly.
        // setLayout(new BorderLayout()); // Usually not needed for JFrame top level

        // Main container panel that will hold everything
        JPanel containerPanel = new JPanel(new BorderLayout(20, 20)); // Use BorderLayout for container
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add some padding
        // containerPanel.setBackground(new Color(250, 250, 250)); // Let FlatLaf handle background

        // Header Panel: Title and Theme Toggle Button
        JPanel headerPanel = new JPanel(new BorderLayout());
        // headerPanel.setBackground(new Color(250, 250, 250)); // Let FlatLaf handle

        JLabel headerLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        // headerLabel.setForeground(new Color(50, 50, 50)); // Let FlatLaf handle
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        JButton themeButton = new JButton(ThemeManager.isDarkMode() ? "☀️ Light" : "🌙 Dark");
        themeButton.setFont(new Font("Arial", Font.PLAIN, 12)); // Slightly smaller font
        themeButton.setFocusPainted(false);
        themeButton.addActionListener(e -> {
            ThemeManager.toggleTheme();
            themeButton.setText(ThemeManager.isDarkMode() ? "☀️ Light" : "🌙 Dark");
            updateSectionPanelColors(); // Explicitly update section panel colors
        });
        headerPanel.add(themeButton, BorderLayout.EAST); // Add theme button to the right of header
        containerPanel.add(headerPanel, BorderLayout.NORTH); // Add header to the top of container

        // Main Content Panel (for User Management, Book Management sections)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(2, 1, 20, 20)); // Two sections: User and Book Management
        // contentPanel.setBackground(new Color(250, 250, 250)); // Let FlatLaf handle

        // User Management Section - assign to field
        userManagementPanel = createSectionPanel("User Management", 1);
        
        JButton addUserBtn = createStyledButton("Add Librarian/User", "icons/add_user.png", new Color(0, 122, 255));
        userManagementPanel.add(addUserBtn);
        
        JButton deleteUserBtn = createStyledButton("Delete Librarian/User", "icons/delete_user.png", Color.WHITE);
        userManagementPanel.add(deleteUserBtn);
        contentPanel.add(userManagementPanel);

        // Book Management Section - assign to field
        bookManagementPanel = createSectionPanel("Book Management", 3);
        JButton addBookBtn = createStyledButton("Add Book", "icons/add_book.png", new Color(0, 200, 150));
        JButton editBookBtn = createStyledButton("Edit Book", "icons/edit_book.png", Color.WHITE);
        JButton deleteBookBtn = createStyledButton("Delete Book", "icons/delete_book.png", Color.WHITE);
        JButton viewBooksBtn = createStyledButton("View All Books", "icons/view_books.png", Color.WHITE);
        JButton approveBookRequestsBtn = createStyledButton("Approve/Deny Book Requests", "icons/approve_requests.png", Color.WHITE);
        JButton viewProfileBtn = createStyledButton("View Profile", "icons/profile.png", Color.WHITE);
        JButton logoutBtn = createStyledButton("Logout", "icons/logout.png", Color.WHITE);

        bookManagementPanel.add(addBookBtn);
        bookManagementPanel.add(editBookBtn);
        bookManagementPanel.add(deleteBookBtn);
        bookManagementPanel.add(viewBooksBtn);
        bookManagementPanel.add(approveBookRequestsBtn);
        bookManagementPanel.add(viewProfileBtn);
        bookManagementPanel.add(logoutBtn);
        contentPanel.add(bookManagementPanel);

        containerPanel.add(contentPanel, BorderLayout.CENTER); // Add main content to the center of container

        // Add the main container panel to the JFrame
        add(containerPanel);

        // Button actions
        addUserBtn.addActionListener(e -> new GUI.dialogs.AddUserDialog(this));
        deleteUserBtn.addActionListener(e -> new GUI.dialogs.DeleteUserDialog(this));
        addBookBtn.addActionListener(e -> new GUI.dialogs.AddBookDialog(this));
        editBookBtn.addActionListener(e -> new GUI.dialogs.EditBookDialog(this));
        deleteBookBtn.addActionListener(e -> new GUI.dialogs.DeleteBookDialog(this));
        viewBooksBtn.addActionListener(e -> new GUI.dialogs.ViewBooksDialog(this));
        approveBookRequestsBtn.addActionListener(e -> new GUI.dialogs.ApproveBookRequestsDialog(this));
        viewProfileBtn.addActionListener(e -> new GUI.dialogs.ViewProfileDialog(this, getAdminId()));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        updateSectionPanelColors(); // Set initial colors correctly
        setVisible(true);
    }

    private void updateSectionPanelColors() {
        Color titleColor = ThemeManager.isDarkMode() ? Color.WHITE : new Color(30, 30, 30);
        Color borderColor = ThemeManager.isDarkMode() ? new Color(100, 100, 100) : new Color(200, 200, 200);
        Font titleFont = new Font("Arial", Font.BOLD, 16);

        if (userManagementPanel != null) {
            TitledBorder tb = (TitledBorder) userManagementPanel.getBorder();
            userManagementPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    tb.getTitle(), // Get existing title from old border
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                    titleFont, // Use consistent font
                    titleColor
            ));
            userManagementPanel.repaint();
        }
        if (bookManagementPanel != null) {
            TitledBorder tb = (TitledBorder) bookManagementPanel.getBorder();
            bookManagementPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    tb.getTitle(), // Get existing title from old border
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                    titleFont, // Use consistent font
                    titleColor
            ));
            bookManagementPanel.repaint();
        }
    }

    private JPanel createSectionPanel(String title, int columns) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new GridLayout(0, columns, 15, 15));
        
        // Explicitly set panel background to allow TitledBorder to paint correctly with FlatLaf
        // UIManager.getColor("Panel.background") should give the correct theme background.
        sectionPanel.setBackground(UIManager.getColor("Panel.background"));
        sectionPanel.setOpaque(true);

        // Initial colors set here, will be updated by updateSectionPanelColors if theme changes
        Color initialTitleColor = ThemeManager.isDarkMode() ? Color.WHITE : new Color(30, 30, 30);
        Color initialBorderColor = ThemeManager.isDarkMode() ? new Color(100, 100, 100) : new Color(200, 200, 200);
        Font titleFont = new Font("Arial", Font.BOLD, 16);

        // Create a TitledBorder and then set it on the panel
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(initialBorderColor, 1),
                title,
                TitledBorder.DEFAULT_JUSTIFICATION, // Use TitledBorder constants
                TitledBorder.DEFAULT_POSITION,
                titleFont,
                initialTitleColor
        );
        sectionPanel.setBorder(titledBorder);
        
        return sectionPanel;
    }

    private JButton createStyledButton(String text, String iconPath, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        if (backgroundColor != null) {
            button.setBackground(backgroundColor);
            boolean isDarkBackground = (backgroundColor.getRed() + backgroundColor.getGreen() + backgroundColor.getBlue()) < 384;
            button.setForeground(isDarkBackground ? Color.WHITE : Color.BLACK);
            button.setOpaque(true);
        }
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        button.setPreferredSize(new Dimension(170, 45));
        button.setHorizontalAlignment(SwingConstants.LEFT);

        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                // Load icon from classpath resources
                java.net.URL imgURL = getClass().getResource("/" + iconPath);
                if (imgURL != null) {
                    ImageIcon originalIcon = new ImageIcon(imgURL);
                    int iconSize = button.getPreferredSize().height - 20;
                    if (iconSize < 16) iconSize = 16;
                    Image scaledImage = originalIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(scaledImage));
                    button.setIconTextGap(8);
                } else {
                    System.err.println("Couldn't find icon: /" + iconPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return button;
    }

    private int getAdminId() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT user_id FROM Users WHERE role = 'admin' LIMIT 1";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Return -1 if no admin found
    }

    // It's good practice for Swing applications to have a main method for testing individual frames,
    // but GUI.Main is your primary entry point.
    /*
    public static void main(String[] args) {
        GUI.utils.ThemeManager.initialize(); // Ensure theme is set if running this frame directly
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
    */
}
