package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import models.Task;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.DatabaseConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDAO {
    private final MongoCollection<Document> collection;
    private final MongoCollection<Document> projectCollection;

    public TaskDAO() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        this.collection = database.getCollection("tasks");
        this.projectCollection = database.getCollection("projects");
    }

    public boolean createTask(Task task) {
        try {
            Document doc = new Document("projectId", task.getProjectId())
                    .append("title", task.getTitle())
                    .append("description", task.getDescription())
                    .append("assignedTo", task.getAssignedTo())
                    .append("status", task.getStatus() != null ? task.getStatus() : "To Do")
                    .append("priority", task.getPriority() != null ? task.getPriority() : "Medium");
            collection.insertOne(doc);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Task> getTasksByProjectId(String projectId) {
        List<Task> tasks = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find(new Document("projectId", projectId)).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Task t = new Task();
                t.setId(doc.getObjectId("_id").toString());
                t.setProjectId(doc.getString("projectId"));
                t.setTitle(doc.getString("title"));
                t.setDescription(doc.getString("description"));
                t.setAssignedTo(doc.getString("assignedTo"));
                t.setStatus(doc.getString("status"));
                t.setPriority(doc.getString("priority"));
                tasks.add(t);
            }
        }
        return tasks;
    }

    public boolean updateTaskStatus(String taskId, String newStatus) {
        try {
            collection.updateOne(new Document("_id", new ObjectId(taskId)), Updates.set("status", newStatus));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    
    public String recommendAssignee(String projectId) {
        
        Document project = projectCollection.find(new Document("_id", new ObjectId(projectId))).first();
        if (project == null) return "Unassigned";

        List<String> teamMembers = project.getList("teamMembers", String.class);
        if (teamMembers == null || teamMembers.isEmpty()) return "Unassigned";

        
        Map<String, Integer> workloadMap = new HashMap<>();
        for (String member : teamMembers) {
            workloadMap.put(member, 0); 
        }

        Document query = new Document("projectId", projectId)
                .append("status", new Document("$ne", "Completed")); 

        try (MongoCursor<Document> cursor = collection.find(query).iterator()) {
            while (cursor.hasNext()) {
                String assignee = cursor.next().getString("assignedTo");
                if (workloadMap.containsKey(assignee)) {
                    
                    
                    workloadMap.put(assignee, workloadMap.get(assignee) + 1);
                }
            }
        }

        
        String recommendedUser = teamMembers.get(0);
        int lowestWorkload = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : workloadMap.entrySet()) {
            if (entry.getValue() < lowestWorkload) {
                lowestWorkload = entry.getValue();
                recommendedUser = entry.getKey();
            }
        }

        return recommendedUser;
    }
}