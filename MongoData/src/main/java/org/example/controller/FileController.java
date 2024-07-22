/**
 * @author Bug (Alyssa Morley)
 * @createdOn 7/17/2024 at 10:56 AM
 * @projectName MongoData
 * @packageName org.example.controller;
 */
package org.example.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Employee;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

//because of file creation and employee creation being heavily intertwined,
//all data management methods are in this class

public class FileController {
    private final static String DATA_FOLDER_PATH = "C:\\Assignment 1 - data-1\\people\\long\\", SERIAL_FOLDER_PATH = "C:\\Assignment 1 - data-1\\people\\long serialized\\", FILE_EXTENSION = ".txt", SERIAL_EXTENSION = ".ser";
    //private HashMap<Integer, Employee> employeeID = new HashMap<>();
    //private HashMap<String, Employee> employeeLName = new HashMap<>();


    public void updateEmployeeFile(Employee input){
        File file = new File(DATA_FOLDER_PATH + input.getId() + FILE_EXTENSION);
        try{
            if(!checkifFileExists(input.getId())){
                file.createNewFile();
            }
            Files.write(Paths.get(file.getPath()), input.getDocumentForm() .getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }catch (IOException e){
            e.printStackTrace();
        }
        serializeEmployee(input);
    }

    public void deleteEmployeeFile(int employeeId){
        File file = new File(DATA_FOLDER_PATH + employeeId + FILE_EXTENSION);
        file.delete();

        deleteSerializedFile(employeeId);
    }

    public Employee[] readAllFiles() {
        ArrayList<Employee> returnList = new ArrayList<>();
        File folder = new File(DATA_FOLDER_PATH);
        for(File select : folder.listFiles()) {
            try {
                BufferedReader bread = new BufferedReader(new InputStreamReader(new FileInputStream(select)));
                String data = bread.readLine();
                returnList.add(new Employee(data));


                bread.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return returnList.toArray(new Employee[returnList.size()]);
    }


    //region serialized things
    /**
     * deletes all files within serialized folder
     */
    private void emptySerializedFolder() {
        File[] fileSet = new File(SERIAL_FOLDER_PATH).listFiles();
        for (File select : fileSet) {
            select.delete();
        }
    }

    /**
     * deletes single serialized file based on employee id
     * @param employeeID = deletes file named after id
     */
    private void deleteSerializedFile(int employeeID) {
        File file = new File(SERIAL_FOLDER_PATH + employeeID + SERIAL_EXTENSION);
        file.delete();
    }

    public void serializeAllEmployees(Employee[] list) {
        emptySerializedFolder();
        for (Employee select : list) {
            serializeEmployee(select);
        }
    }

    public void serializeEmployee(Employee employee){
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

    private Employee deserializeEmployees() {
        //I don't think I needed to make this, but it's good for making sure the serializeEmployees method worked
        String filePath = SERIAL_FOLDER_PATH + 2 + SERIAL_EXTENSION;
        try {
            FileInputStream file = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(file);

            Employee test = (Employee) in.readObject();
            in.close();
            file.close();
            return test;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String showSerializedContent(int employeeID) {
        File pathFile = new File(SERIAL_FOLDER_PATH + employeeID + SERIAL_EXTENSION);

        if (pathFile.exists()) {
            try {
                BufferedReader bread = new BufferedReader(new InputStreamReader(new FileInputStream(pathFile)));
                String data = bread.readLine();
                bread.close();
                return data;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            return null;
        }
    }
    //endregion


    private boolean checkifFileExists(int employeeID) {
        File file = new File(DATA_FOLDER_PATH + employeeID + FILE_EXTENSION);
        return file.exists();
    }

    public void makeJsonFile(Employee[] list){
        ObjectMapper map = new ObjectMapper();

        try{
            FileWriter write = new FileWriter("JsonFile.json");
            for (Employee select: list) {
                write.write(String.valueOf(map.writerWithDefaultPrettyPrinter().writeValueAsString(select)));
                //write.write(map.writeValueAsString(select));
                //I don't know how the formatting will affect reading the file, so the second version is unformatted
            }
            write.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }




}
