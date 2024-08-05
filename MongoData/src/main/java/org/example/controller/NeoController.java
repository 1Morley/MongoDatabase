/**
 * @author Bug (Alyssa Morley)
 * @createdOn 8/1/2024 at 1:03 AM
 * @projectName MongoData
 * @packageName org.example.controller;
 */
package org.example.controller;

import org.example.model.Employee;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class NeoController {
    private final String URI = "neo4j://localhost:7687/neo4j", user = "neo4j", password = "password";
    Driver database;


    public NeoController(){
        database = GraphDatabase.driver(URI, AuthTokens.basic(user, password));
        database.verifyConnectivity();
    }

    /**
     * closes connection between database and users (MUST BE CALLED AT THE END OF PROGRAM)
     */
    public void closeNeoConnection(){
        database.close();
    }
    public void uploadData(HashSet<Employee> employees){
        for(Employee employee: employees){
            insertEmployee(employee);
        }
    }
    public void insertEmployee(Employee employee){
        try (Session session = database.session()) {
            String cypherQuery = "CREATE (e:Employee {id: $id, firstName: $firstName, lastName: $lastName, year: $year})";
            try (Transaction tx = session.beginTransaction()) {
                tx.run(cypherQuery,
                        org.neo4j.driver.Values.parameters(
                                "id", employee.getId(), //I did a little research and it said its internal id cannot be changed
                                                                    // If you find out differently let me know
                                "firstName", employee.getFirstName(),
                                "lastName", employee.getLastName(),
                                "year", employee.getYear()));
                tx.commit();
            }
        }
    }
    public int getNextID(){
        try (Session session = database.session()) {
            String cypherQuery = "MATCH (e:Employee) RETURN e.id ORDER BY e.id DESC LIMIT 1";
            try (Transaction tx = session.beginTransaction()) {
                Result result = tx.run(cypherQuery);
                if(result.hasNext()){
                    return result.single().get(0).asInt() + 1;
                }
            }
        }
        return 0;
    }
    public void deleteEmployee(int id){
        wipeRelationships(id);
        try (Session session = database.session()) {
            String cypherQuery = "MATCH (e:Employee) WHERE e.id = $id DELETE e";
            try (Transaction tx = session.beginTransaction()) {
                tx.run(cypherQuery, org.neo4j.driver.Values.parameters("id", id));
                tx.commit();
            }
        }
    }

    public Employee findEmployee(int id){
        Employee foundEmployee = null;
        try (Session session = database.session()) {
            String cypherQuery = "MATCH (e:Employee) WHERE e.id = $id RETURN e";
            Transaction action = session.beginTransaction();
                Result query = action.run(cypherQuery, org.neo4j.driver.Values.parameters("id", id));
                List<Record> dataList = query.list();
                if (!dataList.isEmpty()){
                    foundEmployee = new Employee(dataList.get(0).values().get(0).get("id").asInt()
                            ,dataList.get(0).values().get(0).get("firstName").asString()
                            ,dataList.get(0).values().get(0).get("lastName").asString()
                            ,dataList.get(0).values().get(0).get("year").asInt());
                }
                action.close();
        }
        return foundEmployee;
    }

    public void updateEmployee(int id, String firstName, String lastName, int year){
        try (Session session = database.session()) {
            String cypherQuery = "MATCH (e:Employee) WHERE e.id = $id SET e.firstName = $firstName, e.lastName = $lastName, e.year = $year";
            Transaction action = session.beginTransaction();
            Result query = action.run(cypherQuery, org.neo4j.driver.Values.parameters(
                    "id", id,
                    "firstName", firstName,
                    "lastName", lastName,
                    "year", year));
            action.commit();
            action.close();
        }
    }

    public int findNextId(){
        int returnId = -1;
        try (Session session = database.session()) {
            String cypherQuery = "match (e:Employee) return max(e.id)";
            Transaction action = session.beginTransaction();
            Result query = action.run(cypherQuery);
            List<Record> dataList = query.list();
            if(!dataList.get(0).values().get(0).isNull()){
                returnId = dataList.get(0).values().get(0).asInt();
            }

            action.close();
        }
        return returnId + 1;
    }

    public void updateRelationship(int id, int oldConnectedId, int newConnectedId){
        deleteRelationship(id,oldConnectedId);
        createRelationship(id,newConnectedId);
    }

    public int[] findRelationships(int id){
        int[] returnList = null;
        try (Session session = database.session()) {
            String cypherQuery = "MATCH (e:Employee {id: $id}) -[rel:REPORTS_TO] -> (m) RETURN m";
            Transaction action = session.beginTransaction();
            Result query = action.run(cypherQuery, org.neo4j.driver.Values.parameters("id", id));
            List<Record> dataList = query.list();
            if(!dataList.isEmpty()){
                returnList = new int[dataList.size()];
                for (int i = 0; i < dataList.size(); i++) {
                    returnList[i] = dataList.get(i).values().get(0).get("id").asInt();
                }
            }
            action.close();
        }
        return returnList;
    }

    public void deleteRelationship(int id, int connectedId){
        try (Session session = database.session()) {
            String cypherQuery = "MATCH (e:Employee{id:$id})-[rel:REPORTS_TO]->(m:Employee{id: $connectedId}) delete rel";
            Transaction action = session.beginTransaction();
            action.run(cypherQuery, org.neo4j.driver.Values.parameters("id", id, "connectedId", connectedId));
            action.commit();
            action.close();
        }
    }

    public void wipeRelationships(int id){
        String cypherQuery = "MATCH (e:Employee{id:$id})-[rel:REPORTS_TO]->(m:Employee) delete rel";
        try (Session session = database.session()) {
            for (int i = 0; i < 2; i++) {
                Transaction action = session.beginTransaction();
                action.run(cypherQuery, org.neo4j.driver.Values.parameters("id", id));
                action.commit();
                cypherQuery = "MATCH (e:Employee)-[rel:REPORTS_TO]->(m:Employee{id: $id}) delete rel";
                action.close();
            }

        }
    }

    private boolean checkRelationshipAlreadyExists(int id, int connectedId){
        int[] list = findRelationships(id);
        if(list != null){
            for (int select : list) {
                if (connectedId == select) {
                    return true;
                }
            }
        }
        return false;
    }

    public void createRelationship(int id,int connectedId){
        if(!checkRelationshipAlreadyExists(id,connectedId)){
            try (Session session = database.session()) {
                String cypherQuery = "MATCH (e:Employee{id:$id}), (m:Employee{id: $connectedId}) CREATE (e) - [:REPORTS_TO] -> (m)";
                Transaction action = session.beginTransaction();
                action.run(cypherQuery, org.neo4j.driver.Values.parameters("id", id, "connectedId", connectedId));
                action.commit();
                action.close();
            }
        }
    }
}