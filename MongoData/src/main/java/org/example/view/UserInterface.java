/**
 * @author Bug (Alyssa Morley)
 * @createdOn 7/17/2024 at 10:55 AM
 * @projectName MongoData
 * @packageName org.example.view;
 */
package org.example.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class UserInterface {
    private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public void displayInfo(String info, String type){
        if(info != null){
            System.out.println("Found " + type + ":\n" + info);
        }else{
            System.out.println("Error " + type + " : not found");
        }

        System.out.println();
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
            input = getIntInput("Enter your choice:");
            if (input >= min && input <= max) {
                return input;
            } else {
                System.out.println("Input out of range. Please try again.");
            }
        }
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

    public String getString(String question, boolean allowNull) {
        System.out.println(question);
        while(true){
            try{
                String input = br.readLine();
                if(!input.isEmpty() || allowNull){
                    return input;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void displayMessage(String message){
        System.out.println(message);
    }

    public String getName(String type, boolean allowNull) {
        String name;
        while (true) {
            name = getString(type + " Name:", allowNull);
            if (name != null && name.length() > 1) {
                return name.toUpperCase();
            }else if (allowNull && (name.isEmpty() || name == null)){
                return null;
            } else {
                displayMessage("Name must be at least 1 characters long. Please try again.");
            }
        }
    }

    public int getYearInput(boolean allowNull) {
        while (true) {
            String yearStr = getString("Year: ", allowNull).trim();

            if (yearStr.matches("\\d{4}")) {
                int year = Integer.parseInt(yearStr);
                System.out.println("Entered year: " + year);
                return year;
            } else if (allowNull && yearStr.isEmpty()) {
                return 0;
            } else {
                System.out.println("Invalid input. Please enter a 4-digit number.");
            }
        }

    }

    public void displayTime(LocalTime startTime){
        System.out.println("(Process Took " + startTime.until(LocalTime.now(), ChronoUnit.SECONDS) + " Seconds)");
    }


    public int displayMenu(String[] optionList){
        StringBuilder bob = new StringBuilder();
        for (int i = 0; i < optionList.length; i++) {
            bob.append((i+1) + ". " + optionList[i]+"\n");
        }

        displayMessage(bob.toString());
        return getRangeInt(1, optionList.length + 1);
    }

    public int getEmployeeId(){
        return getIntInput("Enter EmployeeID: ");
    }

    public String[] getFullEmployeeInfo(boolean allowNulls){
        String[] info = new String[3];

        info[0] = getName("First", allowNulls);
        info[1] = getName("Last", allowNulls);
        info[2] = String.valueOf(getYearInput(allowNulls));

        return info;
    }
}
