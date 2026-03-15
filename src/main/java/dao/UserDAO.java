package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.User;
import org.bson.Document;
import utils.DatabaseConnection;

public class UserDAO {
    private final MongoCollection<Document> collection;

    public UserDAO() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        this.collection = database.getCollection("users");
    }

    public boolean registerUser(User user) {
        Document existingUser = collection.find(new Document("username", user.getUsername())).first();
        if (existingUser != null) {
            return false;
        }

        Document doc = new Document("username", user.getUsername())
                .append("password", user.getPassword())
                .append("role", user.getRole());

        collection.insertOne(doc);
        return true;
    }

    public User loginUser(String username, String password) {
        Document userDoc = collection.find(new Document("username", username)).first();

        if (userDoc != null) {
            String storedPassword = userDoc.getString("password");

            if (password.equals(storedPassword)) {
                User loggedInUser = new User();
                loggedInUser.setUsername(userDoc.getString("username"));
                loggedInUser.setRole(userDoc.getString("role"));
                return loggedInUser;
            }
        }
        return null;
    }
}