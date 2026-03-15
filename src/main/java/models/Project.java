package models;

import java.util.List;

public class Project {
    private String id;
    private String name;
    private String description;
    private String managerUsername;
    private List<String> teamMembers;
    private String deadline; // Format: YYYY-MM-DD
    private String status; // e.g., "Planning", "Active", "Completed"

    public Project() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getManagerUsername() { return managerUsername; }
    public void setManagerUsername(String managerUsername) { this.managerUsername = managerUsername; }

    public List<String> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<String> teamMembers) { this.teamMembers = teamMembers; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}