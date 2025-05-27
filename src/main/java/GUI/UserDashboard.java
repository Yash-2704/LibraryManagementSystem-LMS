package GUI;

import javax.swing.*;
import java.awt.*;
import GUI.dialogs.*;
import GUI.utils.ThemeManager;
import javax.swing.border.TitledBorder;

public class UserDashboard extends JFrame {
    private int userId;
    private JPanel bookActionsPanel;
    private JPanel accountPanel;

    public UserDashboard(int userId) {
        this.userId = userId;

        setTitle("User Dashboard");
        setSize(750, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel containerPanel = new JPanel(new BorderLayout(20, 20));
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("User Dashboard", JLabel.CENTER);
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

        bookActionsPanel = createSectionPanel("Book Actions", 1);
        JButton applyIssueRequestBtn = createStyledButton("Apply Issue Request", "icons/apply_request.png", new Color(0, 200, 150));
        applyIssueRequestBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton returnBookBtn = createStyledButton("Return a Book", "icons/return_book.png", Color.WHITE);
        returnBookBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookActionsPanel.add(applyIssueRequestBtn);
        bookActionsPanel.add(returnBookBtn);
        contentPanel.add(bookActionsPanel);

        accountPanel = createSectionPanel("Account", 3);
        JButton viewMyBooksBtn = createStyledButton("My Issued Books & Fines", "icons/view_issued_books.png", new Color(0, 122, 255));
        JButton requestBookBtn = createStyledButton("Request a New Book", "icons/request_book.png", Color.WHITE);
        JButton viewBooksBtn = createStyledButton("View All Books", "icons/view_books.png", Color.WHITE);
        JButton viewProfileBtn = createStyledButton("View Profile", "icons/profile.png", Color.WHITE);
        JButton logoutBtn = createStyledButton("Logout", "icons/logout.png", Color.WHITE);
        accountPanel.add(viewMyBooksBtn);
        accountPanel.add(requestBookBtn);
        accountPanel.add(viewBooksBtn);
        accountPanel.add(viewProfileBtn);
        accountPanel.add(logoutBtn);
        contentPanel.add(accountPanel);

        containerPanel.add(contentPanel, BorderLayout.CENTER);
        add(containerPanel);

        applyIssueRequestBtn.addActionListener(e -> new ApplyIssueRequestDialog(this, userId));
        returnBookBtn.addActionListener(e -> new ReturnBookByUserDialog(this, userId));
        viewMyBooksBtn.addActionListener(e -> new ViewMyBooksDialog(this, userId));
        requestBookBtn.addActionListener(e -> new RequestBookDialog(this, userId));
        viewBooksBtn.addActionListener(e -> new GUI.dialogs.ViewBooksDialog(this));
        viewProfileBtn.addActionListener(e -> new ViewProfileDialog(this, userId));
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

        if (bookActionsPanel != null) {
            TitledBorder tb = (TitledBorder) bookActionsPanel.getBorder();
            bookActionsPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    tb.getTitle(),
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                    titleFont,
                    titleColor
            ));
            bookActionsPanel.repaint();
        }
        if (accountPanel != null) {
            TitledBorder tb = (TitledBorder) accountPanel.getBorder();
            accountPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    tb.getTitle(),
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                    titleFont,
                    titleColor
            ));
            accountPanel.repaint();
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
    /*
    public static void main(String[] args) {
        GUI.utils.ThemeManager.initialize();
        SwingUtilities.invokeLater(() -> new UserDashboard(1)); // Example userId
    }
    */
}