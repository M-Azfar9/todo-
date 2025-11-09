package Todo.View;

import Todo.Models.*;
import Todo.DAO.*;
import Todo.Controller.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

/**
 * ✓ Enhanced TaskListPanel - Modern Layout with Integrated Action Buttons
 * 
 * NEW FEATURES:
 * - Buttons integrated in header row for better space utilization
 * - Professional horizontal layout: "All Tasks" on left, buttons on right
 * - Maintains full functionality with improved visual hierarchy
 * - Responsive design with proper spacing
 */
public class TaskListPanel extends JPanel {
    private TaskController controller;
    private JPanel tasksContainer;
    private JLabel headerLabel;
    private String currentCategoryName = "All Tasks";
    private Integer currentCategoryId = null;
    
    // Button references
    private ActionButton addBtn;
    private ActionButton clearBtn;
    
    public TaskListPanel(TaskController controller) {
        this.controller = controller;
        
        setLayout(new BorderLayout());
        setOpaque(true);
        
        // Main content area with padding
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        contentPanel.setOpaque(false);
        
        // ═══════════════════════════════════════════════════════════
        // NEW: Header Row with Integrated Buttons
        // ═══════════════════════════════════════════════════════════
        JPanel headerRow = new JPanel(new BorderLayout(20, 0));
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Left side: Task count header
        headerLabel = new JLabel("All Tasks");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(ThemeManager.getTextPrimary());
        headerRow.add(headerLabel, BorderLayout.WEST);
        
        // Right side: Action buttons
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonContainer.setOpaque(false);
        
        addBtn = new ActionButton("➕ Add Task", ButtonType.ADD, this::handleAddTask);
        clearBtn = new ActionButton("🗑️ Clear Done", ButtonType.DELETE, this::handleClearCompleted);
        
        buttonContainer.add(addBtn);
        buttonContainer.add(clearBtn);
        
        headerRow.add(buttonContainer, BorderLayout.EAST);
        
        contentPanel.add(headerRow);
        contentPanel.add(Box.createVerticalStrut(25));
        
        // Container for dynamic task cards
        tasksContainer = new JPanel();
        tasksContainer.setLayout(new BoxLayout(tasksContainer, BoxLayout.Y_AXIS));
        tasksContainer.setOpaque(false);
        tasksContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(tasksContainer);
        
        // Add glue to push tasks to top
        contentPanel.add(Box.createVerticalGlue());
        
        // Wrap in scroll pane for many tasks
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Custom scrollbar styling
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = ThemeManager.getBorder();
                this.trackColor = ThemeManager.getBgPrimary();
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createInvisibleButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createInvisibleButton();
            }
            
            private JButton createInvisibleButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Load initial tasks
        refreshTasks();
        
        updateTheme();
    }
    
    // ═══════════════════════════════════════════════════════════
    // Button Action Handlers (Integrated from ButtonPanel)
    // ═══════════════════════════════════════════════════════════
    
    /**
     * ➕ Handle Add Task
     */
    private void handleAddTask() {
        // Create modern dialog with form fields
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 2));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JTextField titleField = new JTextField(25);
        titleField.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 13));
        
        JTextArea descArea = new JTextArea(3, 25);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 13));
        JScrollPane descScroll = new JScrollPane(descArea);
        
        // Category dropdown
        java.util.List<Category> categories = controller.getAllCategories();
        JComboBox<String> categoryCombo = new JComboBox<>();
        categoryCombo.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 13));
        
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
        dueDateField.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 13));
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
        
        // Styled labels
        JLabel titleLbl = new JLabel("✏️ Task Title:");
        JLabel descLbl = new JLabel("📝 Description:");
        JLabel catLbl = new JLabel("📁 Category:");
        JLabel dateLbl = new JLabel("📅 Due Date:");
        
        Font labelFont = new Font("Segoe UI Symbol", Font.BOLD, 12);
        titleLbl.setFont(labelFont);
        descLbl.setFont(labelFont);
        catLbl.setFont(labelFont);
        dateLbl.setFont(labelFont);
        
        panel.add(titleLbl);
        panel.add(titleField);
        panel.add(descLbl);
        panel.add(descScroll);
        panel.add(catLbl);
        panel.add(categoryCombo);
        panel.add(dateLbl);
        panel.add(dueDateField);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Create New Task",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String description = descArea.getText().trim();
            int categoryId = categories.get(categoryCombo.getSelectedIndex()).getId();
            
            // Validation
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Task title cannot be empty!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            // Parse due date
            LocalDate dueDate = null;
            String dueDateText = dueDateField.getText();
            if (!dueDateText.isEmpty() && !dueDateText.equals("YYYY-MM-DD (optional)")) {
                try {
                    dueDate = LocalDate.parse(dueDateText);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Invalid date format. Please use YYYY-MM-DD",
                        "Date Format Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
            }
            
            // Create task through controller
            Task newTask = controller.addTask(title, description, categoryId, dueDate);
            
            if (newTask != null) {
                System.out.println("✓ Task created successfully: " + title);
                refreshTasks();
                
                // Success feedback
                JOptionPane.showMessageDialog(
                    this,
                    "Task \"" + title + "\" added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to create task. Please try again.",
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
        // Count completed tasks first
        List<Task> allTasks = controller.getAllTasks();
        long completedCount = allTasks.stream().filter(Task::isDone).count();
        
        if (completedCount == 0) {
            JOptionPane.showMessageDialog(
                this,
                "No completed tasks to clear.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete " + completedCount + " completed task(s)?\nThis action cannot be undone.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            int deletedCount = controller.deleteCompletedTasks();
            
            JOptionPane.showMessageDialog(
                this,
                "Successfully deleted " + deletedCount + " completed task(s).",
                "Deleted",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            refreshTasks();
        }
    }
    
    /**
     * Set current category (for adding tasks to correct category)
     */
    public void setCurrentCategory(Integer categoryId) {
        this.currentCategoryId = categoryId;
    }
    
    /**
     * 🔄 Refresh tasks from database
     * Loads tasks based on current category filter
     */
    public void refreshTasks() {
        tasksContainer.removeAll();
        
        // Get tasks from controller
        List<Task> tasks;
        if (currentCategoryId != null) {
            tasks = controller.getTasksByCategory(currentCategoryId);
        } else {
            tasks = controller.getAllTasks();
        }
        
        // Update header with task count
        headerLabel.setText(currentCategoryName + " (" + tasks.size() + ")");
        
        // Create task cards
        if (tasks.isEmpty()) {
            // Show empty state with icon
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setOpaque(false);
            emptyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            emptyPanel.setBorder(new EmptyBorder(40, 0, 0, 0));
            
            JLabel emptyIcon = new JLabel("📋");
            emptyIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 48));
            emptyIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel emptyLabel = new JLabel("No tasks yet. Click 'Add Task' to get started!");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            emptyLabel.setForeground(ThemeManager.getTextMuted());
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            emptyPanel.add(emptyIcon);
            emptyPanel.add(Box.createVerticalStrut(10));
            emptyPanel.add(emptyLabel);
            
            tasksContainer.add(emptyPanel);
        } else {
            for (Task task : tasks) {
                TaskCard taskCard = new TaskCard(task);
                tasksContainer.add(taskCard);
                tasksContainer.add(Box.createVerticalStrut(12));
            }
        }
        
        tasksContainer.revalidate();
        tasksContainer.repaint();
    }
    
    /**
     * Filter tasks by category
     */
    public void filterByCategory(int categoryId, String categoryName) {
        this.currentCategoryId = categoryId;
        this.currentCategoryName = categoryName;
        refreshTasks();
    }
    
    /**
     * Show all tasks
     */
    public void showAllTasks() {
        this.currentCategoryId = null;
        this.currentCategoryName = "All Tasks";
        refreshTasks();
    }
    
    /**
     * Search tasks
     */
    public void searchTasks(String query) {
        tasksContainer.removeAll();
        
        List<Task> tasks = controller.searchTasks(query);
        
        headerLabel.setText("🔍 Search Results (" + tasks.size() + ")");
        
        if (tasks.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setOpaque(false);
            emptyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            emptyPanel.setBorder(new EmptyBorder(40, 0, 0, 0));
            
            JLabel emptyIcon = new JLabel("🔍");
            emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            emptyIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel emptyLabel = new JLabel("No tasks found matching \"" + query + "\"");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            emptyLabel.setForeground(ThemeManager.getTextMuted());
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            emptyPanel.add(emptyIcon);
            emptyPanel.add(Box.createVerticalStrut(10));
            emptyPanel.add(emptyLabel);
            
            tasksContainer.add(emptyPanel);
        } else {
            for (Task task : tasks) {
                TaskCard taskCard = new TaskCard(task);
                tasksContainer.add(taskCard);
                tasksContainer.add(Box.createVerticalStrut(12));
            }
        }
        
        tasksContainer.revalidate();
        tasksContainer.repaint();
    }
    
    // ═══════════════════════════════════════════════════════════
    // Inner Classes: TaskCard, CustomCheckbox, ActionButton
    // ═══════════════════════════════════════════════════════════
    
    /**
     * 📝 Individual Task Card (WITH DATABASE INTEGRATION!)
     */
    private class TaskCard extends JPanel {
        private final Task task;
        private boolean isHovered = false;
        private JLabel textLabel;
        private CustomCheckbox checkbox;
        
        public TaskCard(Task task) {
            this.task = task;
            
            setLayout(new BorderLayout(15, 0));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            setPreferredSize(new Dimension(600, 70));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(15, 20, 15, 20));
            
            // Checkbox on left
            checkbox = new CustomCheckbox(task.isDone());
            add(checkbox, BorderLayout.WEST);
            
            // Task content in center
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setOpaque(false);
            
            // Task title
            String displayText = task.getTitle();
            if (task.isDone()) {
                displayText = "<html><strike>" + displayText + "</strike></html>";
            }
            
            textLabel = new JLabel(displayText);
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            textLabel.setForeground(task.isDone() ? 
                ThemeManager.getTextMuted() : ThemeManager.getTextPrimary());
            textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            centerPanel.add(textLabel);
            
            // Task metadata (category + due date)
            if (task.getDueDate() != null) {
                JLabel metaLabel = new JLabel("📅 " + task.getFormattedDueDate());
                metaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                metaLabel.setForeground(task.isOverdue() ? 
                    ThemeManager.getButtonDelete() : ThemeManager.getTextMuted());
                metaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                centerPanel.add(Box.createVerticalStrut(3));
                centerPanel.add(metaLabel);
            }
            
            add(centerPanel, BorderLayout.CENTER);
            
            // Status indicator on right
            if (task.isOverdue()) {
                JLabel statusLabel = new JLabel("🔴");
                statusLabel.setToolTipText("Overdue");
                statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                add(statusLabel, BorderLayout.EAST);
            } else if (task.isDueSoon()) {
                JLabel statusLabel = new JLabel("🟡");
                statusLabel.setToolTipText("Due soon");
                statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                add(statusLabel, BorderLayout.EAST);
            }
            
            // Click to toggle completion
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggleCompletion();
                }
            });
        }
        
        /**
         * Toggle completion (SAVES TO DATABASE!)
         */
        private void toggleCompletion() {
            // Update database through controller
            boolean success = controller.toggleTaskDone(task.getId());
            
            if (success) {
                // Update local task object
                task.toggleDone();
                
                // Update UI
                checkbox.setChecked(task.isDone());
                
                if (task.isDone()) {
                    textLabel.setText("<html><strike>" + task.getTitle() + "</strike></html>");
                    textLabel.setForeground(ThemeManager.getTextMuted());
                } else {
                    textLabel.setText(task.getTitle());
                    textLabel.setForeground(ThemeManager.getTextPrimary());
                }
                
                repaint();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to update task status",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Shadow layer (offset slightly)
            g2.setColor(ThemeManager.getShadow());
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
            
            // Main card background
            Color bgColor = ThemeManager.getBgSecondary();
            if (isHovered) {
                bgColor = ThemeManager.getBgHover();
            }
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);
            
            // Subtle border
            g2.setColor(ThemeManager.getBorder());
            g2.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);
            
            // Hover glow effect
            if (isHovered) {
                g2.setColor(new Color(
                    ThemeManager.getAccent().getRed(),
                    ThemeManager.getAccent().getGreen(),
                    ThemeManager.getAccent().getBlue(),
                    30
                ));
                g2.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);
            }
            
            g2.dispose();
        }
    }
    
    /**
     * ☑️ Custom Checkbox
     */
    private class CustomCheckbox extends JPanel {
        private boolean isChecked = false;
        
        public CustomCheckbox(boolean isChecked) {
            this.isChecked = isChecked;
            setOpaque(false);
            setPreferredSize(new Dimension(22, 22));
        }
        
        public void setChecked(boolean checked) {
            this.isChecked = checked;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int size = 20;
            
            if (isChecked) {
                // Filled checkbox with accent color
                g2.setColor(ThemeManager.getAccent());
                g2.fillRoundRect(0, 0, size, size, 6, 6);
                
                // White checkmark
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // Draw checkmark path
                int[] xPoints = {4, 9, 16};
                int[] yPoints = {10, 15, 6};
                g2.drawPolyline(xPoints, yPoints, 3);
            } else {
                // Empty checkbox with border
                g2.setColor(ThemeManager.getBorder());
                g2.fillRoundRect(0, 0, size, size, 6, 6);
                
                g2.setColor(ThemeManager.getBgSecondary());
                g2.fillRoundRect(2, 2, size - 4, size - 4, 4, 4);
            }
            
            g2.dispose();
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
     * 🔘 Custom Action Button (Integrated from ButtonPanel)
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
            setPreferredSize(new Dimension(140, 40));
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
            g2.setFont(new Font("Segoe UI Symbol", Font.BOLD, 12));
            
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
     * 🎨 Update all components when theme changes
     */
    public void updateTheme() {
        setBackground(ThemeManager.getBgPrimary());
        headerLabel.setForeground(ThemeManager.getTextPrimary());
        
        // Update buttons
        if (addBtn != null) addBtn.repaint();
        if (clearBtn != null) clearBtn.repaint();
        
        // Update scrollpane viewport
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                scrollPane.getViewport().setBackground(ThemeManager.getBgPrimary());
            }
        }
        
        repaint();
    }
}