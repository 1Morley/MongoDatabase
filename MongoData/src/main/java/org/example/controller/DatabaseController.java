/**
 * @author Bug (Alyssa Morley)
 * @createdOn 7/19/2024 at 10:40 AM
 * @projectName MongoData
 * @packageName org.example.controller;
 */
package org.example.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.example.model.Employee;

import java.util.*;

public class DatabaseController {
    private static final boolean MONGO = true, FILES = false; //decides whether to search + update the mongo database or the file directory
    private FileController fc = new FileController();
    private MongoController mango = new MongoController();
    private HashMap<Integer, Employee> employeeID = new HashMap<>();
    private HashMap<String, Employee> employeeLName = new HashMap<>();


    public void setup(){
        Employee[] list = fc.readAllFiles();
        for(Employee select: list){
            employeeID.put(select.getId(), select);
            employeeLName.put(select.getLastName(), select);
        }
        fc.serializeAllEmployees(getIdHashmapToArray());
    }

    public String addEmployee(String first, String last, int year){
        int id;
        if(MONGO){
            id = mango.getNextID();
            mango.addToDatabase(new Employee(id,first,last,year));
            return mango.readDatabase(id).toString();
        }
        if (FILES){
            id = Collections.max(employeeID.keySet()) + 1;

            Employee employee = new Employee(id, first, last, year);

            employeeID.put(employee.getId(), employee);
            employeeLName.put(employee.getLastName(), employee);
            fc.updateEmployeeFile(employee);
            return employeeID.get(id).toString();
        }
        return null;
    }

    public void deleteEmployee(int employeeId){
        if(MONGO){
            mango.deleteFromDatabase(employeeId);
        }
        if (FILES){
            if (employeeID.containsKey(employeeId)) {
                employeeLName.remove(employeeID.get(employeeId).getLastName());
                employeeID.remove(employeeId);
                deleteEmployee(employeeId);
            }

        }
    }

    public void updateEmployee(int employeeId, String first, String last, int year){
        if(MONGO){
            mango.updateFromDatabase(employeeId, first, last, year);
        }
        if (FILES){
            if (employeeID.containsKey(employeeId)) {
                Employee employee = employeeID.get(employeeId);
                String oldLastName = employee.getLastName();
                if(first != null){
                    employee.setFirstName(first);
                }
                if(last != null){
                    employee.setLastName(last);
                }
                if(year != 0){
                    employee.setYear(year);
                }

                employee.updateDocumentForm();

                employeeLName.remove(oldLastName);
                employeeLName.put(employee.getLastName(), employee);

                fc.updateEmployeeFile(employee);

            }
        }
    }

    public void addFilesToMongo(boolean wipe){
        if(wipe){
            mango.wipeCollection();
        }
        mango.importEmployees(getDocumentList());
    }

    public void mergeFilesWithMongo(){
        mango.mergeDatabase(getIdHashmapToArray());
    }

    public String findMongoEmployee(int employeeId){
        Employee found = mango.readDatabase(employeeId);
        if (found != null){
            return found.toString();
        }else {
            return null;
        }
    }

    public String findFileEmployee(int employeeId){
        Employee found = employeeID.get(employeeId);
        if (found != null){
            return found.toString();
        }else {
            return null;
        }
    }

    public String findFileEmployee(String lastName){
        Employee found = employeeLName.get(lastName);
        if (found != null){
            return found.toString();
        }else {
            return null;
        }
    }

    public String showSerializedFile(int employeeId){
        return fc.showSerializedContent(employeeId);
    }

    public void shutDown(){
        mango.closeMongoClient();
    }

    private Employee[] getIdHashmapToArray(){
       ArrayList<Employee> thing = new ArrayList<>();
       thing.addAll(employeeID.values());
       return thing.toArray(new Employee[thing.size()]);
    }

    public List<Document> getDocumentList(){
        ObjectMapper map = new ObjectMapper();
        List<Document> doc = new ArrayList<>();

        try{
            for (Map.Entry<Integer, Employee> select : employeeID.entrySet()) {
                doc.add(Document.parse(String.valueOf(map.writerWithDefaultPrettyPrinter().writeValueAsString(select.getValue()))));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return doc;
    }






}
