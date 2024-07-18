/**
 * @author Bug (Alyssa Morley)
 * @createdOn 7/17/2024 at 10:55 AM
 * @projectName MongoData
 * @packageName org.example.view;
 */
package org.example.view;

import org.example.model.Employee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInterface {
    private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public void displayEmployee(Employee employee){
        System.out.println(employee);
    }
    public int getIntInput(String prompt){
        while (true) {
            try {
                return Integer.parseInt(getString(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    public int getRangeInt(int min, int max){
        int input;
        while (true) {
            input = getIntInput("Enter number:");
            if (input >= min && input <= max) {
                return input;
            } else {
                System.out.println("Input out of range. Please try again.");
            }
        }
    }

    public int displayMenu() {
        displayMessage("\n\nEnter your choice: \n" +
                "1. Add Employee\n" +
                "2. Delete Employee\n" +
                "3. Find Employee\n" +
                "4. Update Employee\n" +
                "5. Show Serialized Content\n" +
                "6. Exit");

        return getRangeInt(1,6);
    }

    public String getString(String question) {
        System.out.println(question);
        while(true){
            try{
                String input = br.readLine();
                if(!input.isEmpty()){
                    return input;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void displayMessage(String message){
        System.out.println(message);
    }

    public String getName(String type) {
        String name;
        while (true) {
            name = getString(type + " Name:");
            if (name != null && name.length() > 3) {
                return name.toUpperCase();
            } else {
                displayMessage("Name must be at least 4 characters long. Please try again.");
            }
        }
    }

    public int getYearInput() {
        while (true) {
            String yearStr = getString("Year: ").trim();

            if (yearStr.matches("\\d{4}")) {
                int year = Integer.parseInt(yearStr);
                System.out.println("Entered year: " + year);
                return year;
            } else {
                System.out.println("Invalid input. Please enter a 4-digit number.");
            }
        }

    }

    public void displayTime(long time, String processName){
        System.out.println("(" + processName + " Took " + time + " Seconds)");
    }


    //this method may seem repetitive, but one of my checks in mongoDB controller allows for the user to leave the space empty,
    //so I need to accommodate for that
    public String updateName(String question) {
        displayMessage(question);
        try {
            return br.readLine();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    //this method may seem repetitive, but one of my checks in mongoDB controller allows for the user to leave the space empty,
    //so I need to accommodate for that
    public int updateYear(String question)  {
        displayMessage(question);
        int returner;
        try {
            returner = Integer.parseInt(br.readLine());
            return returner;
        } catch (NumberFormatException ignored){
            return 0;
        } catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }
}
