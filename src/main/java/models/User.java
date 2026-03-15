package models;

import org.bson.types.ObjectId;

public class User {
    private ObjectId id; // MongoDB's default ID type
    private String username;
    private String password; // We will hash this later
    private String role; // "Admin", "Project Manager", or "Developer"

    // Empty constructor required for Jackson JSON parsing
    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}