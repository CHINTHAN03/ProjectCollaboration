package models;

import java.time.LocalDateTime;

public class Comment {
    private String id;
    private String taskId;
    private String username;
    private String text;
    private String timestamp;

    public Comment() {
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}