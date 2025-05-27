package GUI.dialogs;

import javax.swing.*;
import java.awt.*;

public class IssueBookDialog extends JDialog {
    public IssueBookDialog(JFrame parent) {
        super(parent, "Issue Book", true);
        setSize(400, 300);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Issue Book functionality goes here.");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        add(closeButton, BorderLayout.SOUTH);

        setLocationRelativeTo(parent);
        setVisible(true);
    }
}