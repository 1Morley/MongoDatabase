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
    private UserInterface ui = new UserInterface();
    private FileController fc = new FileController();

    private void setup(){
        fc.readAllFiles();
        //fc.serializeAllEmployees();
    }

    public void run(){
        setup();
        boolean exit = false;
        while(!exit){
            switch (ui.displayMenu()){
                case 1:
                    fc.addCustomEmployee();
                    break;
                case 2:
                    fc.deleteEmployee();
                    break;
                case 3:
                    fc.findEmployee();
                    break;
                case 4:
                    fc.updateEmployee();
                    break;
                case 5:
                    fc.showSerializedContent(ui.getIntInput("Enter Employee ID"));
                    break;
                default:
                    exit = true;
                    break;
            }
        }
    }
}
