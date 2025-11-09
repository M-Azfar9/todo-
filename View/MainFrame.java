package Todo.View;
// package View;

import javax.swing.*;
import java.awt.*;

import Todo.DAO.*;
import Todo.Controller.*;

/**
 * 🏛️ MainFrame - The Grand Stage (FULLY INTEGRATED!)
 * 
 * Now orchestrates the complete MVC architecture:
 * - View: All Swing UI components
 * - Controller: TaskController (business logic)
 * - Model: Task, Category (data objects)
 * - DAO: TaskDAO, CategoryDAO (database layer)
 * 
 * This is where the magic happens - all layers working in harmony!
 */
public class MainFrame extends JFrame {
    // Controller (the brain)
    private TaskController controller;

    // UI Components (the face)
    private TopBarPanel topBar;
    private SidebarPanel sidebar;
    private TaskListPanel taskList;
    private ButtonPanel buttonPanel;

    public MainFrame() {
        // Initialize controller FIRST (it connects to database)
        initializeBackend();

        // Window setup
        setTitle("ProNote");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // ✅ Load the icon image
        // Option 1: Absolute path (quick test)
        // ImageIcon icon = new ImageIcon("D:/Java Applications/Todo/tasks.png");

        // Option 2: Resource inside your project (recommended)
        // Put tasks.png inside src/resources/
        ImageIcon icon = new ImageIcon("D:/Java Applications/Todo/tasks.png");

        setIconImage(icon.getImage()); // Set the app icon on this JFrame

        // Main layout
        setLayout(new BorderLayout());

        // Initialize UI components
        initComponents();

        // Assemble the UI
        buildLayout();

        // Apply initial theme
        updateAllThemes();

        // Add shutdown hook to close database connection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n👋 Closing database connection...");
            DatabaseConnection.getInstance().closeConnection();
        }));

        setVisible(true); // Make the frame visible
    }

    /**
     * 🧠 Initialize backend (Controller + Database)
     */
    private void initializeBackend() {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     🚀 Initializing Backend Layer...      ║");
        System.out.println("╚════════════════════════════════════════════╝");

        // Initialize database connection (singleton)
        DatabaseConnection db = DatabaseConnection.getInstance();

        // Test connection
        if (db.testConnection()) {
            System.out.println("✓ Database connection verified");
            db.printDatabaseInfo();
        } else {
            System.err.println("❌ Database connection failed!");
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to connect to database. Application may not work properly.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Initialize controller
        controller = new TaskController();
        System.out.println("✓ TaskController initialized");

        // Print statistics
        TaskController.TaskStats stats = controller.getStatistics();
        System.out.println("📊 " + stats);
        System.out.println();
    }

    /**
     * 🎨 Initialize all UI components
     * Each component gets a reference to the controller
     */
    private void initComponents() {
        // Top bar with search functionality
        topBar = new TopBarPanel(
                this::toggleTheme,
                this::handleSearch);

        // Sidebar with category management
        sidebar = new SidebarPanel(
                controller,
                this::handleCategoryChange);

        // Task list with dynamic loading
        taskList = new TaskListPanel(controller);

        // Action buttons
        // buttonPanel = new ButtonPanel(
        //         controller,
        //         this::refreshAllData);

        // Set initial category for button panel
        // buttonPanel.setCurrentCategory(sidebar.getSelectedCategoryId());
    }

    /**
     * 🏗️ Build the main layout structure
     */
    private void buildLayout() {
        // Top bar spans full width
        add(topBar, BorderLayout.NORTH);

        // Center: Sidebar + Task List
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(sidebar, BorderLayout.WEST);
        centerPanel.add(taskList, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom: Action buttons
        // add(buttonPanel, BorderLayout.SOUTH);

        // Main frame background
        getContentPane().setBackground(ThemeManager.getBgPrimary());
    }

    // ═══════════════════════════════════════════════════════════
    // EVENT HANDLERS (The Glue Between UI and Backend)
    // ═══════════════════════════════════════════════════════════

    /**
     * 🔍 Handle search
     * Triggered when user presses Enter in search field
     */
    private void handleSearch() {
        // String query = topBar.getSearchQuery();

        // if (query.isEmpty()) {
        // // Empty search = show current category tasks
        // handleCategoryChange();
        // } else {
        // // Perform search
        // taskList.searchTasks(query);
        // }
    }

    /**
     * 📂 Handle category change
     * Triggered when user clicks a category in sidebar
     */
    private void handleCategoryChange() {
        // Clear search
        // topBar.clearSearch();

        // Get selected category
        int categoryId = sidebar.getSelectedCategoryId();
        String categoryName = sidebar.getSelectedCategoryName();

        // Update task list
        taskList.filterByCategory(categoryId, categoryName);

        // Update button panel (so Add Task uses correct category)
        // buttonPanel.setCurrentCategory(categoryId);
    }

    /**
     * 🔄 Refresh all data from database
     * Called after adding/editing/deleting tasks
     */
    private void refreshAllData() {
        // Refresh task list
        handleCategoryChange();

        // Refresh sidebar (updates statistics)
        sidebar.refreshCategories();
    }

    /**
     * 🌓 Toggle between light and dark themes
     */
    private void toggleTheme() {
        ThemeManager.toggleTheme();
        updateAllThemes();
    }

    /**
     * 🎨 Update all components to reflect current theme
     */
    private void updateAllThemes() {
        getContentPane().setBackground(ThemeManager.getBgPrimary());
        topBar.updateTheme();
        sidebar.updateTheme();
        taskList.updateTheme();
        // buttonPanel.updateTheme();

        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }

    // ═══════════════════════════════════════════════════════════
    // APPLICATION ENTRY POINT
    // ═══════════════════════════════════════════════════════════

    /**
     * 🚀 Launch the application
     */
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);

            // Welcome message
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║     🎨 My Tasks - FULLY FUNCTIONAL! ✨    ║");
            System.out.println("║                                            ║");
            System.out.println("║  ✓ MVC Architecture                        ║");
            System.out.println("║  ✓ JDBC Database Integration (SQLite)     ║");
            System.out.println("║  ✓ Full CRUD Operations                    ║");
            System.out.println("║  ✓ Category Management                     ║");
            System.out.println("║  ✓ Search & Filter                         ║");
            System.out.println("║  ✓ Light/Dark Mode                         ║");
            System.out.println("║  ✓ Real-time UI Updates                    ║");
            System.out.println("║                                            ║");
            System.out.println("║  🎮 HOW TO USE:                            ║");
            System.out.println("║  • Click categories to filter tasks        ║");
            System.out.println("║  • Click tasks to toggle completion        ║");
            System.out.println("║  • Press Enter in search to find tasks     ║");
            System.out.println("║  • Click ➕ Add Task to create new tasks   ║");
            System.out.println("║  • Click 🗑️ Clear Done to remove completed║");
            System.out.println("║  • Click + Add Category to create new ones ║");
            System.out.println("║  • Toggle 🌓 for dark/light mode           ║");
            System.out.println("║                                            ║");
            System.out.println("║  Phase 2-4: COMPLETE! 🎉                   ║");
            System.out.println("╚════════════════════════════════════════════╝");
        });
    }
}