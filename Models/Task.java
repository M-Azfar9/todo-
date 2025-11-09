package Todo.Models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * ✓ Task Model - The Core Entity
 * 
 * Represents a single TODO task with all its properties.
 * This is the heart of our application's data structure.
 * 
 * Design Principles:
 * - Rich domain model: Contains both data AND behavior
 * - Validation: Ensures data integrity at the model level
 * - Immutable ID: Once set, never changes
 * - Null-safe: Handles null values gracefully
 * 
 * @author Your Java Architect
 */
public class Task {
    // Core properties
    private int id;                    // Database primary key
    private String title;              // Task title (required)
    private String description;        // Optional detailed description
    private int categoryId;            // Foreign key to Category
    private LocalDate dueDate;         // Optional deadline
    private boolean isDone;            // Completion status
    private LocalDate createdAt;       // Timestamp of creation
    
    // Formatting helper
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("MMM dd, yyyy");
    
    // ═══════════════════════════════════════════════════════════
    // CONSTRUCTORS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Default constructor
     */
    public Task() {
        this.id = -1;
        this.title = "";
        this.description = "";
        this.categoryId = -1;
        this.dueDate = null;
        this.isDone = false;
        this.createdAt = LocalDate.now();
    }
    
    /**
     * Constructor for new tasks (before DB insertion)
     * Minimal required fields: title and category
     */
    public Task(String title, int categoryId) {
        this();
        this.title = title;
        this.categoryId = categoryId;
    }
    
    /**
     * Extended constructor with description and due date
     */
    public Task(String title, String description, int categoryId, LocalDate dueDate) {
        this(title, categoryId);
        this.description = description;
        this.dueDate = dueDate;
    }
    
    /**
     * Full constructor (used when loading from database)
     * This is the complete representation of a persisted task
     */
    public Task(int id, String title, String description, int categoryId, 
                LocalDate dueDate, boolean isDone, LocalDate createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.dueDate = dueDate;
        this.isDone = isDone;
        this.createdAt = createdAt != null ? createdAt : LocalDate.now();
    }
    
    // ═══════════════════════════════════════════════════════════
    // GETTERS & SETTERS
    // ═══════════════════════════════════════════════════════════
    
    public int getId() {
        return id;
    }
    
    // ID should only be set once (by DAO after insertion)
    public void setId(int id) {
        if (this.id == -1) {
            this.id = id;
        }
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        // Validation: Title cannot be empty
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        this.title = title.trim();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public boolean isDone() {
        return isDone;
    }
    
    public void setDone(boolean done) {
        isDone = done;
    }
    
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
    
    // ═══════════════════════════════════════════════════════════
    // BUSINESS LOGIC HELPERS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Check if task is persisted in database
     */
    public boolean isPersisted() {
        return id > 0;
    }
    
    /**
     * Toggle completion status
     */
    public void toggleDone() {
        this.isDone = !this.isDone;
    }
    
    /**
     * Check if task is overdue
     */
    public boolean isOverdue() {
        if (dueDate == null || isDone) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }
    
    /**
     * Check if task is due soon (within 3 days)
     */
    public boolean isDueSoon() {
        if (dueDate == null || isDone) {
            return false;
        }
        LocalDate threeDaysFromNow = LocalDate.now().plusDays(3);
        return !dueDate.isAfter(threeDaysFromNow) && !isOverdue();
    }
    
    /**
     * Get formatted due date string for UI display
     */
    public String getFormattedDueDate() {
        if (dueDate == null) {
            return "No due date";
        }
        return dueDate.format(DATE_FORMATTER);
    }
    
    /**
     * Get formatted creation date
     */
    public String getFormattedCreatedAt() {
        return createdAt.format(DATE_FORMATTER);
    }
    
    /**
     * Get display-friendly title with status indicators
     */
    public String getDisplayTitle() {
        StringBuilder sb = new StringBuilder();
        
        if (isDone) {
            sb.append("✓ ");
        } else if (isOverdue()) {
            sb.append("🔴 ");
        } else if (isDueSoon()) {
            sb.append("🟡 ");
        }
        
        sb.append(title);
        return sb.toString();
    }
    
    /**
     * Check if task matches search query
     * Searches in title and description (case-insensitive)
     */
    public boolean matchesSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;  // Empty query matches everything
        }
        
        String lowerQuery = query.toLowerCase().trim();
        return title.toLowerCase().contains(lowerQuery) ||
               (description != null && description.toLowerCase().contains(lowerQuery));
    }
    
    // ═══════════════════════════════════════════════════════════
    // OBJECT CONTRACT METHODS
    // ═══════════════════════════════════════════════════════════
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Task task = (Task) o;
        
        // If both have valid IDs, compare by ID
        if (this.id > 0 && task.id > 0) {
            return this.id == task.id;
        }
        
        // Otherwise compare by content (for unpersisted tasks)
        return Objects.equals(title, task.title) &&
               Objects.equals(categoryId, task.categoryId) &&
               Objects.equals(createdAt, task.createdAt);
    }
    
    @Override
    public int hashCode() {
        return id > 0 ? Objects.hash(id) : 
               Objects.hash(title, categoryId, createdAt);
    }
    
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", categoryId=" + categoryId +
                ", isDone=" + isDone +
                ", dueDate=" + (dueDate != null ? dueDate : "none") +
                '}';
    }
}
