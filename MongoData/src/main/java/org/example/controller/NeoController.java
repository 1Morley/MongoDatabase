/**
 * @author Bug (Alyssa Morley)
 * @createdOn 8/1/2024 at 1:03 AM
 * @projectName MongoData
 * @packageName org.example.controller;
 */
package org.example.controller;

import org.example.model.Employee;
import org.neo4j.driver.*;

import java.util.HashSet;

public class NeoController {
    //TODO in order for this to work, neo4j needs to be running in the background and have the proper user,password,and localhost number
    // so either make a login at start, have 2 databases that have the same info, or maybe get the remote db to work (which I can't make because neo4j gives me errors)
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
            insertEmlpoyee(employee);
        }
    }
    public void insertEmlpoyee(Employee employee){
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
        System.out.println("Employee inserted");
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
        try (Session session = database.session()) {
            String cypherQuery = "MATCH (e:Employee) WHERE e.id = $id DELETE e";
            try (Transaction tx = session.beginTransaction()) {
                tx.run(cypherQuery, org.neo4j.driver.Values.parameters("id", id));
                tx.commit();
            }
        }
        System.out.println("Employee deleted");
    }
}