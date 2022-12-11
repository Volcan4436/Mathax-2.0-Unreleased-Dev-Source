package xyz.mathax.client.utils.entity;

public enum SortPriority {
    Lowest_Distance("Lowest distance"),
    Highest_Distance("Highest distance"),
    Lowest_Health("Lowest health"),
    Highest_Health("Highest health"),
    Closest_Angle("Closest angle");

    private final String title;

    SortPriority(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}