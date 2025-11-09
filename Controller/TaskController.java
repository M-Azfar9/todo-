package Todo.Controller;
import Todo.DAO.*;
import Todo.Models.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 🎮 TaskController - The Business Logic Orchestrator
 * 
 * This is the heart of our MVC architecture's Controller layer.
 * It sits between the UI (View) and the database (DAO/Model),
 * handling all business logic and coordinating data flow.
 * 
 * Design Principles:
 * - Single Responsibility: Each method does ONE thing well
 * - No SQL: All database operations delegated to DAOs
 * - No UI: No Swing imports - controller is UI-agnostic
 * - Validation: Business rules enforced here
 * - Observer Pattern: Ready for UI event listeners
 * 
 * @author Your Java Architect
 */
public class TaskController {
    // Data Access Objects - the data whisperers
    private final TaskDAO taskDAO;
    private final CategoryDAO categoryDAO;
    
    /**
     * Constructor - initialize DAOs
     */
    public TaskController() {
        this.taskDAO = new TaskDAO();
        this.categoryDAO = new CategoryDAO();
    }
    
    // ═══════════════════════════════════════════════════════════
    // TASK OPERATIONS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Create a new task
     * Validates input and delegates to DAO
     * 
     * @return Created task with ID, or null if failed
     */
    public Task addTask(String title, String description, int categoryId, LocalDate dueDate) {
        // Validation: Title is required
        if (title == null || title.trim().isEmpty()) {
            System.err.println("⚠ Cannot create task: Title is required");
            return null;
        }
        
        // Validation: Category must exist
        if (!categoryDAO.exists(categoryId)) {
            System.err.println("⚠ Cannot create task: Invalid category ID");
            return null;
        }
        
        // Create task object
        Task task = new Task(title.trim(), description, categoryId, dueDate);
        
        // Persist to database
        return taskDAO.create(task);
    }
    
    /**
     * Overloaded: Create task without description or due date
     */
    public Task addTask(String title, int categoryId) {
        return addTask(title, "", categoryId, null);
    }
    
    /**
     * Update existing task
     * 
     * @return true if update successful
     */
    public boolean updateTask(int taskId, String title, String description, 
                             int categoryId, LocalDate dueDate) {
        // Validation
        if (title == null || title.trim().isEmpty()) {
            System.err.println("⚠ Cannot update task: Title is required");
            return false;
        }
        
        // Fetch existing task
        Task task = taskDAO.findById(taskId);
        if (task == null) {
            System.err.println("⚠ Cannot update: Task not found (ID: " + taskId + ")");
            return false;
        }
        
        // Update properties
        task.setTitle(title.trim());
        task.setDescription(description);
        task.setCategoryId(categoryId);
        task.setDueDate(dueDate);
        
        // Persist changes
        return taskDAO.update(task);
    }
    
    /**
     * Mark task as done (or undone)
     */
    public boolean markTaskDone(int taskId, boolean isDone) {
        Task task = taskDAO.findById(taskId);
        if (task == null) {
            return false;
        }
        
        task.setDone(isDone);
        return taskDAO.update(task);
    }
    
    /**
     * Toggle task completion status
     */
    public boolean toggleTaskDone(int taskId) {
        return taskDAO.toggleDone(taskId);
    }
    
    /**
     * Delete task
     */
    public boolean deleteTask(int taskId) {
        return taskDAO.delete(taskId);
    }
    
    /**
     * Delete all completed tasks
     * Useful for "Clear Completed" functionality
     */
    public int deleteCompletedTasks() {
        return taskDAO.deleteCompleted();
    }
    
    // ═══════════════════════════════════════════════════════════
    // TASK RETRIEVAL & FILTERING
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Get all tasks
     */
    public List<Task> getAllTasks() {
        return taskDAO.findAll();
    }
    
    /**
     * Get tasks by category
     */
    public List<Task> getTasksByCategory(int categoryId) {
        return taskDAO.findByCategory(categoryId);
    }
    
    /**
     * Get tasks by category name
     */
    public List<Task> getTasksByCategory(String categoryName) {
        Category category = categoryDAO.findByName(categoryName);
        if (category != null) {
            return taskDAO.findByCategory(category.getId());
        }
        return List.of(); // Empty list if category not found
    }
    
    /**
     * Get pending (incomplete) tasks
     */
    public List<Task> getPendingTasks() {
        return taskDAO.findByStatus(false);
    }
    
    /**
     * Get completed tasks
     */
    public List<Task> getCompletedTasks() {
        return taskDAO.findByStatus(true);
    }
    
    /**
     * Get overdue tasks
     */
    public List<Task> getOverdueTasks() {
        return taskDAO.findDueTasks();
    }
    
    /**
     * Search tasks by query
     * Searches in title and description
     */
    public List<Task> searchTasks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllTasks();
        }
        return taskDAO.search(query.trim());
    }
    
    /**
     * Filter tasks by multiple criteria
     * 
     * @param categoryId Filter by category (null = all categories)
     * @param showCompleted Include completed tasks?
     * @param searchQuery Search text (null = no search)
     */
    public List<Task> filterTasks(Integer categoryId, boolean showCompleted, String searchQuery) {
        List<Task> tasks;
        
        // Start with category filter
        if (categoryId != null) {
            tasks = taskDAO.findByCategory(categoryId);
        } else {
            tasks = taskDAO.findAll();
        }
        
        // Apply completion filter using streams
        if (!showCompleted) {
            tasks = tasks.stream()
                        .filter(task -> !task.isDone())
                        .collect(Collectors.toList());
        }
        
        // Apply search filter
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.toLowerCase().trim();
            tasks = tasks.stream()
                        .filter(task -> task.matchesSearch(query))
                        .collect(Collectors.toList());
        }
        
        return tasks;
    }
    
    /**
     * Get single task by ID
     */
    public Task getTask(int taskId) {
        return taskDAO.findById(taskId);
    }
    
    // ═══════════════════════════════════════════════════════════
    // CATEGORY OPERATIONS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Create new category
     */
    public Category addCategory(String name, String icon) {
        // Validation
        if (name == null || name.trim().isEmpty()) {
            System.err.println("⚠ Cannot create category: Name is required");
            return null;
        }
        
        // Check for duplicate name
        Category existing = categoryDAO.findByName(name.trim());
        if (existing != null) {
            System.err.println("⚠ Category already exists: " + name);
            return existing;
        }
        
        // Create category
        Category category = new Category(name.trim(), icon);
        return categoryDAO.create(category);
    }
    
    /**
     * Update category
     */
    public boolean updateCategory(int categoryId, String name, String icon) {
        Category category = categoryDAO.findById(categoryId);
        if (category == null) {
            return false;
        }
        
        category.setName(name);
        category.setIcon(icon);
        return categoryDAO.update(category);
    }
    
    /**
     * Delete category
     * WARNING: This cascades to all tasks in the category!
     */
    public boolean deleteCategory(int categoryId) {
        // Don't allow deletion of default categories (IDs 1-3)
        if (categoryId <= 3) {
            System.err.println("⚠ Cannot delete default categories");
            return false;
        }
        
        // Check if category has tasks
        int taskCount = taskDAO.countByCategory(categoryId);
        if (taskCount > 0) {
            System.out.println("⚠ Warning: Deleting category with " + taskCount + " tasks");
        }
        
        return categoryDAO.delete(categoryId);
    }
    
    /**
     * Get all categories
     */
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }
    
    /**
     * Get category by ID
     */
    public Category getCategory(int categoryId) {
        return categoryDAO.findById(categoryId);
    }
    
    /**
     * Get category by name
     */
    public Category getCategoryByName(String name) {
        return categoryDAO.findByName(name);
    }
    
    // ═══════════════════════════════════════════════════════════
    // STATISTICS & INSIGHTS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Get task statistics
     */
    public TaskStats getStatistics() {
        int total = taskDAO.count();
        int completed = taskDAO.findByStatus(true).size();
        int pending = total - completed;
        int overdue = taskDAO.findDueTasks().size();
        
        return new TaskStats(total, completed, pending, overdue);
    }
    
    /**
     * Inner class for task statistics
     */
    public static class TaskStats {
        public final int total;
        public final int completed;
        public final int pending;
        public final int overdue;
        
        public TaskStats(int total, int completed, int pending, int overdue) {
            this.total = total;
            this.completed = completed;
            this.pending = pending;
            this.overdue = overdue;
        }
        
        public double getCompletionPercentage() {
            return total > 0 ? (completed * 100.0 / total) : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format(
                "Tasks: %d total | %d done (%.1f%%) | %d pending | %d overdue",
                total, completed, getCompletionPercentage(), pending, overdue
            );
        }
    }
}
