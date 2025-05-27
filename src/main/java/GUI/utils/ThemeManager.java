package GUI.utils;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;

public class ThemeManager {
    private static boolean isDarkMode = false;

    public static void initialize() {
        // Set default theme (light)
        setTheme(false);
    }

    public static void toggleTheme() {
        setTheme(!isDarkMode);
    }

    public static void setTheme(boolean darkMode) {
        try {
            if (darkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                isDarkMode = true;
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
                isDarkMode = false;
            }
            // Update all windows
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }
} 