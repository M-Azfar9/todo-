package Todo.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import Todo.Models.*;
import Todo.Controller.*;
/**
 * 🎮 ButtonPanel - Action Command Center (WITH BACKEND!)
 * 
 * All buttons now perform real database operations through the controller.
 * Includes dialogs for adding and editing tasks.
 */
public class ButtonPanel extends JPanel {
    private TaskController controller;
    private Runnable onTaskChange;  // Callback to refresh UI
    private Integer currentCategoryId;
    
    public ButtonPanel(TaskController controller, Runnable onTaskChange) {
        this.controller = controller;
        this.onTaskChange = onTaskChange;
        
        setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        setBorder(new EmptyBorder(20, 30, 30, 30));
        setOpaque(false);
        
        // Action buttons
        ActionButton addBtn = new ActionButton("Add Task", ButtonType.ADD, this::handleAddTask);
        ActionButton clearBtn = new ActionButton("Clear Done", ButtonType.DELETE, this::handleClearCompleted);
        
        add(addBtn);
        add(clearBtn);
        
        updateTheme();
    }
    
    /**
     * Set current category (for adding tasks to correct category)
     */
    public void setCurrentCategory(int categoryId) {
        this.currentCategoryId = categoryId;
    }
    
    /**
     * ➕ Handle Add Task
     */
    private void handleAddTask() {
        // Create dialog with form fields
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        
        JTextField titleField = new JTextField(20);
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        
        // Category dropdown
        java.util.List<Category> categories = controller.getAllCategories();
        JComboBox<String> categoryCombo = new JComboBox<>();
        for (Category cat : categories) {
            categoryCombo.addItem(cat.getDisplayText());
        }
        // Pre-select current category if set
        if (currentCategoryId != null) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == currentCategoryId) {
                    categoryCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        JTextField dueDateField = new JTextField("YYYY-MM-DD (optional)");
        dueDateField.setForeground(Color.GRAY);
        dueDateField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (dueDateField.getText().equals("YYYY-MM-DD (optional)")) {
                    dueDateField.setText("");
                    dueDateField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (dueDateField.getText().isEmpty()) {
                    dueDateField.setText("YYYY-MM-DD (optional)");
                    dueDateField.setForeground(Color.GRAY);
                }
            }
        });
        
        panel.add(new JLabel("Task Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Description:"));
        panel.add(descScroll);
        panel.add(new JLabel("Category:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Due Date:"));
        panel.add(dueDateField);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Add New Task",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String description = descArea.getText().trim();
            int categoryId = categories.get(categoryCombo.getSelectedIndex()).getId();
            
            // Parse due date
            LocalDate dueDate = null;
            String dueDateText = dueDateField.getText();
            if (!dueDateText.isEmpty() && !dueDateText.equals("YYYY-MM-DD (optional)")) {
                try {
                    dueDate = LocalDate.parse(dueDateText);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Invalid date format. Use YYYY-MM-DD",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
            }
            
            // Create task through controller
            Task newTask = controller.addTask(title, description, categoryId, dueDate);
            
            if (newTask != null) {
                System.out.println("✓ Task created successfully!");
                if (onTaskChange != null) {
                    onTaskChange.run();  // Refresh UI
                }
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to create task. Please check the title.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**
     * 🗑️ Handle Clear Completed Tasks
     */
    private void handleClearCompleted() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete all completed tasks? This cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            int deletedCount = controller.deleteCompletedTasks();
            
            JOptionPane.showMessageDialog(
                this,
                deletedCount + " completed task(s) deleted.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            if (onTaskChange != null) {
                onTaskChange.run();  // Refresh UI
            }
        }
    }
    
    /**
     * 🎨 Button types for different color schemes
     */
    private enum ButtonType {
        ADD,      // Green accent
        DELETE,   // Red accent
        NORMAL    // Standard accent
    }
    
    /**
     * 🔘 Custom Action Button (WITH CLICK HANDLERS!)
     */
    private class ActionButton extends JPanel {
        private final String text;
        private final ButtonType type;
        private final Runnable onClick;
        private boolean isHovered = false;
        private boolean isPressed = false;
        
        public ActionButton(String text, ButtonType type, Runnable onClick) {
            this.text = text;
            this.type = type;
            this.onClick = onClick;
            
            setOpaque(false);
            setPreferredSize(new Dimension(150, 40));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Mouse interactions
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    isPressed = false;
                    repaint();
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (onClick != null) {
                        onClick.run();
                    }
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Slight scale effect when pressed
            int offset = isPressed ? 2 : 0;
            int drawWidth = width - offset;
            int drawHeight = height - offset;
            
            // Determine button colors
            Color baseColor, hoverColor;
            
            switch (type) {
                case ADD:
                    baseColor = ThemeManager.getButtonAdd();
                    hoverColor = ThemeManager.getButtonAddHover();
                    break;
                case DELETE:
                    baseColor = ThemeManager.getButtonDelete();
                    hoverColor = ThemeManager.getButtonDeleteHover();
                    break;
                default:
                    baseColor = ThemeManager.getAccent();
                    hoverColor = ThemeManager.getAccentHover();
            }
            
            Color currentColor = isHovered ? hoverColor : baseColor;
            
            // Shadow (if not pressed)
            if (!isPressed) {
                g2.setColor(ThemeManager.getShadowStrong());
                g2.fillRoundRect(offset + 2, offset + 2, drawWidth - 2, drawHeight - 2, 10, 10);
            }
            
            // Button background with gradient
            GradientPaint gradient = new GradientPaint(
                0, offset, currentColor,
                0, offset + drawHeight, darkenColor(currentColor, 0.9f)
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(offset, offset, drawWidth - 2, drawHeight - 2, 10, 10);
            
            // Subtle highlight on top
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillRoundRect(offset, offset, drawWidth - 2, drawHeight / 3, 10, 10);
            
            // Button text
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            
            FontMetrics fm = g2.getFontMetrics();
            int textX = (width - fm.stringWidth(text)) / 2 + offset;
            int textY = (height + fm.getAscent() - fm.getDescent()) / 2 + offset;
            
            g2.drawString(text, textX, textY);
            
            g2.dispose();
        }
        
        private Color darkenColor(Color color, float factor) {
            return new Color(
                Math.max((int)(color.getRed() * factor), 0),
                Math.max((int)(color.getGreen() * factor), 0),
                Math.max((int)(color.getBlue() * factor), 0),
                color.getAlpha()
            );
        }
    }
    
    /**
     * 🎨 Update theme for all buttons
     */
    public void updateTheme() {
        repaint();
    }
}