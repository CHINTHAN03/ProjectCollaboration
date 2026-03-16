package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import models.Project;
import org.bson.Document;
import utils.DatabaseConnection;

import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    private final MongoCollection<Document> collection;

    public ProjectDAO() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        this.collection = database.getCollection("projects");
    }

    public boolean createProject(Project project) {
        try {
            Document doc = new Document("name", project.getName())
                    .append("description", project.getDescription())
                    .append("managerUsername", project.getManagerUsername())
                    .append("teamMembers", project.getTeamMembers())
                    .append("deadline", project.getDeadline())
                    .append("status", project.getStatus() != null ? project.getStatus() : "Planning");

            collection.insertOne(doc);
            return true;
        } catch (Exception e) {
            System.err.println("Error creating project: " + e.getMessage());
            return false;
        }
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Project p = new Project();

                
                p.setId(doc.getObjectId("_id").toString());
                p.setName(doc.getString("name"));
                p.setDescription(doc.getString("description"));
                p.setManagerUsername(doc.getString("managerUsername"));
                p.setTeamMembers(doc.getList("teamMembers", String.class));
                p.setDeadline(doc.getString("deadline"));
                p.setStatus(doc.getString("status"));

                projects.add(p);
            }
        }
        return projects;
    }
}