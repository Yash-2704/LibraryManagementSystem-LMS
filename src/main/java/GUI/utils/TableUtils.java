package GUI.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
// import GUI.ThemeManager; // Old import
import GUI.utils.ThemeManager; // New import

public class TableUtils {

    public static void styleTable(JTable table, JScrollPane scrollPane) {

        // 2. Custom Font
        Font tableFont = new Font("Arial", Font.PLAIN, 14);
        table.setFont(tableFont);

        // 3. Table Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 15));
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus,
                                                         int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Color headerBg = UIManager.getColor("TableHeader.background");
                Color headerFg = UIManager.getColor("TableHeader.foreground");

                label.setBackground(headerBg != null ? headerBg : new Color(220, 225, 230)); 
                label.setForeground(headerFg != null ? headerFg : new Color(30, 30, 30));   
                label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10)); 
                label.setHorizontalAlignment(SwingConstants.LEADING); 
                return label;
            }
        };
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // 4. Row Striping (Alternate Row Coloring) and Cell Padding
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color EVEN_ROW_COLOR_LIGHT = new Color(242, 245, 247); 
            private final Color ODD_ROW_COLOR_LIGHT = Color.WHITE;
            private final Color EVEN_ROW_COLOR_DARK = UIManager.getColor("Table.alternateRowColor"); 
            private final Color ODD_ROW_COLOR_DARK = UIManager.getColor("Table.background");

            private final Color SELECTION_BACKGROUND_COLOR = UIManager.getColor("Table.selectionBackground");
            private final Color SELECTION_FOREGROUND_COLOR = UIManager.getColor("Table.selectionForeground");
            private final Color DEFAULT_FOREGROUND_LIGHT = UIManager.getColor("Table.foreground");
            private final Color DEFAULT_FOREGROUND_DARK = UIManager.getColor("Table.foreground");

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus,
                                                         int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setText(value == null ? "" : value.toString());
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10)); 
                setHorizontalAlignment(SwingConstants.LEADING); 

                boolean isDark = ThemeManager.isDarkMode();
                Color evenRowColor = isDark ? (EVEN_ROW_COLOR_DARK != null ? EVEN_ROW_COLOR_DARK : new Color(60, 63, 65)) : EVEN_ROW_COLOR_LIGHT;
                Color oddRowColor = isDark ? (ODD_ROW_COLOR_DARK != null ? ODD_ROW_COLOR_DARK : new Color(50,53,55)) : ODD_ROW_COLOR_LIGHT;

                if (isSelected) {
                    setBackground(SELECTION_BACKGROUND_COLOR);
                    setForeground(SELECTION_FOREGROUND_COLOR);
                } else {
                    setBackground(row % 2 == 0 ? evenRowColor : oddRowColor);
                    setForeground(isDark ? DEFAULT_FOREGROUND_DARK : DEFAULT_FOREGROUND_LIGHT); 
                }
                return this;
            }
        });

        // 5. Configure Grid Lines
        table.setShowGrid(true); // Show both horizontal and vertical grid lines
        Color gridColor = ThemeManager.isDarkMode() ? new Color(110, 110, 110) : new Color(210, 210, 210); // Made dark mode grid lines lighter
        table.setGridColor(gridColor);
        table.setIntercellSpacing(new Dimension(0, 0)); // Lines directly between cells

        // 6. Increase Row Height
        table.setRowHeight(table.getFontMetrics(tableFont).getHeight() + 14); 

        // 8. JScrollPane
        if (scrollPane != null) {
            scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
        }

        // 9. Enable Sorting
        table.setAutoCreateRowSorter(true);
    }
} 