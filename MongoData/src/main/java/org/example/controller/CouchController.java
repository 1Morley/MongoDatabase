package org.example.controller;

import org.bson.json.JsonObject;
import org.bson.json.JsonReader;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.StdHttpClient;
import org.ektorp.http.StdHttpClient.Builder;
import org.ektorp.impl.StdCouchDbInstance;

import java.io.StringReader;


public class CouchController {
    CouchDbInstance dbInstance;
    JsonObject dataForTesting = new JsonObject("{\"name\": \"Jane Doe\", \"position\": \"Manager\", \"age\": 30, \"salary\": 75000.50, \"isFullTime\": true}");
    CouchDbConnector dbConnector;

    public CouchController() {

        String username = "schoolUser";
        String password = "1234";
        try{
            Builder build = new StdHttpClient.Builder()
                    .url("http://localhost:5984")
                    .username(username)
                    .password(password);

            dbInstance = new StdCouchDbInstance(build.build());
            dbConnector = dbInstance.createConnector("my-couchdb", true);
            System.out.println("Couchbase connection successful.");
        }catch (Exception e){
            System.err.println("Couchbase connection failed.");
            e.printStackTrace();
        }
    }

    public void testCRUD(){

    }

    private void addData(){
        try {
            dbConnector.create(dataForTesting);
            System.out.println("Data added successfully.");
        } catch (Exception e) {
            System.err.println("Failed to add data.");
            e.printStackTrace();
        }
    }
}
