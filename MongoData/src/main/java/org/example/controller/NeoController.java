/**
 * @author Bug (Alyssa Morley)
 * @createdOn 8/1/2024 at 1:03 AM
 * @projectName MongoData
 * @packageName org.example.controller;
 */
package org.example.controller;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class NeoController {
    //TODO in order for this to work, neo4j needs to be running in the background and have the proper user,password,and localhost number
    // so either make a login at start, have 2 databases that have the same info, or maybe get the remote db to work (which I can't make because neo4j gives me errors)
    private final String URI = "neo4j://localhost:7687/neo4j", user = "neo4j", password = "password";
    Driver database;

    public NeoController(){
        var driver = GraphDatabase.driver(URI, AuthTokens.basic(user, password));
        database = driver;
        database.verifyConnectivity();
        closeNeoConnection();
    }

    /**
     * closes connection between database and users (MUST BE CALLED AT THE END OF PROGRAM)
     */
    public void closeNeoConnection(){
        database.close();
    }
}