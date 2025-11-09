package Todo.Models;

import java.util.Objects;

/**
 * 📂 Category Model - The Organizational DNA
 * 
 * Represents a task category (Work, Personal, Urgent, or custom ones).
 * This is a Plain Old Java Object (POJO) following JavaBean conventions.
 * 
 * Design Principles:
 * - Encapsulation: Private fields with public getters/setters
 * - Immutability options: ID shouldn't change after creation
 * - Proper equals() and hashCode() for collection operations
 * - toString() for easy debugging
 * 
 * @author Your Java Architect
 */
public class Category {
    // Core properties
    private int id;           // Database primary key (auto-generated)
    private String name;      // Category name (e.g., "Work", "Personal")
    private String icon;      // Emoji or icon identifier (e.g., "💼", "👤")
    
    // ═══════════════════════════════════════════════════════════
    // CONSTRUCTORS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Default constructor (required for frameworks/reflection)
     */
    public Category() {
        this.id = -1;  // -1 indicates not yet persisted
        this.name = "";
        this.icon = "📁";
    }
    
    /**
     * Constructor for new categories (before DB insertion)
     * ID will be assigned by database auto-increment
     */
    public Category(String name, String icon) {
        this.id = -1;  // Will be set after database insertion
        this.name = name;
        this.icon = icon;
    }
    
    /**
     * Full constructor (used when loading from database)
     */
    public Category(int id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }
    
    // ═══════════════════════════════════════════════════════════
    // GETTERS & SETTERS (Encapsulation in action)
    // ═══════════════════════════════════════════════════════════
    
    public int getId() {
        return id;
    }
    
    // ID should only be set once (by DAO after insertion)
    public void setId(int id) {
        if (this.id == -1) {  // Only set if not already set
            this.id = id;
        }
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name != null ? name.trim() : "";
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon != null ? icon : "📁";
    }
    
    // ═══════════════════════════════════════════════════════════
    // BUSINESS LOGIC HELPERS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Check if this category is persisted in database
     */
    public boolean isPersisted() {
        return id > 0;
    }
    
    /**
     * Get display text for UI (icon + name)
     */
    public String getDisplayText() {
        return icon + " " + name;
    }
    
    // ═══════════════════════════════════════════════════════════
    // OBJECT CONTRACT METHODS (equals, hashCode, toString)
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Two categories are equal if they have the same ID
     * (or same name if both are unpersisted)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Category category = (Category) o;
        
        // If both have valid IDs, compare by ID
        if (this.id > 0 && category.id > 0) {
            return this.id == category.id;
        }
        
        // Otherwise compare by name (for unpersisted categories)
        return Objects.equals(name, category.name);
    }
    
    @Override
    public int hashCode() {
        return id > 0 ? Objects.hash(id) : Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}