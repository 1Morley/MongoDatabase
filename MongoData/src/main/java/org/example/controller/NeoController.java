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
    public void deleteEmployee(int id){
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
        int returnId = -10;
        try (Session session = database.session()) {
            String cypherQuery = "match (e:Employee) return max(e.id)";
            Transaction action = session.beginTransaction();
            Result query = action.run(cypherQuery);
            List<Record> dataList = query.list();
            returnId = dataList.get(0).values().get(0).asInt();
            action.close();
        }
        return returnId + 1;
    }
}