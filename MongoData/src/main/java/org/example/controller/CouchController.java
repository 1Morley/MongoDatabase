package org.example.controller;

import org.bson.json.JsonObject;
import org.bson.json.JsonReader;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.StdHttpClient;
import org.ektorp.http.StdHttpClient.Builder;
import org.ektorp.impl.StdCouchDbInstance;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;


public class CouchController {
    CouchDbInstance dbInstance;
    Map<String, Object> document = new HashMap<>();

    CouchDbConnector dbConnector;

    public CouchController() {
        String username = "schooluser";
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
        addData();
        findData("365");
    }

    private void addData(){
        document.put("_id", "365");
        document.put("name", "Jane Doe");
        document.put("position", "Manager");
        document.put("age", 30);
        document.put("salary", 75000.50);
        document.put("isFullTime", true);
        try {
            dbConnector.create(document);
            System.out.println("Data added successfully.");
        } catch (Exception e) {
            System.err.println("Failed to add data.");
            e.printStackTrace();
        }
    }

    private void findData(String id) {
        try {
            Map<String, Object> document = dbConnector.get(Map.class, id);

            System.out.println("Document found: ");
            for (Map.Entry<String, Object> entry : document.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } catch (Exception e) {
            System.err.println("Failed to find data.");
            e.printStackTrace();
        }
    }
}
