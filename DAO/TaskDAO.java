package Todo.DAO;
import Todo.Models.Task;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ✓ TaskDAO - The Task Data Whisperer
 * 
 * Data Access Object for Task CRUD operations.
 * Handles all database interactions for tasks with proper error handling
 * and resource management.
 * 
 * Design Principles:
 * - PreparedStatements: Security (SQL injection prevention) + Performance
 * - Transaction Support: Ensures data consistency
 * - Null Safety: Handles optional fields gracefully
 * - Resource Management: Automatic cleanup with try-with-resources
 * 
 * @author Your Java Architect
 */
public class TaskDAO {
    private final Connection connection;
    
    /**
     * Constructor - gets connection from singleton
     */
    public TaskDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    // ═══════════════════════════════════════════════════════════
    // CREATE OPERATIONS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Insert new task into database
     * Returns the task with assigned ID, or null if failed
     */
    public Task create(Task task) {
        String sql = """
            INSERT INTO tasks (title, description, category_id, due_date, is_done, created_at) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setInt(3, task.getCategoryId());
            
            // Handle nullable due date
            if (task.getDueDate() != null) {
                pstmt.setDate(4, Date.valueOf(task.getDueDate()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            
            pstmt.setInt(5, task.isDone() ? 1 : 0);
            pstmt.setDate(6, Date.valueOf(task.getCreatedAt()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Retrieve generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        task.setId(id);
                        System.out.println("✓ Task created: " + task.getTitle() + " (ID: " + id + ")");
                        return task;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to create task: " + task.getTitle());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // ═══════════════════════════════════════════════════════════
    // READ OPERATIONS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Retrieve task by ID
     */
    public Task findById(int id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTaskFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to find task with ID: " + id);
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Retrieve all tasks
     */
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY created_at DESC, id DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tasks.add(extractTaskFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to retrieve tasks");
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    /**
     * Retrieve tasks by category
     */
    public List<Task> findByCategory(int categoryId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE category_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(extractTaskFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to retrieve tasks for category: " + categoryId);
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    /**
     * Retrieve tasks by completion status
     */
    public List<Task> findByStatus(boolean isDone) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE is_done = ? ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, isDone ? 1 : 0);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(extractTaskFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to retrieve tasks by status");
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    /**
     * Search tasks by title or description
     */
    public List<Task> search(String query) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
            SELECT * FROM tasks 
            WHERE title LIKE ? OR description LIKE ? 
            ORDER BY created_at DESC
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(extractTaskFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to search tasks");
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    /**
     * Get tasks due today or overdue
     */
    public List<Task> findDueTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = """
            SELECT * FROM tasks 
            WHERE is_done = 0 
            AND due_date IS NOT NULL 
            AND due_date <= date('now')
            ORDER BY due_date ASC
        """;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tasks.add(extractTaskFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to retrieve due tasks");
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    /**
     * Count total tasks
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM tasks";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to count tasks");
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Count tasks by category
     */
    public int countByCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM tasks WHERE category_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // ═══════════════════════════════════════════════════════════
    // UPDATE OPERATIONS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Update existing task
     */
    public boolean update(Task task) {
        String sql = """
            UPDATE tasks 
            SET title = ?, description = ?, category_id = ?, 
                due_date = ?, is_done = ? 
            WHERE id = ?
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setInt(3, task.getCategoryId());
            
            if (task.getDueDate() != null) {
                pstmt.setDate(4, Date.valueOf(task.getDueDate()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            
            pstmt.setInt(5, task.isDone() ? 1 : 0);
            pstmt.setInt(6, task.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✓ Task updated: " + task.getTitle());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to update task: " + task.getTitle());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Toggle task completion status
     */
    public boolean toggleDone(int taskId) {
        String sql = "UPDATE tasks SET is_done = NOT is_done WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("❌ Failed to toggle task status");
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ═══════════════════════════════════════════════════════════
    // DELETE OPERATIONS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Delete task by ID
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✓ Task deleted (ID: " + id + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to delete task (ID: " + id + ")");
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete all completed tasks
     */
    public int deleteCompleted() {
        String sql = "DELETE FROM tasks WHERE is_done = 1";
        
        try (Statement stmt = connection.createStatement()) {
            int deletedCount = stmt.executeUpdate(sql);
            System.out.println("✓ Deleted " + deletedCount + " completed tasks");
            return deletedCount;
        } catch (SQLException e) {
            System.err.println("❌ Failed to delete completed tasks");
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // ═══════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Extract Task object from ResultSet
     * Centralizes the mapping logic to avoid code duplication
     */
    private Task extractTaskFromResultSet(ResultSet rs) throws SQLException {
        // Handle nullable due date
        Date dueDate = rs.getDate("due_date");
        LocalDate dueDateLocal = (dueDate != null) ? dueDate.toLocalDate() : null;
        
        // Handle nullable created_at
        Date createdAt = rs.getDate("created_at");
        LocalDate createdAtLocal = (createdAt != null) ? 
            createdAt.toLocalDate() : LocalDate.now();
        
        return new Task(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getInt("category_id"),
            dueDateLocal,
            rs.getInt("is_done") == 1,
            createdAtLocal
        );
    }
}