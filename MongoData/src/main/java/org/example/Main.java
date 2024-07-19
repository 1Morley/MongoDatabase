package org.example;

import org.example.controller.FileController;
import org.example.controller.MongoController;
import org.example.model.Employee;
import org.example.view.UserInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args){
        //FIXME: TESTING
        MongoController m = new MongoController();
        UserInterface UI = new UserInterface();
        FileController fc = new FileController();

        fc.readAllFiles();

        m.importEmployees(fc.getDocumentList());

        m.closeMongoClient();

        System.out.println("Hello world!");
    }
}