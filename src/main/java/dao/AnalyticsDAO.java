package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.DashboardMetrics;
import org.bson.Document;
import utils.DatabaseConnection;
import java.util.*;

public class AnalyticsDAO {
    private final MongoCollection<Document> taskCollection;
    private final MongoCollection<Document> userCollection;

    public AnalyticsDAO() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        this.taskCollection = database.getCollection("tasks");
        this.userCollection = database.getCollection("users");
    }

    public DashboardMetrics getProjectMetrics(String projectId) {
        long total = taskCollection.countDocuments(new Document("projectId", projectId));
        long completed = taskCollection.countDocuments(new Document("projectId", projectId).append("status", "Completed"));

        double percentage = total > 0 ? ((double) completed / total) * 100 : 0;

        DashboardMetrics metrics = new DashboardMetrics();
        metrics.setTotalTasks((int) total);
        metrics.setCompletedTasks((int) completed);
        metrics.setCompletionPercentage(Math.round(percentage * 100.0) / 100.0);
        return metrics;
    }

    
    
    public Map<String, Object> getWorkloadRecommendation(String projectId) {
        Map<String, Integer> userScores = new HashMap<>();

        
        for (Document task : taskCollection.find(new Document("projectId", projectId))) {
            String assignee = task.getString("assignedTo");
            String status = task.getString("status");
            int weight = status.equals("In Progress") ? 3 : 1; 
            userScores.put(assignee, userScores.getOrDefault(assignee, 0) + weight);
        }

        
        String recommended = userScores.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No recommendation available");

        Map<String, Object> result = new HashMap<>();
        result.put("recommendedUser", recommended);
        result.put("reason", "Lowest active workload in this project.");
        return result;
    }
}