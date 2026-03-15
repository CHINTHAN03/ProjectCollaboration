package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import models.Comment;
import org.bson.Document;
import utils.DatabaseConnection;

import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    private final MongoCollection<Document> collection;

    public CommentDAO() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        this.collection = database.getCollection("comments");
    }

    public boolean addComment(Comment comment) {
        try {
            Document doc = new Document("taskId", comment.getTaskId())
                    .append("username", comment.getUsername())
                    .append("text", comment.getText())
                    .append("timestamp", comment.getTimestamp());
            collection.insertOne(doc);
            return true;
        } catch (Exception e) {
            System.err.println("Error adding comment: " + e.getMessage());
            return false;
        }
    }

    public List<Comment> getCommentsByTaskId(String taskId) {
        List<Comment> comments = new ArrayList<>();
        // Fetch comments and sort them by timestamp (oldest first)
        try (MongoCursor<Document> cursor = collection.find(new Document("taskId", taskId))
                .sort(new Document("timestamp", 1)).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Comment c = new Comment();
                c.setId(doc.getObjectId("_id").toString());
                c.setTaskId(doc.getString("taskId"));
                c.setUsername(doc.getString("username"));
                c.setText(doc.getString("text"));
                c.setTimestamp(doc.getString("timestamp"));
                comments.add(c);
            }
        }
        return comments;
    }
}