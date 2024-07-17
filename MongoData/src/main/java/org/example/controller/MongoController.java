/**
 * @author Bug (Alyssa Morley)
 * @createdOn 7/17/2024 at 10:57 AM
 * @projectName MongoData
 * @packageName org.example.controller;
 */
package org.example.controller;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoController {

    public MongoController() {
        MongoClient mongoClient = buildConnection();
    }


    /**
     * buildConnection() tests the connection to the database, and if it works, it will generate a mongoClient for
     * use during the rest of the program
     */
    public MongoClient buildConnection() {
        String connectionString = "mongodb+srv://tommysmith12443:immamakethisareallygoodpassword@main.s3hilgi.mongodb.net/?retryWrites=true&w=majority&appName=Main";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                MongoDatabase database = mongoClient.getDatabase("Employee");
                database.runCommand(new Document("ping", 1));
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
                return mongoClient;
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
