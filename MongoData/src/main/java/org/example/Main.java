package org.example;

import org.example.controller.MongoController;
import org.example.model.Employee;
import org.example.view.UserInterface;

import java.io.IOException;

public class Main {
    public static void main(String[] args){
        //FIXME: TESTING
        MongoController m = new MongoController();
        UserInterface UI = new UserInterface();

        Employee e = new Employee(1, "Tommy", "Southerland", 1999);

        //m.addToDatabase(e);
        //UI.displayMessage(m.readDatabase(e.getId()).toString());
        //m.deleteFromDatabase(e.getId());

        m.updateFromDatabase(e.getId());

        m.closeMongoClient();

        System.out.println("Hello world!");
    }
}