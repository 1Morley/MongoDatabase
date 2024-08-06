/**
 * @author Bug (Alyssa Morley)
 * @createdOn 7/17/2024 at 10:57 AM
 * @projectName MongoData
 * @packageName org.example.controller;
 */
package org.example.controller;

import org.example.model.Employee;
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
                    {"Add Employee"
                            , "Delete Employee"
                            , "Find Employee"
                            , "Update Employee"
                            , "Show Serialized Content (Only works if File database is active)"
                            , "Import Files to Mongo (Only works if File and another database is active)"
                            , "Edit Neo Relationship (Only works if Neo4j database is active)"
                            , "Exit"})){
                case 1:
                    String[] addInfo = ui.getFullEmployeeInfo(false);
                    int newEmployeeId = data.addEmployee(addInfo[0], addInfo[1], Integer.valueOf(addInfo[2]));

                    ui.displayInfo(data.getEmployeeString(newEmployeeId),"Employee");
                    if(data.NEO && ui.userConfirmation("Do you want to add a relation?")){
                        do{
                            data.addNeoRelationship(newEmployeeId, ui.getEmployeeId("Connected"));
                        }while(ui.userConfirmation("Add another?"));
                    }
                    break;
                case 2:
                    int deleteId = ui.getEmployeeId();
                    data.deleteEmployee(deleteId);
                    break;
                case 3:
                    int searchId = ui.getEmployeeId();
                    ui.displayInfo(data.getEmployeeString(searchId), "Employee");
                    if(data.NEO){
                        String[] relList = data.listAllNeoRelationships(searchId);
                        if(relList != null){
                            ui.displayEmployeeRelationship(relList);
                        }
                    }
                    break;
                case 4:
                    int id = ui.getEmployeeId();
                    String oldData = data.getEmployeeString(id);
                    ui.displayInfo(oldData, "Old Employee");
                    if(oldData != null){
                        String[] updateInfo = ui.getFullEmployeeInfo(true);
                        data.updateEmployee(id, updateInfo[0], updateInfo[1], Integer.valueOf(updateInfo[2]));
                        ui.displayInfo(data.getEmployeeString(id), "Updated Employee");
                    }
                    break;
                case 5:
                    String serialData = data.showSerializedFile(ui.getEmployeeId());
                    ui.displayInfo(serialData, "Serialized File");
                    break;
                case 6:
                    if(data.MONGO){
                        switch (ui.displayMenu(new String[]{
                                "Import All (RISK OF REPEAT IDS)",
                                "Replace Collection (ALL PREVIOUS DOCUMENTS WILL BE DELETED)",
                                "Merge Databases (REPLACE PRE-EXISTING IDS)"})){
                            case 1:
                                data.addFileListToOtherDatabase(false);
                                break;
                            case 2:
                                data.addFileListToOtherDatabase(true);
                                break;
                            case 3:
                                data.mergeFilesWithMongo();
                                break;
                        }
                    }else{
                        data.addFileListToOtherDatabase(false);
                    }
                    break;
                case 7:
                    switch (ui.displayMenu(new String[]{
                            "Add Relationship",
                            "Delete Relationship",
                            "Update Relationship"})){
                        case 1:
                            data.addNeoRelationship(ui.getEmployeeId(), ui.getEmployeeId("Connected"));
                            break;
                        case 2:
                            data.deleteNeoRelationship(ui.getEmployeeId(), ui.getEmployeeId("Connected"));
                            break;
                        case 3:
                            data.updateNeoRelationship(
                                    ui.getEmployeeId(), ui.getEmployeeId("Old Connected"),ui.getEmployeeId("New Connected"));
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
