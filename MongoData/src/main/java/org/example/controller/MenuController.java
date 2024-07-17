/**
 * @author Bug (Alyssa Morley)
 * @createdOn 7/17/2024 at 10:57 AM
 * @projectName MongoData
 * @packageName org.example.controller;
 */
package org.example.controller;

import org.example.model.Employee;
import org.example.view.UserInterface;

import java.util.ArrayList;

public class MenuController {
    private enum AppState {
        START, EXIT, ADD, DELETE, FIND, UPDATE, SERIAL_CONTENT
    }
    private final static String DATA_FOLDER_PATH = "C:\\Assignment 1 - data-1\\people\\long\\", FILE_EXTENSION = ".txt";
    private ArrayList<Employee> employeeList = new ArrayList<>();
    private UserInterface ui = new UserInterface();
    private FileController fc = new FileController();

    private void setup(){
        fc.readAllFiles();
        fc.serializeAllEmployees();
        //fc.testNum();
    }

    public void run(){
        setup();
        boolean exit = false;
        while(exit == false){
            switch (switchAppState()){
                case ADD:
                    fc.addCustomEmployee();
                    break;
                case DELETE:
                    fc.deleteEmployee();
                    break;
                case FIND:
                    fc.findEmployee();
                    break;
                case UPDATE:
                    fc.updateEmployee();
                    break;
                case SERIAL_CONTENT:
                    fc.showSerializedContent(ui.getIntInput("Enter Employee ID"));
                    break;
                case EXIT:
                    exit = true;
                    break;
            }
        }
    }

    private AppState switchAppState(){
        switch(ui.displayMenu()){
            case 1:
                return AppState.ADD;
            case 2:
                return AppState.DELETE;
            case 3:
                return AppState.FIND;
            case 4:
                return AppState.UPDATE;
            case 5:
                return AppState.SERIAL_CONTENT;
            case 6:
                return AppState.EXIT;
            default: return AppState.START;
        }
    }


}
