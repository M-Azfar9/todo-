package Todo.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Todo.Models.*;
import Todo.DAO.*;
import Todo.Controller.*;

/**
 * 🚀 Modern Sidebar - Completely Redesigned
 * 
 * New Structure:
 * - Floating card-based categories with glassmorphism
 * - Compact, icon-first design for cleaner look
 * - Split view: Quick stats above, categories below
 * - Modern material design with depth and shadows
 * - Smooth spring animations
 */
public class SidebarPanel extends JPanel {
    private String selectedCategory = "Work";
    private int selectedCategoryId = 1;
    private List<ModernCategoryCard> categoryCards;
    private JPanel cardsContainer;
    private TaskController controller;
    private Runnable onCategoryChange;
    private StatsCard statsCard;
    
    // Modern color palette
    private static final Color GLASS_OVERLAY = new Color(255, 255, 255, 10);
    private static final Color CARD_SHADOW = new Color(0, 0, 0, 8);
    
    public SidebarPanel(TaskController controller, Runnable onCategoryChange) {
        this.controller = controller;
        this.onCategoryChange = onCategoryChange;
        this.categoryCards = new ArrayList<>();
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 0));
        
        // Main scrollable content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(24, 20, 24, 20));
        
        // Compact header
        JPanel header = createCompactHeader();
        mainContent.add(header);
        mainContent.add(Box.createVerticalStrut(24));
        
        // Stats card at top
        statsCard = new StatsCard();
        mainContent.add(statsCard);
        mainContent.add(Box.createVerticalStrut(24));
        
        // Section divider
        mainContent.add(createSectionLabel("Your Collections"));
        mainContent.add(Box.createVerticalStrut(16));
        
        // Cards container with modern grid
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);
        mainContent.add(cardsContainer);
        
        loadCategories();
        
        mainContent.add(Box.createVerticalStrut(12));
        
        // Modern floating add button
        FloatingAddButton addBtn = new FloatingAddButton();
        mainContent.add(addBtn);
        
        mainContent.add(Box.createVerticalGlue());
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        updateTheme();
    }
    
    /**
     * 🎯 Minimal header with just the essentials
     */
    private JPanel createCompactHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        // Left: App branding
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JLabel icon = new JLabel("✓");
        icon.setFont(new Font("Segoe UI Symbol", Font.BOLD, 24));
        icon.setForeground(ThemeManager.getAccent());
        
        JLabel appName = new JLabel("  TaskFlow");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appName.setForeground(ThemeManager.getTextPrimary());
        
        leftPanel.add(icon);
        leftPanel.add(appName);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    /**
     * 📊 Stats Card - Glassmorphic design with key metrics
     */
    private class StatsCard extends JPanel {
        private JLabel totalLabel;
        private JLabel completedLabel;
        private JLabel progressLabel;
        
        public StatsCard() {
            setLayout(new BorderLayout(16, 0));
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
            setPreferredSize(new Dimension(260, 90));
            setBorder(new EmptyBorder(16, 20, 16, 20));
            
            TaskController.TaskStats stats = controller.getStatistics();
            
            // Left side - metrics
            JPanel metricsPanel = new JPanel();
            metricsPanel.setLayout(new BoxLayout(metricsPanel, BoxLayout.Y_AXIS));
            metricsPanel.setOpaque(false);
            
            JLabel todayLabel = new JLabel("Today");
            todayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            todayLabel.setForeground(ThemeManager.getTextMuted());
            
            totalLabel = new JLabel(stats.total + " tasks");
            totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            totalLabel.setForeground(ThemeManager.getTextPrimary());
            
            completedLabel = new JLabel(stats.completed + " completed");
            completedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            completedLabel.setForeground(ThemeManager.getTextSecondary());
            
            metricsPanel.add(todayLabel);
            metricsPanel.add(Box.createVerticalStrut(4));
            metricsPanel.add(totalLabel);
            metricsPanel.add(Box.createVerticalStrut(2));
            metricsPanel.add(completedLabel);
            
            // Right side - circular progress
            CircularProgress circularProgress = new CircularProgress(stats.getCompletionPercentage());
            
            add(metricsPanel, BorderLayout.CENTER);
            add(circularProgress, BorderLayout.EAST);
        }
        
        public void updateStats() {
            TaskController.TaskStats stats = controller.getStatistics();
            totalLabel.setText(stats.total + " tasks");
            completedLabel.setText(stats.completed + " completed");
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Glass card background
            g2.setColor(GLASS_OVERLAY);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            
            // Gradient border
            GradientPaint borderGradient = new GradientPaint(
                0, 0, new Color(ThemeManager.getAccent().getRed(), 
                    ThemeManager.getAccent().getGreen(), 
                    ThemeManager.getAccent().getBlue(), 60),
                getWidth(), getHeight(), new Color(ThemeManager.getAccent().getRed(), 
                    ThemeManager.getAccent().getGreen(), 
                    ThemeManager.getAccent().getBlue(), 20)
            );
            g2.setPaint(borderGradient);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            
            g2.dispose();
        }
    }
    
    /**
     * ⭕ Circular Progress Indicator
     */
    private class CircularProgress extends JPanel {
        private double percentage;
        
        public CircularProgress(double percentage) {
            this.percentage = percentage;
            setOpaque(false);
            setPreferredSize(new Dimension(50, 50));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int size = 50;
            int strokeWidth = 4;
            
            // Background circle
            g2.setColor(new Color(ThemeManager.getTextMuted().getRGB() & 0x00FFFFFF | (30 << 24), true));
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.drawOval(strokeWidth, strokeWidth, size - strokeWidth * 2, size - strokeWidth * 2);
            
            // Progress arc
            g2.setColor(ThemeManager.getAccent());
            int angle = (int) (360 * (percentage / 100.0));
            g2.drawArc(strokeWidth, strokeWidth, size - strokeWidth * 2, size - strokeWidth * 2, 90, -angle);
            
            // Percentage text
            String text = (int) percentage + "%";
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            g2.setColor(ThemeManager.getTextPrimary());
            g2.drawString(text, (size - textWidth) / 2, (size + textHeight / 2) / 2);
            
            g2.dispose();
        }
    }
    
    /**
     * 🏷️ Modern section label
     */
    private JPanel createSectionLabel(String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(ThemeManager.getTextMuted());
        
        panel.add(label);
        return panel;
    }
    
    /**
     * 🔄 Load categories as modern cards
     */
    private void loadCategories() {
        cardsContainer.removeAll();
        categoryCards.clear();
        
        List<Category> categories = controller.getAllCategories();
        
        for (Category category : categories) {
            ModernCategoryCard card = new ModernCategoryCard(
                category.getDisplayText(),
                category.getName(),
                category.getId()
            );
            
            if (categoryCards.isEmpty()) {
                card.setSelected(true);
                selectedCategory = category.getName();
                selectedCategoryId = category.getId();
            }
            
            categoryCards.add(card);
            cardsContainer.add(card);
            cardsContainer.add(Box.createVerticalStrut(10));
        }
        
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }
    
    public void refreshCategories() {
        loadCategories();
        if (statsCard != null) {
            statsCard.updateStats();
        }
        updateTheme();
    }
    
    public int getSelectedCategoryId() {
        return selectedCategoryId;
    }
    
    public String getSelectedCategoryName() {
        return selectedCategory;
    }
    
    /**
     * 🎨 Modern minimal background
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Subtle gradient - less dramatic
        Color top = ThemeManager.getSidebarTop();
        Color bottom = new Color(
            Math.max(0, top.getRed() - 8),
            Math.max(0, top.getGreen() - 8),
            Math.max(0, top.getBlue() - 8)
        );
        
        GradientPaint gradient = new GradientPaint(
            0, 0, top,
            0, getHeight(), bottom
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Minimal border
        g2.setColor(new Color(ThemeManager.getBorder().getRed(),
            ThemeManager.getBorder().getGreen(),
            ThemeManager.getBorder().getBlue(), 100));
        g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
        
        g2.dispose();
    }
    
    /**
     * 🎴 Modern Category Card - Compact horizontal layout
     */
    private class ModernCategoryCard extends JPanel {
        private final String label;
        private final String category;
        private final int categoryId;
        private boolean isSelected = false;
        private boolean isHovered = false;
        private float animProgress = 0.0f;
        private Timer animTimer;
        
        public ModernCategoryCard(String label, String category, int categoryId) {
            this.label = label;
            this.category = category;
            this.categoryId = categoryId;
            
            setLayout(new BorderLayout(12, 0));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
            setPreferredSize(new Dimension(260, 56));
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 16, 0, 16));
            
            // Left: Icon + Text
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            leftPanel.setOpaque(false);
            leftPanel.setBorder(new EmptyBorder(16, 0, 16, 0));
            
            // Category icon
            String emoji = label.split(" ")[0];
            JLabel iconLabel = new JLabel(emoji);
            iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
            
            // Category name
            String name = label.substring(emoji.length()).trim();
            JLabel nameLabel = new JLabel(name);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setForeground(ThemeManager.getTextSecondary());
            
            leftPanel.add(iconLabel);
            leftPanel.add(nameLabel);
            
            // Right: Count badge
            int taskCount = controller.getTasksByCategory(categoryId).size();
            JLabel countLabel = new JLabel(String.valueOf(taskCount));
            countLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            countLabel.setForeground(ThemeManager.getTextMuted());
            countLabel.setBorder(new EmptyBorder(16, 0, 16, 12));
            
            add(leftPanel, BorderLayout.CENTER);
            add(countLabel, BorderLayout.EAST);
            
            // Store reference to name label for color updates
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    animate(true);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    animate(false);
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectCard();
                }
            });
        }
        
        private void animate(boolean forward) {
            if (animTimer != null) animTimer.stop();
            
            animTimer = new Timer(16, e -> {
                if (forward) {
                    animProgress = Math.min(1.0f, animProgress + 0.1f);
                } else {
                    animProgress = Math.max(0.0f, animProgress - 0.1f);
                }
                repaint();
                
                if ((forward && animProgress >= 1.0f) || (!forward && animProgress <= 0.0f)) {
                    animTimer.stop();
                }
            });
            animTimer.start();
        }
        
        private void selectCard() {
            for (ModernCategoryCard card : categoryCards) {
                card.setSelected(false);
            }
            
            setSelected(true);
            selectedCategory = category;
            selectedCategoryId = categoryId;
            
            if (onCategoryChange != null) {
                onCategoryChange.run();
            }
        }
        
        public void setSelected(boolean selected) {
            this.isSelected = selected;
            
            // Update text colors
            Component[] comps = ((JPanel) getComponent(0)).getComponents();
            for (Component c : comps) {
                if (c instanceof JLabel) {
                    JLabel lbl = (JLabel) c;
                    if (!lbl.getText().matches("[\\p{So}\\p{Cn}]+")) { // Not emoji
                        lbl.setForeground(selected ? 
                            ThemeManager.getTextPrimary() : ThemeManager.getTextSecondary());
                    }
                }
            }
            
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Elevated card background
            if (isSelected || animProgress > 0) {
                // Shadow
                g2.setColor(new Color(0, 0, 0, (int)(12 * (isSelected ? 1 : animProgress))));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 2, 12, 12);
                
                // Card background
                Color cardBg = isSelected ? 
                    new Color(ThemeManager.getAccent().getRed(),
                        ThemeManager.getAccent().getGreen(),
                        ThemeManager.getAccent().getBlue(), 25) :
                    new Color(255, 255, 255, (int)(8 * animProgress));
                
                g2.setColor(cardBg);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);
                
                // Accent border when selected
                if (isSelected) {
                    g2.setColor(new Color(ThemeManager.getAccent().getRed(),
                        ThemeManager.getAccent().getGreen(),
                        ThemeManager.getAccent().getBlue(), 120));
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);
                }
            }
            
            g2.dispose();
        }
    }
    
    /**
     * ➕ Floating Add Button - Modern circular design
     */
    private class FloatingAddButton extends JPanel {
        private boolean isHovered = false;
        private float scale = 1.0f;
        private Timer scaleTimer;
        
        public FloatingAddButton() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
            setPreferredSize(new Dimension(260, 56));
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            JLabel plusIcon = new JLabel("+");
            plusIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
            plusIcon.setForeground(ThemeManager.getAccent());
            
            JLabel textLabel = new JLabel("New Collection");
            textLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            textLabel.setForeground(ThemeManager.getTextSecondary());
            textLabel.setBorder(new EmptyBorder(0, 8, 0, 0));
            
            add(plusIcon);
            add(textLabel);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    textLabel.setForeground(ThemeManager.getAccent());
                    animateScale(1.05f);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    textLabel.setForeground(ThemeManager.getTextSecondary());
                    animateScale(1.0f);
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    showAddDialog();
                }
            });
        }
        
        private void animateScale(float target) {
            if (scaleTimer != null) scaleTimer.stop();
            
            scaleTimer = new Timer(16, e -> {
                if (Math.abs(scale - target) < 0.01f) {
                    scale = target;
                    scaleTimer.stop();
                } else {
                    scale += (target - scale) * 0.2f;
                }
                repaint();
            });
            scaleTimer.start();
        }
        
        private void showAddDialog() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            JTextField nameField = new JTextField(15);
            JTextField iconField = new JTextField("📁", 3);
            
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Icon:"), gbc);
            gbc.gridx = 1;
            panel.add(iconField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1;
            panel.add(nameField, gbc);
            
            int result = JOptionPane.showConfirmDialog(this, panel, 
                "Create New Collection", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String icon = iconField.getText().trim();
                
                if (!name.isEmpty()) {
                    Category newCat = controller.addCategory(name, icon.isEmpty() ? "📁" : icon);
                    if (newCat != null) {
                        refreshCategories();
                    }
                }
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Apply scale transform
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            g2.translate(centerX, centerY);
            g2.scale(scale, scale);
            g2.translate(-centerX, -centerY);
            
            // Dashed border
            float[] dash = {5f, 5f};
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, 
                BasicStroke.JOIN_ROUND, 1.0f, dash, 0f));
            g2.setColor(new Color(ThemeManager.getAccent().getRed(),
                ThemeManager.getAccent().getGreen(),
                ThemeManager.getAccent().getBlue(), isHovered ? 180 : 100));
            g2.drawRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 28, 28);
            
            // Hover fill
            if (isHovered) {
                g2.setColor(new Color(ThemeManager.getAccent().getRed(),
                    ThemeManager.getAccent().getGreen(),
                    ThemeManager.getAccent().getBlue(), 15));
                g2.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 28, 28);
            }
            
            g2.dispose();
        }
    }
    
    public void updateTheme() {
        repaint();
        if (statsCard != null) {
            statsCard.repaint();
        }
        for (ModernCategoryCard card : categoryCards) {
            card.repaint();
        }
    }
}