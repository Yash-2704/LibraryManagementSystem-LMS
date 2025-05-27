package GUI;

import javax.swing.*;
import java.awt.*;
import GUI.dialogs.*;
import java.sql.*;
import Database.DBConnection;
import GUI.utils.ThemeManager;
import javax.swing.border.TitledBorder;

public class LibrarianDashboard extends JFrame {
    private JPanel managementTasksPanel;
    private JPanel generalActionsPanel;

    public LibrarianDashboard() {
        setTitle("Librarian Dashboard");
        setSize(750, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel containerPanel = new JPanel(new BorderLayout(20, 20));
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Librarian Dashboard", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        JButton themeButton = new JButton(ThemeManager.isDarkMode() ? "☀️ Light" : "🌙 Dark");
        themeButton.setFont(new Font("Arial", Font.PLAIN, 12));
        themeButton.setFocusPainted(false);
        themeButton.addActionListener(e -> {
            ThemeManager.toggleTheme();
            themeButton.setText(ThemeManager.isDarkMode() ? "☀️ Light" : "🌙 Dark");
            updateSectionPanelColors();
        });
        headerPanel.add(themeButton, BorderLayout.EAST);
        containerPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 20, 20));

        managementTasksPanel = createSectionPanel("Management Tasks", 3);
        JButton approveRequestsBtn = createStyledButton("Approve/Deny Book Requests", "icons/approve_requests.png", new Color(0, 200, 150));
        JButton viewIssuedBtn = createStyledButton("View Issued Books & Fines", "icons/view_issued_books.png", new Color(0, 122, 255));
        JButton viewReturnedBooksBtn = createStyledButton("View Returned Books", "icons/view_returned_books.png", Color.WHITE);
        managementTasksPanel.add(approveRequestsBtn);
        managementTasksPanel.add(viewIssuedBtn);
        managementTasksPanel.add(viewReturnedBooksBtn);
        contentPanel.add(managementTasksPanel);

        generalActionsPanel = createSectionPanel("General Actions", 3);
        JButton viewBooksBtn = createStyledButton("View Available Books", "icons/view_books.png", Color.WHITE);
        JButton viewProfileBtn = createStyledButton("View Profile", "icons/profile.png", Color.WHITE);
        JButton logoutBtn = createStyledButton("Logout", "icons/logout.png", Color.WHITE);
        generalActionsPanel.add(viewBooksBtn);
        generalActionsPanel.add(viewProfileBtn);
        generalActionsPanel.add(logoutBtn);
        contentPanel.add(generalActionsPanel);

        containerPanel.add(contentPanel, BorderLayout.CENTER);
        add(containerPanel);

        approveRequestsBtn.addActionListener(e -> new ApproveRequestsDialog(this));
        viewIssuedBtn.addActionListener(e -> new ViewIssuedBooksDialog(this));
        viewReturnedBooksBtn.addActionListener(e -> new ViewReturnedBooksDialog(this));
        viewBooksBtn.addActionListener(e -> new GUI.dialogs.ViewBooksDialog(this));
        viewProfileBtn.addActionListener(e -> new ViewProfileDialog(this, getLibrarianId()));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        updateSectionPanelColors();
        setVisible(true);
    }

    private void updateSectionPanelColors() {
        Color titleColor = ThemeManager.isDarkMode() ? Color.WHITE : new Color(30, 30, 30);
        Color borderColor = ThemeManager.isDarkMode() ? new Color(100, 100, 100) : new Color(200, 200, 200);
        Font titleFont = new Font("Arial", Font.BOLD, 16);

        if (managementTasksPanel != null) {
            TitledBorder tb = (TitledBorder) managementTasksPanel.getBorder();
            managementTasksPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    tb.getTitle(),
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                    titleFont,
                    titleColor
            ));
            managementTasksPanel.repaint();
        }
        if (generalActionsPanel != null) {
            TitledBorder tb = (TitledBorder) generalActionsPanel.getBorder();
            generalActionsPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    tb.getTitle(),
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                    titleFont,
                    titleColor
            ));
            generalActionsPanel.repaint();
        }
    }

    private JPanel createSectionPanel(String title, int columns) {
        JPanel sectionPanel = new JPanel(new GridLayout(0, columns, 15, 15));
        sectionPanel.setBackground(UIManager.getColor("Panel.background"));
        sectionPanel.setOpaque(true);

        Color initialTitleColor = ThemeManager.isDarkMode() ? Color.WHITE : new Color(30, 30, 30);
        Color initialBorderColor = ThemeManager.isDarkMode() ? new Color(100, 100, 100) : new Color(200, 200, 200);
        Font titleFont = new Font("Arial", Font.BOLD, 16);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(initialBorderColor, 1), title,
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            titleFont, initialTitleColor
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

    private int getLibrarianId() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT user_id FROM Users WHERE role = 'librarian' LIMIT 1";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }
    /*
    public static void main(String[] args) {
        GUI.utils.ThemeManager.initialize();
        SwingUtilities.invokeLater(LibrarianDashboard::new);
    }
    */
}
