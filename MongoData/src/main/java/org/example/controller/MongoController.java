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
import static com.mongodb.client.model.Sorts.descending;

import org.bson.Document;
import org.example.model.Employee;
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
    MongoDatabase database;
    MongoCollection<Document> collection;

    public MongoController() {
        //builds the client every time the class is called
        mongoClient = buildConnection();

        database = mongoClient.getDatabase("Employee");
        collection = database.getCollection("test");
    }


    /**
     * buildConnection() tests the connection to the database, and generates a mongoClient for
     * use during the rest of the program
     */
    private MongoClient buildConnection() {
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
        //UI.displayMessage("Pinged your deployment. You successfully connected to MongoDB!");
        return mongoClient;
    }

    /**
     * This method adds ONE specific user to the database
     *
     * @param employee the employee added to the database
     */
    public void addToDatabase(Employee employee){
        //access database
        Document doc = new Document("id", employee.getId())
                .append("first_name", employee.getFirstName())
                .append("last_name", employee.getLastName())
                .append("hire_year", employee.getYear());

        collection.insertOne(doc);
        //UI.displayMessage("Added to Database");
    }

    /**
     * Takes an employeeID and uses that ID to find
     * that certain employee in the database
     * @param employeeId the ID it searches for
     * @return the Employee it found
     */
    public Employee readDatabase(int employeeId){
        Document filter = new Document("id", employeeId);
        FindIterable<Document> foundDocuments = collection.find(filter);

        if(foundDocuments.first() != null){
            Document found = foundDocuments.first();

            if(found == null){
                //UI.displayMessage("An error occurred");
                return null;
            }

            return new Employee(
                    found.getInteger("id"),
                    found.getString("first_name"),
                    found.getString("last_name"),
                    found.getInteger("hire_year"));
        }else {
            //UI.displayMessage("No Document found with id: " + employeeId);
            return null;
        }
    }

    /**
     * deleteFromDatabase will delete a certain object from the database, deletes by ID
     * @param employeeId the ID that was saved to the database that will be deleted
     */
    public void deleteFromDatabase(int employeeId){
        Document filter = new Document("id", employeeId);

        DeleteResult result = collection.deleteOne(filter);

    }


    /**
     * Updates an employee based on their ID, if no employee is there, updates nothing
     * @param EmployeeID The ID of the employee you want updated
     */
    public void updateFromDatabase(int EmployeeID, String firstname, String lastname, int year) {
        Employee employee = readDatabase(EmployeeID);


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

    public void updateFromDatabase(Employee replacement){
        deleteFromDatabase(replacement.getId());
        addToDatabase(replacement);
    }

    /**
     * Closes the connection between the database and the user (MUST BE RAN AT THE END
     * OF THE PROGRAM EVERY SINGLE TIME)
     */
    public void closeMongoClient(){
        mongoClient.close();
    }


    public void importEmployees(List<Document> docList){
        collection.insertMany(docList);
    }

    public void wipeCollection(){
        collection.drop();
    }

    public void mergeDatabase(Employee[] list){
        for (Employee select : list) {
            if(readDatabase(select.getId()) == null){
                addToDatabase(select);
            }else{
                updateFromDatabase(select);
            }
        }
    }

    public int getNextID(){
        Document lastDoc = collection.find().sort(descending("id")).limit(1).first();
        return (lastDoc.getInteger("id") + 1);
    }
}
