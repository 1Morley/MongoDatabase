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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.example.model.Employee;
import org.example.view.UserInterface;

import javax.print.Doc;

public class MongoController {
    public MongoClient mongoClient;
    UserInterface UI = new UserInterface();

    public MongoController() {
        mongoClient = buildConnection();
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
        MongoClient mongoClient = MongoClients.create(settings);

        // Send a ping to confirm a successful connection
        MongoDatabase database = mongoClient.getDatabase("Employee");
        database.runCommand(new Document("ping", 1));
        System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
        return mongoClient;
    }

    /**
     * This method adds ONE specific user to the database
     *
     * @param employee the employee added to the database
     */
    public void addToDatabase(Employee employee){
        //access database
        MongoDatabase database = mongoClient.getDatabase("Employee");
        MongoCollection<Document> collection = database.getCollection("Employees");

        Document doc = new Document("id", employee.getId())
                .append("first_name", employee.getFirstName())
                .append("last_name", employee.getLastName())
                .append("hire_year", employee.getYear());

        collection.insertOne(doc);
        UI.displayMessage("Added to Database");
    }

    public void deleteFromDatabase(int employeeId){
        MongoDatabase database = mongoClient.getDatabase("Employee");
        MongoCollection<Document> collection = database.getCollection("Employees");

        Document filter = new Document("id", employeeId);

        DeleteResult result = collection.deleteOne(filter);

        if(result.getDeletedCount() > 0){
            UI.displayMessage("Document with id " + employeeId + " was deleted.");
        } else {
            UI.displayMessage("Nothing was found under id: " + employeeId);
        }
    }
}
