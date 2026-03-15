package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.User;
import org.bson.Document;
import utils.DatabaseConnection;

import java.util.ArrayList;
import java.util.List;

public class AdminDAO {
    private final MongoDatabase database;

    public AdminDAO() {
        this.database = DatabaseConnection.getDatabase();
    }

    // Wipes all project, task, and comment data, but leaves users intact
    public boolean wipeWorkspace() {
        try {
            database.getCollection("projects").drop();
            database.getCollection("tasks").drop();
            database.getCollection("comments").drop();
            return true;
        } catch (Exception e) {
            System.err.println("Wipe error: " + e.getMessage());
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("users");
        for (Document doc : collection.find()) {
            User u = new User();
            u.setUsername(doc.getString("username"));
            u.setRole(doc.getString("role"));
            users.add(u);
        }
        return users;
    }

    public boolean removeUser(String username) {
        try {
            database.getCollection("users").deleteOne(new Document("username", username));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}