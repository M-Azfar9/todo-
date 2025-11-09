package Todo.DAO;
import java.sql.*;

/**
 * 🔌 DatabaseConnection - The Data Gateway
 * 
 * Singleton pattern implementation for managing JDBC connections.
 * Uses SQLite for simplicity (no server setup required).
 * 
 * Design Patterns:
 * - Singleton: Ensures only one connection instance exists
 * - Resource Management: Proper connection lifecycle handling
 * - Error Handling: Graceful failure with detailed logging
 * 
 * Why SQLite?
 * - Zero configuration (embedded database)
 * - Single file storage (tasks.db)
 * - Perfect for desktop applications
 * - Can easily swap to MySQL/PostgreSQL later
 * 
 * @author Your Java Architect
 */
public class DatabaseConnection {
    // Singleton instance
    private static DatabaseConnection instance;
    
    // Database configuration
    private static final String DB_NAME = "tasks.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    
    // Active connection
    private Connection connection;
    
    // ═══════════════════════════════════════════════════════════
    // SINGLETON PATTERN IMPLEMENTATION
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Private constructor prevents direct instantiation
     * Connection is established lazily (on first use)
     */
    private DatabaseConnection() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Establish connection
            connection = DriverManager.getConnection(DB_URL);
            
            System.out.println("✓ Database connected: " + DB_NAME);
            
            // Initialize database schema
            initializeSchema();
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ SQLite JDBC driver not found!");
            System.err.println("   Add sqlite-jdbc.jar to your classpath");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            e.printStackTrace();
        }
    }
    
    /**
     * Get singleton instance (thread-safe)
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Get active connection
     */
    public Connection getConnection() {
        try {
            // Check if connection is still valid
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("↻ Database connection reestablished");
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to get database connection!");
            e.printStackTrace();
        }
        return connection;
    }
    
    // ═══════════════════════════════════════════════════════════
    // SCHEMA INITIALIZATION
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Create database tables if they don't exist
     * This runs automatically on first connection
     */
    private void initializeSchema() {
        try (Statement stmt = connection.createStatement()) {
            
            // Create categories table
            String createCategoriesTable = """
                CREATE TABLE IF NOT EXISTS categories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    icon TEXT DEFAULT '📁'
                );
            """;
            stmt.execute(createCategoriesTable);
            
            // Create tasks table
            String createTasksTable = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT,
                    category_id INTEGER NOT NULL,
                    due_date DATE,
                    is_done INTEGER DEFAULT 0,
                    created_at DATE DEFAULT CURRENT_DATE,
                    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
                );
            """;
            stmt.execute(createTasksTable);
            
            // Create indexes for better query performance
            String createCategoryIndex = 
                "CREATE INDEX IF NOT EXISTS idx_task_category ON tasks(category_id);";
            stmt.execute(createCategoryIndex);
            
            String createDoneIndex = 
                "CREATE INDEX IF NOT EXISTS idx_task_done ON tasks(is_done);";
            stmt.execute(createDoneIndex);
            
            System.out.println("✓ Database schema initialized");
            
            // Insert default categories if table is empty
            insertDefaultCategories();
            
        } catch (SQLException e) {
            System.err.println("❌ Schema initialization failed!");
            e.printStackTrace();
        }
    }
    
    /**
     * Insert default categories (Work, Personal, Urgent)
     */
    private void insertDefaultCategories() {
        try {
            // Check if categories already exist
            String checkSQL = "SELECT COUNT(*) FROM categories";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(checkSQL);
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert default categories
                String insertSQL = """
                    INSERT INTO categories (name, icon) VALUES 
                    ('Work', '💼'),
                    ('Personal', '👤'),
                    ('Urgent', '🔥');
                """;
                stmt.execute(insertSQL);
                System.out.println("✓ Default categories created");
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("⚠ Default categories insertion failed");
            e.printStackTrace();
        }
    }
    
    // ═══════════════════════════════════════════════════════════
    // CONNECTION LIFECYCLE MANAGEMENT
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Close database connection
     * Should be called when application exits
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("⚠ Error closing database connection");
            e.printStackTrace();
        }
    }
    
    /**
     * Test database connection
     * Returns true if connection is alive
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Get database metadata information
     */
    public void printDatabaseInfo() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("═══════════════════════════════════════");
            System.out.println("Database: " + metaData.getDatabaseProductName());
            System.out.println("Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver: " + metaData.getDriverName());
            System.out.println("Driver Version: " + metaData.getDriverVersion());
            System.out.println("═══════════════════════════════════════");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}