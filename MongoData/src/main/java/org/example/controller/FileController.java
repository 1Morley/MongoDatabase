/**
 * @author Bug (Alyssa Morley)
 * @createdOn 7/17/2024 at 10:56 AM
 * @projectName MongoData
 * @packageName org.example.controller;
 */
package org.example.controller;


import org.example.model.Employee;
import org.example.view.UserInterface;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//because of file creation and employee creation being heavily intertwined,
//all data management methods are in this class

public class FileController {
    private UserInterface ui = new UserInterface();
    private final static String DATA_FOLDER_PATH = "C:\\Assignment 1 - data-1\\people\\long\\", SERIAL_FOLDER_PATH = "C:\\Assignment 1 - data-1\\people\\long serialized\\", FILE_EXTENSION = ".txt", SERIAL_EXTENSION = ".ser";
    private HashMap<Integer, Employee> employeeID = new HashMap<>();
    private HashMap<String, Employee> employeeLName = new HashMap<>();

    public void removeFileViaTitle(int employeeID) {
        File file = new File(DATA_FOLDER_PATH + employeeID + FILE_EXTENSION);
        if (file.delete()) {
            ui.displayMessage("File Deleted Successfully");
        } else {
            ui.displayMessage("File Deletion Failed");
        }
    }

    public void createNewFileViaID(int employeeID) {
        try {
            File file = new File(DATA_FOLDER_PATH + employeeID + FILE_EXTENSION);
            if (file.createNewFile()) {
                ui.displayMessage("File created: " + file.getName());
            } else {
                ui.displayMessage("File already exists.");
            }
        } catch (IOException e) {
            ui.displayMessage("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void addContent(int employeeID, String content) {
        String filePath = DATA_FOLDER_PATH + employeeID + FILE_EXTENSION;
        try {
            Files.write(Paths.get(filePath), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testNum() {
        Integer largestKey = Collections.max(employeeID.keySet());
        System.out.println("Largest Key: " + largestKey);
        System.out.println("Size of employees: " + employeeID);
        System.out.println("Size of employees: " + employeeID.size());
    }

    public void readAllFiles() {
        LocalTime startTime = LocalTime.now();
        ui.displayMessage("Retrieving Database From Files");
        File folder = new File(DATA_FOLDER_PATH);
        for(File select : folder.listFiles()) {
            try {
                BufferedReader bread = new BufferedReader(new InputStreamReader(new FileInputStream(select)));
                String data = bread.readLine();
                generateEmployees(data);

                bread.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        timeProcess(startTime, "Reading All Files");
    }


    /**
     * This method creates an employee object from the data in the files
     * It should never be used for creating new employees in the application
     *
     * @param data the data from the file
     */
    private void generateEmployees(String data) {
        Employee employee = new Employee(data);
        employeeID.put(employee.getId(), employee);
        employeeLName.put(employee.getLastName(), employee);
    }

    public void emptySerializedFolder() {
        File[] fileSet = new File(SERIAL_FOLDER_PATH).listFiles();
        for (File select : fileSet) {
            select.delete();
        }
    }

    private void deleteSerializedFile(int employeeID) {
        File file = new File(SERIAL_FOLDER_PATH + employeeID + SERIAL_EXTENSION);
        file.delete();
    }

    public void serializeAllEmployees() {
        LocalTime startTime = LocalTime.now();
        emptySerializedFolder();
        for (Map.Entry<Integer, Employee> select : employeeID.entrySet()) {
            serializeEmployee(select.getValue());
        }
        timeProcess(startTime, "Serializing Employees");
    }

    private void serializeEmployee(Employee employee){
        try{
            String filePath = SERIAL_FOLDER_PATH + employee.getId() + SERIAL_EXTENSION;

            FileOutputStream file = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(employee);

            out.close();
            file.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void deserializeEmployees() {
        //I don't think I needed to make this, but it's good for making sure the serializeEmployees method worked
        String filePath = SERIAL_FOLDER_PATH + 2 + SERIAL_EXTENSION;
        try {
            FileInputStream file = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(file);

            Employee test = (Employee) in.readObject();
            System.out.println(test);
            in.close();
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSerializedContent(int employeeID) {
        File pathFile = new File(SERIAL_FOLDER_PATH + employeeID + SERIAL_EXTENSION);
        LocalTime startTime = LocalTime.now();

        if (pathFile.exists()) {
            try {
                BufferedReader bread = new BufferedReader(new InputStreamReader(new FileInputStream(pathFile)));
                String data = bread.readLine();
                System.out.println(data);
                bread.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            System.out.println("Employee Not found");
        }
        timeProcess(startTime, "Displaying Serialized Content");
    }

    public void timeProcess(LocalTime startTime, String processName) {
        ui.displayTime(startTime.until(LocalTime.now(), ChronoUnit.SECONDS), processName);
    }

    // Data management
    public void addCustomEmployee() {
        ui.displayMessage("Adding New Employee");
        String firstName = ui.getName("First");
        String lastName = ui.getName("Last");
        int year = ui.getIntInput("Year:");
        int id = Collections.max(employeeID.keySet()) + 1;
        while (checkifFileExists(id)) {
            //finding avaliable id
            id++;
        }

        Employee employee = new Employee(id, firstName, lastName, year);

        ui.displayMessage("Employee Added with ID: " + employee.getId());
        employeeID.put(employee.getId(), employee);
        employeeLName.put(employee.getLastName(), employee);
        createNewFileViaID(employee.getId());
        addContent(employee.getId(), employee.DocumentForm());
        serializeEmployee(employee);
    }
    public void deleteEmployee() {
        ui.displayMessage("Deleting Employee");
        int id = ui.getIntInput("ID:");
        if (employeeID.containsKey(id) && checkifFileExists(id)) {
            removeFileViaTitle(id);
            employeeLName.remove(employeeID.get(id).getLastName());
            employeeID.remove(id);
            deleteSerializedFile(id);
            ui.displayMessage("Employee Deleted");
        } else {
            ui.displayMessage("Employee not found");
        }
    }


    public void findEmployee() {
        ui.displayMessage("Finding Employee" +
                "\n1. By ID" +
                "\n2. By Last Name");
        switch (ui.getRangeInt(1,2)){
            case 1:
                int inputId = ui.getIntInput("ID:");
                LocalTime startTimeID = LocalTime.now();
                if (employeeID.containsKey(inputId)) {
                    System.out.println(employeeID.get(inputId));
                    timeProcess(startTimeID, "Finding Employee By ID");
                } else {
                    ui.displayMessage("Employee not found");
                }
                break;
            case 2:
                String inputName = ui.getName("Last");
                LocalTime startTimeName = LocalTime.now();
                if (employeeLName.containsKey(inputName)) {
                    System.out.println(employeeLName.get(inputName));
                    timeProcess(startTimeName, "Finding Employee By Name");
                } else {
                    ui.displayMessage("Employee not found");
                }
                break;
        }
    }
    public void updateEmployee() {
        ui.displayMessage("Updating Employee");
        int id = ui.getIntInput("ID:");
        if (employeeID.containsKey(id) && checkifFileExists(id)) {
            Employee employee = employeeID.get(id);
            String oldLastName = employee.getLastName();
            employee.setFirstName(ui.getName("First"));
            employee.setLastName(ui.getName("Last"));
            employee.setYear(ui.getIntInput("Year:"));
            LocalTime startTime = LocalTime.now();
            employee.updateDocumentForm();
            addContent(employee.getId(), employee.DocumentForm());

            employeeLName.remove(oldLastName);
            employeeLName.put(employee.getLastName(), employee);
            serializeEmployee(employee);
            timeProcess(startTime, "Updating Employee");
        } else {
            ui.displayMessage("Employee not found");
        }
    }
    public boolean checkifFileExists(int employeeID) {
        File file = new File(DATA_FOLDER_PATH + employeeID + FILE_EXTENSION);
        return file.exists();
    }



}
