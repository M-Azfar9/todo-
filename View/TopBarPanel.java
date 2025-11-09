package Todo.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 🎨 TopBarPanel - Premium Command Bridge
 * 
 * A production-ready top navigation bar featuring:
 * - Smooth animated search with real-time feedback
 * - Elegant theme toggle with fluid transitions
 * - Responsive hover states and micro-interactions
 * - Accessible keyboard navigation
 * - Professional spacing and alignment
 */
public class TopBarPanel extends JPanel {
    // Components
    private JTextField searchField;
    private JPanel searchPanel;
    private JPanel toggleButton;
    private JLabel titleLabel;
    private JLabel clearButton;
    
    // State management
    private boolean isHoveringToggle = false;
    private boolean isSearchFocused = false;
    private boolean isHoveringSearch = false;
    private int toggleAnimationProgress = 0;
    private Timer toggleAnimationTimer;
    
    // Callbacks
    private Runnable onThemeToggle;
    private Runnable onSearch;
    
    // Constants for better maintainability
    private static final int SEARCH_WIDTH = 420;
    private static final int SEARCH_HEIGHT = 44;
    private static final int TOGGLE_WIDTH = 56;
    private static final int TOGGLE_HEIGHT = 28;
    private static final int ANIMATION_DURATION = 200; // ms
    private static final int ANIMATION_STEPS = 20;

    public TopBarPanel(Runnable onThemeToggle, Runnable onSearch) {
        this.onThemeToggle = onThemeToggle;
        this.onSearch = onSearch;
        
        initializeLayout();
        createComponents();
        setupKeyboardShortcuts();
        updateTheme();
    }

    /**
     * 🏗️ Initialize the main layout structure
     */
    private void initializeLayout() {
        setLayout(new BorderLayout(20, 0));
        setBorder(new EmptyBorder(20, 30, 20, 30));
        setOpaque(true);
    }

    /**
     * 🎯 Create all UI components
     */
    private void createComponents() {
        // Left: Branding section
        JPanel leftPanel = createBrandingPanel();
        add(leftPanel, BorderLayout.WEST);
        
        // Right: Controls section (search + theme toggle)
        JPanel rightPanel = createControlsPanel();
        add(rightPanel, BorderLayout.EAST);
    }

    /**
     * 🎨 Create branding panel with app title and icon
     */
    private JPanel createBrandingPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        
        titleLabel = new JLabel("ProNote");
        
        // Load icon with fallback and error handling
        try {
            ImageIcon originalIcon = loadIcon();
            if (originalIcon != null) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                titleLabel.setIcon(new ImageIcon(scaledImage));
                titleLabel.setIconTextGap(12);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Icon loading failed: " + e.getMessage());
            // Graceful degradation - use emoji as fallback
            titleLabel.setText("📝 ProNote");
        }
        
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 28));
        titleLabel.setForeground(ThemeManager.getTextPrimary());
        
        panel.add(titleLabel);
        return panel;
    }

    /**
     * 🎯 Create controls panel (search + toggle)
     */
    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setOpaque(false);
        
        searchPanel = createEnhancedSearchPanel();
        toggleButton = createEnhancedToggleSwitch();
        
        panel.add(searchPanel);
        panel.add(toggleButton);
        
        return panel;
    }

    /**
     * 🔍 Create enhanced search panel with animations and feedback
     */
    private JPanel createEnhancedSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int width = getWidth();
                int height = getHeight();
                int arc = 12;

                // Enhanced shadow with glow effect when focused
                if (isSearchFocused) {
                    // Accent glow
                    g2.setColor(new Color(
                        ThemeManager.getAccent().getRed(),
                        ThemeManager.getAccent().getGreen(),
                        ThemeManager.getAccent().getBlue(),
                        40
                    ));
                    g2.fillRoundRect(0, 0, width, height, arc, arc);
                    
                    // Stronger shadow
                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.fillRoundRect(1, 1, width - 2, height - 2, arc, arc);
                } else {
                    // Subtle shadow
                    g2.setColor(ThemeManager.getShadow());
                    g2.fillRoundRect(2, 2, width - 4, height - 4, arc, arc);
                }

                // Background with hover effect
                Color bgColor = isHoveringSearch || isSearchFocused 
                    ? ThemeManager.getBgHover() 
                    : ThemeManager.getBgSecondary();
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, width - 4, height - 4, arc, arc);

                // Border
                Color borderColor = isSearchFocused 
                    ? ThemeManager.getAccent() 
                    : ThemeManager.getBorder();
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(isSearchFocused ? 2f : 1f));
                g2.drawRoundRect(0, 0, width - 4, height - 4, arc, arc);

                g2.dispose();
            }
        };
        
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(SEARCH_WIDTH, SEARCH_HEIGHT));
        panel.setBorder(new EmptyBorder(0, 15, 0, 15));

        // Left: Search icon
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        searchIcon.setBorder(new EmptyBorder(0, 0, 0, 8));
        panel.add(searchIcon, BorderLayout.WEST);

        // Center: Search field
        searchField = createSearchField();
        panel.add(searchField, BorderLayout.CENTER);

        // Right: Clear button (initially hidden)
        clearButton = createClearButton();
        panel.add(clearButton, BorderLayout.EAST);

        // Hover detection for the entire search panel
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHoveringSearch = true;
                panel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHoveringSearch = false;
                panel.repaint();
            }
        });

        return panel;
    }

    /**
     * 📝 Create the actual search text field
     */
    private JTextField createSearchField() {
        JTextField field = new JTextField("Search tasks...");
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new EmptyBorder(0, 0, 0, 0));
        field.setOpaque(false);
        field.setForeground(ThemeManager.getTextMuted());

        // Placeholder behavior
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isSearchFocused = true;
                if (field.getText().equals("Search tasks...")) {
                    field.setText("");
                    field.setForeground(ThemeManager.getTextPrimary());
                }
                searchPanel.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isSearchFocused = false;
                if (field.getText().isEmpty()) {
                    field.setText("Search tasks...");
                    field.setForeground(ThemeManager.getTextMuted());
                    clearButton.setVisible(false);
                }
                searchPanel.repaint();
            }
        });

        // Real-time search with debouncing
        field.getDocument().addDocumentListener(new DocumentListener() {
            private Timer debounceTimer;
            
            @Override
            public void insertUpdate(DocumentEvent e) { handleChange(); }
            
            @Override
            public void removeUpdate(DocumentEvent e) { handleChange(); }
            
            @Override
            public void changedUpdate(DocumentEvent e) { handleChange(); }
            
            private void handleChange() {
                String text = field.getText();
                clearButton.setVisible(!text.isEmpty() && !text.equals("Search tasks..."));
                
                // Debounce search execution (300ms delay)
                if (debounceTimer != null) {
                    debounceTimer.stop();
                }
                debounceTimer = new Timer(300, e -> {
                    if (onSearch != null && !text.equals("Search tasks...")) {
                        onSearch.run();
                    }
                });
                debounceTimer.setRepeats(false);
                debounceTimer.start();
            }
        });

        // Enter key triggers immediate search
        field.addActionListener(e -> {
            if (onSearch != null && !field.getText().equals("Search tasks...")) {
                onSearch.run();
            }
        });

        return field;
    }

    /**
     * ✖️ Create clear button for search field
     */
    private JLabel createClearButton() {
        JLabel button = new JLabel("✕");
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(ThemeManager.getTextMuted());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setVisible(false);
        button.setBorder(new EmptyBorder(0, 8, 0, 0));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(ThemeManager.getTextPrimary());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(ThemeManager.getTextMuted());
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                searchField.setText("");
                searchField.requestFocus();
                if (onSearch != null) {
                    onSearch.run();
                }
            }
        });
        
        return button;
    }

    /**
     * 🌓 Create enhanced toggle switch with smooth animation
     */
    private JPanel createEnhancedToggleSwitch() {
        JPanel toggle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int padding = 3;
                int knobSize = TOGGLE_HEIGHT - 2 * padding;

                // Background track with gradient
                Color trackColor = ThemeManager.isDarkMode() 
                    ? ThemeManager.getAccent() 
                    : new Color(220, 220, 225);

                if (isHoveringToggle) {
                    trackColor = ThemeManager.isDarkMode() 
                        ? ThemeManager.getAccentHover() 
                        : new Color(200, 200, 210);
                }

                // Track shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(1, 1, TOGGLE_WIDTH, TOGGLE_HEIGHT, TOGGLE_HEIGHT, TOGGLE_HEIGHT);

                // Track background
                g2.setColor(trackColor);
                g2.fillRoundRect(0, 0, TOGGLE_WIDTH, TOGGLE_HEIGHT, TOGGLE_HEIGHT, TOGGLE_HEIGHT);

                // Calculate knob position with animation
                int startX = padding;
                int endX = TOGGLE_WIDTH - knobSize - padding;
                int knobX = ThemeManager.isDarkMode() 
                    ? startX + (int)((endX - startX) * (toggleAnimationProgress / 100.0))
                    : endX - (int)((endX - startX) * (toggleAnimationProgress / 100.0));

                // Knob shadow for depth
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillOval(knobX + 2, padding + 2, knobSize, knobSize);

                // Knob with gradient
                GradientPaint knobGradient = new GradientPaint(
                    knobX, padding, Color.WHITE,
                    knobX, padding + knobSize, new Color(245, 245, 245)
                );
                g2.setPaint(knobGradient);
                g2.fillOval(knobX, padding, knobSize, knobSize);

                // Icon on knob
                g2.setColor(ThemeManager.isDarkMode() ? new Color(255, 215, 0) : new Color(255, 150, 0));
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
                String icon = ThemeManager.isDarkMode() ? "🌙" : "☀️";
                FontMetrics fm = g2.getFontMetrics();
                int iconX = knobX + (knobSize - fm.stringWidth(icon)) / 2;
                int iconY = padding + (knobSize + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(icon, iconX, iconY);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(TOGGLE_WIDTH, TOGGLE_HEIGHT);
            }
        };

        toggle.setOpaque(false);
        toggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggle.setToolTipText("Toggle theme (Ctrl+T)");

        // Hover and click interactions
        toggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHoveringToggle = true;
                toggle.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHoveringToggle = false;
                toggle.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                animateToggle();
                if (onThemeToggle != null) {
                    onThemeToggle.run();
                }
            }
        });

        return toggle;
    }

    /**
     * 🎬 Animate the toggle switch transition
     */
    private void animateToggle() {
        if (toggleAnimationTimer != null && toggleAnimationTimer.isRunning()) {
            return;
        }

        toggleAnimationProgress = 0;
        int delay = ANIMATION_DURATION / ANIMATION_STEPS;
        
        toggleAnimationTimer = new Timer(delay, e -> {
            toggleAnimationProgress += (100 / ANIMATION_STEPS);
            if (toggleAnimationProgress >= 100) {
                toggleAnimationProgress = 100;
                ((Timer) e.getSource()).stop();
            }
            toggleButton.repaint();
        });
        
        toggleAnimationTimer.start();
    }

    /**
     * ⌨️ Setup keyboard shortcuts for accessibility
     */
    private void setupKeyboardShortcuts() {
        // Ctrl+F or Cmd+F to focus search
        KeyStroke searchShortcut = KeyStroke.getKeyStroke(
            KeyEvent.VK_F, 
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
        );
        
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(searchShortcut, "focusSearch");
        getActionMap().put("focusSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.requestFocus();
                searchField.selectAll();
            }
        });

        // Ctrl+T or Cmd+T to toggle theme
        KeyStroke themeShortcut = KeyStroke.getKeyStroke(
            KeyEvent.VK_T,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
        );
        
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(themeShortcut, "toggleTheme");
        getActionMap().put("toggleTheme", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animateToggle();
                if (onThemeToggle != null) {
                    onThemeToggle.run();
                }
            }
        });

        // Escape to clear search
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        searchField.getInputMap(JComponent.WHEN_FOCUSED).put(escapeKey, "clearSearch");
        searchField.getActionMap().put("clearSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!searchField.getText().isEmpty() && !searchField.getText().equals("Search tasks...")) {
                    searchField.setText("");
                    if (onSearch != null) {
                        onSearch.run();
                    }
                }
            }
        });
    }

    /**
     * 🎨 Update all components when theme changes
     */
    public void updateTheme() {
        setBackground(ThemeManager.getBgPrimary());
        
        // Update title
        if (titleLabel != null) {
            titleLabel.setForeground(ThemeManager.getTextPrimary());
        }
        
        // Update search field
        if (searchField != null) {
            boolean isPlaceholder = searchField.getText().equals("Search tasks...");
            searchField.setForeground(isPlaceholder 
                ? ThemeManager.getTextMuted() 
                : ThemeManager.getTextPrimary());
            searchField.setCaretColor(ThemeManager.getTextPrimary());
        }
        
        // Update clear button
        if (clearButton != null && clearButton.isVisible()) {
            clearButton.setForeground(ThemeManager.getTextMuted());
        }
        
        // Repaint all components
        repaint();
        if (searchPanel != null) searchPanel.repaint();
        if (toggleButton != null) toggleButton.repaint();
    }

    /**
     * 🖼️ Load icon with multiple fallback strategies
     */
    private ImageIcon loadIcon() {
        // Strategy 1: Try classpath resource
        try {
            java.net.URL iconURL = getClass().getResource("/resources/tasks.png");
            if (iconURL != null) {
                return new ImageIcon(iconURL);
            }
        } catch (Exception e) {
            System.err.println("Classpath resource not found");
        }

        // Strategy 2: Try relative path
        try {
            java.io.File iconFile = new java.io.File("resources/tasks.png");
            if (iconFile.exists()) {
                return new ImageIcon(iconFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Relative path not found");
        }

        // Strategy 3: Try absolute path as last resort
        try {
            java.io.File iconFile = new java.io.File("D:/Java Applications/Todo/tasks.png");
            if (iconFile.exists()) {
                return new ImageIcon(iconFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Absolute path not found");
        }

        return null; // Will trigger emoji fallback
    }

    /**
     * 🔍 Get the current search query
     */
    public String getSearchQuery() {
        String text = searchField.getText();
        return text.equals("Search tasks...") ? "" : text;
    }

    /**
     * 🧹 Clear the search field programmatically
     */
    public void clearSearch() {
        searchField.setText("");
        clearButton.setVisible(false);
        if (onSearch != null) {
            onSearch.run();
        }
    }

    /**
     * 🎯 Programmatically focus the search field
     */
    public void focusSearch() {
        searchField.requestFocus();
        searchField.selectAll();
    }
}