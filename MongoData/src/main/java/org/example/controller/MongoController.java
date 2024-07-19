/**
 * @author Bug (Alyssa Morley)
 * @createdOn 7/17/2024 at 10:57 AM
 * @projectName MongoData
 * @packageName org.example.controller;
 */
package org.example.controller;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.json.JsonObject;
import org.example.model.Employee;
import org.example.view.UserInterface;

import javax.print.Doc;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * This class deals with all the logic for connecting to the MongoDB and storing information
 * inside of MongoDB Atlas
 * <p>
 *  BuildConnection() starts the connection for the database and generates a client
 *  to use throughout the program
 */
public class MongoController {
    private final MongoClient mongoClient;
    UserInterface UI = new UserInterface();

    public MongoController() {
        //builds the client every time the class is called
        mongoClient = buildConnection();
    }


    /**
     * buildConnection() tests the connection to the database, and generates a mongoClient for
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
        UI.displayMessage("Pinged your deployment. You successfully connected to MongoDB!");
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

    /**
     * Takes an employeeID and uses that ID to find
     * that certain employee in the database
     * @param employeeId the ID it searches for
     * @return the Employee it found
     */
    public Employee readDatabase(int employeeId){
        MongoDatabase database = mongoClient.getDatabase("Employee");
        MongoCollection<Document> collection = database.getCollection("Employees");

        Document filter = new Document("id", employeeId);
        FindIterable<Document> foundDocuments = collection.find(filter);

        if(foundDocuments.first() != null){
            Document found = foundDocuments.first();

            if(found == null){
                UI.displayMessage("An error occurred");
                return null;
            }

            return new Employee(
                    found.getInteger("id"),
                    found.getString("first_name"),
                    found.getString("last_name"),
                    found.getInteger("hire_year"));
        }else {
            UI.displayMessage("No Document found with id: " + employeeId);
            return null;
        }
    }

    /**
     * deleteFromDatabase will delete a certain object from the database, deletes by ID
     * @param employeeId the ID that was saved to the database that will be deleted
     */
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


    /**
     * Updates an employee based on their ID, if no employee is there, updates nothing
     * @param EmployeeID The ID of the employee you want updated
     */
    public void updateFromDatabase(int EmployeeID) {
        Employee employee = readDatabase(EmployeeID);

        UI.displayEmployee(employee);
        UI.displayMessage("Enter information to update for the employee (leave empty to keep the same)");
        String firstname = UI.updateName("First name: ");
        String lastname = UI.updateName("Last name: ");
        int year = UI.updateYear("Year: ");

        if(firstname == null || firstname.isEmpty()){
            firstname = employee.getFirstName();
        }
        if(lastname == null || lastname.isEmpty()){
            lastname = employee.getLastName();
        }
        if(year == 0){
            year = employee.getYear();
        }

        Employee newEmployee = new Employee(employee.getId(), firstname, lastname, year);

        deleteFromDatabase(employee.getId());
        addToDatabase(newEmployee);
    }

    public void closeMongoClient(){
        mongoClient.close();
    }

    //TODO: upload doc file to the atlas db
    public void importEmployees(List<Document> docList){
        MongoDatabase database = mongoClient.getDatabase("Employee");
        MongoCollection<Document> collection = database.getCollection("Employees");

        collection.insertMany(docList);
    }
}
