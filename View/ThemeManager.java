package Todo.View;
import java.awt.Color;
import java.awt.GradientPaint;

/**
 * 🎨 ThemeManager - The Soul of Skeuominimalism
 * 
 * Manages our dual-personality color system. Light mode feels like a 
 * sun-kissed workspace; dark mode is your creative midnight sanctuary.
 * 
 * Design Philosophy:
 * - Soft gradients > flat colors (adds depth without noise)
 * - High contrast for readability, low saturation for elegance
 * - Colors that breathe and adapt
 */
public class ThemeManager {
    private static boolean isDarkMode = false;
    
    // 🌞 LIGHT MODE PALETTE - Inspired by morning coffee and clean paper
    public static class Light {
        // Backgrounds with subtle warmth
        public static final Color BG_PRIMARY = new Color(248, 249, 250);
        public static final Color BG_SECONDARY = new Color(255, 255, 255);
        public static final Color BG_HOVER = new Color(243, 244, 246);
        
        // Sidebar gets a gentle gradient (top to bottom)
        public static final Color SIDEBAR_TOP = new Color(242, 244, 247);
        public static final Color SIDEBAR_BOTTOM = new Color(249, 250, 251);
        
        // Text hierarchy
        public static final Color TEXT_PRIMARY = new Color(31, 41, 55);
        public static final Color TEXT_SECONDARY = new Color(107, 114, 128);
        public static final Color TEXT_MUTED = new Color(156, 163, 175);
        
        // Accents - soft blue that doesn't scream
        public static final Color ACCENT = new Color(59, 130, 246);
        public static final Color ACCENT_HOVER = new Color(37, 99, 235);
        
        // Shadows for depth (semi-transparent black)
        public static final Color SHADOW = new Color(0, 0, 0, 15);
        public static final Color SHADOW_STRONG = new Color(0, 0, 0, 25);
        
        // Borders - barely there, but defining
        public static final Color BORDER = new Color(229, 231, 235);
        public static final Color BORDER_FOCUS = new Color(191, 219, 254);
        
        // Category colors
        public static final Color CATEGORY_WORK = new Color(239, 246, 255);
        public static final Color CATEGORY_PERSONAL = new Color(243, 232, 255);
        public static final Color CATEGORY_URGENT = new Color(254, 242, 242);
        
        // Button states
        public static final Color BUTTON_ADD = new Color(34, 197, 94);
        public static final Color CATEGORY_BUTTON_ADD = new Color(255, 191, 0); // simple amber
        public static final Color CATEGORY_BUTTON_ADD_HOVER = new Color(255, 204, 77); // soft glowing amber


        public static final Color BUTTON_ADD_HOVER = new Color(22, 163, 74);
        public static final Color BUTTON_DELETE = new Color(239, 68, 68);
        public static final Color BUTTON_DELETE_HOVER = new Color(220, 38, 38);
    }
    
    // 🌙 DARK MODE PALETTE - Your 2AM coding companion
    public static class Dark {
        // Deep, rich backgrounds
        public static final Color BG_PRIMARY = new Color(17, 24, 39);
        public static final Color BG_SECONDARY = new Color(31, 41, 55);
        public static final Color BG_HOVER = new Color(55, 65, 81);
        
        // Sidebar gradient (darker at top for depth)
        public static final Color SIDEBAR_TOP = new Color(23, 29, 40);
        public static final Color SIDEBAR_BOTTOM = new Color(31, 41, 55);
        
        // Text - high contrast but not harsh
        public static final Color TEXT_PRIMARY = new Color(243, 244, 246);
        public static final Color TEXT_SECONDARY = new Color(209, 213, 219);
        public static final Color TEXT_MUTED = new Color(156, 163, 175);
        
        // Accents - brighter in dark mode for visibility
        public static final Color ACCENT = new Color(96, 165, 250);
        public static final Color ACCENT_HOVER = new Color(59, 130, 246);
        
        // Shadows are highlights in dark mode
        public static final Color SHADOW = new Color(0, 0, 0, 40);
        public static final Color SHADOW_STRONG = new Color(0, 0, 0, 60);
        
        // Borders glow softly
        public static final Color BORDER = new Color(55, 65, 81);
        public static final Color BORDER_FOCUS = new Color(59, 130, 246);
        
        // Category colors - muted but recognizable
        public static final Color CATEGORY_WORK = new Color(30, 58, 138);
        public static final Color CATEGORY_PERSONAL = new Color(88, 28, 135);
        public static final Color CATEGORY_URGENT = new Color(127, 29, 29);
        
        // Button states
        public static final Color BUTTON_ADD = new Color(34, 197, 94);
        public static final Color CATEGORY_BUTTON_ADD = new Color(255, 191, 0); // simple amber
        public static final Color CATEGORY_BUTTON_ADD_HOVER = new Color(255, 204, 77); // soft glowing amber
        public static final Color BUTTON_ADD_HOVER = new Color(22, 163, 74);
        public static final Color BUTTON_DELETE = new Color(239, 68, 68);
        public static final Color BUTTON_DELETE_HOVER = new Color(220, 38, 38);
    }
    
    // 🎯 Current Theme Getters - Always return the active palette
    public static Color getBgPrimary() {
        return isDarkMode ? Dark.BG_PRIMARY : Light.BG_PRIMARY;
    }
    
    public static Color getBgSecondary() {
        return isDarkMode ? Dark.BG_SECONDARY : Light.BG_SECONDARY;
    }
    
    public static Color getBgHover() {
        return isDarkMode ? Dark.BG_HOVER : Light.BG_HOVER;
    }
    
    public static Color getTextPrimary() {
        return isDarkMode ? Dark.TEXT_PRIMARY : Light.TEXT_PRIMARY;
    }
    
    public static Color getTextSecondary() {
        return isDarkMode ? Dark.TEXT_SECONDARY : Light.TEXT_SECONDARY;
    }
    
    public static Color getTextMuted() {
        return isDarkMode ? Dark.TEXT_MUTED : Light.TEXT_MUTED;
    }
    
    public static Color getAccent() {
        return isDarkMode ? Dark.ACCENT : Light.ACCENT;
    }
    
    public static Color getAccentHover() {
        return isDarkMode ? Dark.ACCENT_HOVER : Light.ACCENT_HOVER;
    }
    
    public static Color getShadow() {
        return isDarkMode ? Dark.SHADOW : Light.SHADOW;
    }
    
    public static Color getShadowStrong() {
        return isDarkMode ? Dark.SHADOW_STRONG : Light.SHADOW_STRONG;
    }
    
    public static Color getBorder() {
        return isDarkMode ? Dark.BORDER : Light.BORDER;
    }
    
    public static Color getBorderFocus() {
        return isDarkMode ? Dark.BORDER_FOCUS : Light.BORDER_FOCUS;
    }
    
    public static Color getSidebarTop() {
        return isDarkMode ? Dark.SIDEBAR_TOP : Light.SIDEBAR_TOP;
    }
    
    public static Color getSidebarBottom() {
        return isDarkMode ? Dark.SIDEBAR_BOTTOM : Light.SIDEBAR_BOTTOM;
    }
    
    public static Color getCategoryWork() {
        return isDarkMode ? Dark.CATEGORY_WORK : Light.CATEGORY_WORK;
    }
    
    public static Color getCategoryPersonal() {
        return isDarkMode ? Dark.CATEGORY_PERSONAL : Light.CATEGORY_PERSONAL;
    }
    
    public static Color getCategoryUrgent() {
        return isDarkMode ? Dark.CATEGORY_URGENT : Light.CATEGORY_URGENT;
    }
    
    public static Color getButtonAdd() {
        return isDarkMode ? Dark.BUTTON_ADD : Light.BUTTON_ADD;
    }

    public static Color getCategoryButtonAdd() {
        return isDarkMode ? Dark.CATEGORY_BUTTON_ADD : Light.CATEGORY_BUTTON_ADD;
    }

    public static Color getCategoryButtonAddHover() {
        return isDarkMode ? Dark.CATEGORY_BUTTON_ADD_HOVER : Light.CATEGORY_BUTTON_ADD_HOVER;
    }

    public static Color getButtonAddHover() {
        return isDarkMode ? Dark.BUTTON_ADD_HOVER : Light.BUTTON_ADD_HOVER;
    }
    
    public static Color getButtonDelete() {
        return isDarkMode ? Dark.BUTTON_DELETE : Light.BUTTON_DELETE;
    }
    
    public static Color getButtonDeleteHover() {
        return isDarkMode ? Dark.BUTTON_DELETE_HOVER : Light.BUTTON_DELETE_HOVER;
    }
    
    // 🌓 Toggle the theme
    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
    }
    
    public static boolean isDarkMode() {
        return isDarkMode;
    }
    
    public static void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
    }
}