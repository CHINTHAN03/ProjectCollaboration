package utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseConnection {
    
    private static final String CONNECTION_STRING = "mongodb://admin:admin3112@ac-nnptslc-shard-00-00.uhgjnzw.mongodb.net:27017,ac-nnptslc-shard-00-01.uhgjnzw.mongodb.net:27017,ac-nnptslc-shard-00-02.uhgjnzw.mongodb.net:27017/?ssl=true&replicaSet=atlas-dmk6i8-shard-0&authSource=admin&appName=Cluster0";
    private static final String DATABASE_NAME = "ProjectCollaborationDB";

    private static MongoClient mongoClient = null;

    
    private DatabaseConnection() {}

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            try {
                mongoClient = MongoClients.create(CONNECTION_STRING);
                System.out.println("Successfully connected to MongoDB Atlas!");
            } catch (Exception e) {
                System.err.println("Database connection failed: " + e.getMessage());
            }
        }
        return mongoClient.getDatabase(DATABASE_NAME);
    }

    
    public static void main(String[] args) {
        MongoDatabase db = DatabaseConnection.getDatabase();
        System.out.println("Database Name: " + db.getName());
    }
}