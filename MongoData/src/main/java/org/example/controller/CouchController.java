package org.example.controller;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.StdHttpClient;
import org.ektorp.http.StdHttpClient.Builder;
import org.ektorp.impl.StdCouchDbInstance;

import java.util.HashMap;
import java.util.List;
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
            Map<String, Object> document = dbConnector.get(Map.class, id); //for demonstration purposes, we're creating another doc

            System.out.println("Document found: ");
            for (Map.Entry<String, Object> entry : document.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } catch (Exception e) {
            System.err.println("Failed to find data.");
            e.printStackTrace();
        }
    }

    private void updateData(){
        Map<String, Object> document = dbConnector.get(Map.class, 365 + "");
        document.put("name", "John Doe");
        document.put("age", "32");

        dbConnector.update(document);
    }

    private void deleteData(String id){
        try{
            Map<String, Object> document = dbConnector.get(Map.class, 365 + "");
            dbConnector.delete(document);
        }catch (Exception e){
            System.out.println("Document not found, Delete Failed");
        }
    }
    
    private void wipeAll(){ //not required it just helps with debugging
        List<String> idList = dbConnector.getAllDocIds();
        System.out.println(idList);
        for(String select : idList){
            deleteData(select);
        }
        System.out.println(dbConnector.getAllDocIds());
    }

    private void showAll(){ //not required it just helps with debugging
        List<String> idList = dbConnector.getAllDocIds();
        System.out.println(idList);
        for(String select : idList){
            findData(select);
        }
    }
}
