package org.example;

import org.example.controller.MongoController;
import org.example.model.Employee;

public class Main {
    public static void main(String[] args) {
        MongoController m = new MongoController();

        Employee e = new Employee(1, "Tommy", "Southerland", 1999);

        m.deleteFromDatabase(e.getId());
        m.addToDatabase(e);
        m.deleteFromDatabase(e.getId());

        m.mongoClient.close();

        System.out.println("Hello world!");
    }
}