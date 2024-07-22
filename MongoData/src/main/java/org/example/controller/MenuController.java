/**
 * @author Bug (Alyssa Morley)
 * @createdOn 7/17/2024 at 10:57 AM
 * @projectName MongoData
 * @packageName org.example.controller;
 */
package org.example.controller;

import org.example.view.UserInterface;

import java.time.LocalTime;

public class MenuController {
    private UserInterface ui = new UserInterface();
    private DatabaseController data = new DatabaseController();


    public void run(){
        LocalTime startTime = LocalTime.now();
        data.setup();
        boolean exit = false;
        ui.displayTime(startTime);
        while(!exit){
            startTime = LocalTime.now();
            switch (ui.displayMenu(new String[]
                    {"Add Employee", "Delete Employee", "Find Employee", "Update Employee", "Show Serialized Content", "Import Files to Mongo", "Exit"})){
                case 1:
                    String[] addInfo = ui.getFullEmployeeInfo(false);
                    ui.displayInfo(data.addEmployee(addInfo[0], addInfo[1], Integer.valueOf(addInfo[2])), "Employee");
                    break;
                case 2:
                    int deleteId = ui.getEmployeeId();
                    data.deleteEmployee(deleteId);
                    break;
                case 3:
                    ui.displayInfo(data.findMongoEmployee(ui.getEmployeeId()), "Employee");
                    break;
                case 4:
                    int id = ui.getEmployeeId();
                    String oldData = data.findMongoEmployee(id);
                    ui.displayInfo(oldData, "Old Employee");
                    if(oldData != null){
                        String[] updateInfo = ui.getFullEmployeeInfo(true);
                        data.updateEmployee(id, updateInfo[0], updateInfo[1], Integer.valueOf(updateInfo[2]));
                        ui.displayInfo(data.findMongoEmployee(id), "Updated Employee");
                    }
                    break;
                case 5:
                    String serialData = data.showSerializedFile(ui.getEmployeeId());
                    ui.displayInfo(serialData, "Serialized File");
                    break;
                case 6:
                    switch (ui.displayMenu(new String[]{
                            "Import All (RISK OF REPEAT IDS)",
                            "Replace Collection (ALL PREVIOUS DOCUMENTS WILL BE DELETED)",
                            "Merge Databases (REPLACE PRE-EXISTING IDS)"})){
                        case 1:
                            data.addFilesToMongo(false);
                            break;
                        case 2:
                            data.addFilesToMongo(true);
                            break;
                        case 3:
                            data.mergeFilesWithMongo();
                            break;
                    }
                    break;
                default:
                    data.shutDown();
                    exit = true;
                    break;
            }
            ui.displayTime(startTime);
        }
    }
}
