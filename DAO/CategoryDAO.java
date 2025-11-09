package Todo.DAO;
import Todo.Models.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 📂 CategoryDAO - The Category Data Whisperer
 * 
 * Data Access Object for Category CRUD operations.
 * Acts as the bridge between our Category model and the database.
 * 
 * Design Patterns:
 * - DAO Pattern: Separates data access logic from business logic
 * - PreparedStatements: Prevents SQL injection, improves performance
 * - Resource Management: try-with-resources for automatic cleanup
 * 
 * @author Your Java Architect
 */
public class CategoryDAO {
    private final Connection connection;
    
    /**
     * Constructor - gets connection from singleton
     */
    public CategoryDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    // ═══════════════════════════════════════════════════════════
    // CREATE OPERATIONS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Insert new category into database
     * Returns the category with assigned ID, or null if failed
     */
    public Category create(Category category) {
        String sql = "INSERT INTO categories (name, icon) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getIcon());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Retrieve generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        category.setId(id);
                        System.out.println("✓ Category created: " + category);
                        return category;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to create category: " + category.getName());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // ═══════════════════════════════════════════════════════════
    // READ OPERATIONS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Retrieve category by ID
     */
    public Category findById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCategoryFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to find category with ID: " + id);
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Retrieve category by name
     */
    public Category findByName(String name) {
        String sql = "SELECT * FROM categories WHERE name = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCategoryFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to find category: " + name);
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Retrieve all categories
     */
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(extractCategoryFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to retrieve categories");
            e.printStackTrace();
        }
        
        return categories;
    }
    
    /**
     * Count total categories
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM categories";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to count categories");
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // ═══════════════════════════════════════════════════════════
    // UPDATE OPERATIONS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Update existing category
     * Returns true if successful
     */
    public boolean update(Category category) {
        String sql = "UPDATE categories SET name = ?, icon = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getIcon());
            pstmt.setInt(3, category.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✓ Category updated: " + category);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to update category: " + category);
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ═══════════════════════════════════════════════════════════
    // DELETE OPERATIONS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Delete category by ID
     * Note: This will cascade delete all tasks in this category!
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✓ Category deleted (ID: " + id + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to delete category (ID: " + id + ")");
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Check if category exists
     */
    public boolean exists(int id) {
        String sql = "SELECT 1 FROM categories WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ═══════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Extract Category object from ResultSet
     * Centralizes the mapping logic to avoid code duplication
     */
    private Category extractCategoryFromResultSet(ResultSet rs) throws SQLException {
        return new Category(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("icon")
        );
    }
}
