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
    public static final boolean MONGO = false, FILES = true, NEO = true; //decides whether to search + update the mongo database or the file directory
    private FileController fc = new FileController();
    private MongoController mango;
    private NeoController neo;
    private HashMap<Integer, Employee> employeeID = new HashMap<>();
    private HashMap<String, Employee> employeeLName = new HashMap<>();


    public void setup(){
        if(NEO){
            neo = new NeoController();
            loadRelationships();
        }
        if(MONGO){
            mango = new MongoController();
        }
        if(FILES){
            Employee[] list = fc.readAllFiles();
            for(Employee select: list){
                employeeID.put(select.getId(), select);
                employeeLName.put(select.getLastName(), select);
            }
            fc.serializeAllEmployees(getIdHashmapToArray());
        }
    }

    public int addEmployee(String first, String last, int year){
        int id;
        if(MONGO){
            id = mango.getNextID();
            mango.addToDatabase(new Employee(id,first,last,year));
        }else if(NEO){
            id = neo.findNextId();
            neo.insertEmployee(new Employee(id, first, last, year));
        }else if (FILES){
            id = Collections.max(employeeID.keySet()) + 1;

            Employee employee = new Employee(id, first, last, year);

            employeeID.put(employee.getId(), employee);
            employeeLName.put(employee.getLastName(), employee);
            fc.updateEmployeeFile(employee);
        }

        return id;
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
        if(NEO){
            neo.deleteEmployee(employeeId);
        }
    }

    public void updateEmployee(int employeeId, String first, String last, int year){
        Employee oldVersion = findEmployeeById(employeeId);

        if(first == null || first.isEmpty()){
            first = oldVersion.getFirstName();
        }
        if(last == null || last.isEmpty()){
            last = oldVersion.getLastName();
        }
        if(year == 0){
            year = oldVersion.getYear();
        }

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
        if(NEO){
            neo.updateEmployee(employeeId, first, last, year);
        }
    }

    public void addFileListToOtherDatabase(boolean wipe){
        if(MONGO && FILES){
            if(wipe){
                mango.wipeCollection();
            }
            mango.importEmployees(getDocumentList());
        }
        if(NEO && FILES){
            neo.uploadData(new HashSet<>(employeeID.values()));
        }
    }

    public void mergeFilesWithMongo(){
        if(MONGO && FILES){
            mango.mergeDatabase(getIdHashmapToArray());
        }
    }


    public Employee findEmployeeById(int employeeId){
        Employee found = null;
        if(MONGO){
            found = mango.readDatabase(employeeId);
            if (found != null){
                return found;
            }
        }
        if (NEO && found == null){
            found = neo.findEmployee(employeeId);
            if (found != null){
                return found;
            }
        }
        if(FILES && found == null){
            found = employeeID.get(employeeId);
            if (found != null){
                return found;
            }
        }
        return null;
    }


    public String findEmployeeByLastName(String lastName){
        if (FILES){
            Employee found = employeeLName.get(lastName);
            if (found != null){
                return found.toString();
            }
        }
        return null;
    }




    public String showSerializedFile(int employeeId){
        if(FILES){
            return fc.showSerializedContent(employeeId);
        }
        return null;
    }

    public void shutDown(){
        if(MONGO){
            mango.closeMongoClient();
        }
        if(NEO){
            neo.closeNeoConnection();
        }
    }

    private Employee[] getIdHashmapToArray(){
        ArrayList<Employee> thing = new ArrayList<>();
        thing.addAll(employeeID.values());
        return thing.toArray(new Employee[thing.size()]);
    }

    private List<Document> getDocumentList(){
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


    public void addNeoRelationship(int id, int connectedId){
        if(NEO){
            neo.createRelationship(id, connectedId);
        }

    }

    public void deleteNeoRelationship(int id, int connectedId){
        if(NEO) {
            neo.deleteRelationship(id, connectedId);
        }
    }

    public String[] listAllNeoRelationships(int id){
        if(NEO) {
            int[] idList = neo.findRelationships(id);
            String[] nameList = new String[idList.length];
            if (idList == null) {
                return null;
            }
            for (int i = 0; i < idList.length; i++) {
                Employee connectedEmployee = findEmployeeById(idList[i]);
                nameList[i] = connectedEmployee.getFirstName() + " " + connectedEmployee.getLastName();
            }
            return nameList;
        }
        return null;
    }

    public void updateNeoRelationship(int id, int oldConnectedId, int newConnectedId ){
        if(NEO) {
            neo.updateRelationship(id, oldConnectedId, newConnectedId);
        }
    }

    private void loadRelationships(){
            HashMap<Integer, Integer> relations = fc.getEmployeeReltations();
            for (Map.Entry<Integer, Integer> select : relations.entrySet()) {
                addNeoRelationship(select.getValue(), select.getKey());
            }
    }
}
