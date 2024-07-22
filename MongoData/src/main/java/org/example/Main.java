package org.example;

import org.example.controller.MenuController;

public class Main {
    public static void main(String[] args){
        //FIXME: TESTING

        //MongoController m = new MongoController();
        //UserInterface UI = new UserInterface();
        //FileController fc = new FileController();

        //fc.readAllFiles();

        //m.importEmployees(fc.getDocumentList());

        //m.closeMongoClient();

        new MenuController().run();
        System.out.println("END PROGRAM");
    }
}