/**
 * @author Bug (Alyssa Morley)
 * @createdOn 7/17/2024 at 10:54 AM
 * @projectName MongoData
 * @packageName org.example.view;
 */
package org.example.model;

public class Employee implements java.io.Serializable{
    private int id;
    private String firstName;
    private String lastName;
    private int year;
    private String documentForm;

    public Employee(int id, String firstName, String lastName, int year) {
        setId(id);
        setFirstName(firstName.toUpperCase());
        setLastName(lastName.toUpperCase());
        setYear(year);
        updateDocumentForm();
    }
    public Employee(String documentForm){
        setDocumentForm(documentForm);
        String[] data = documentForm.split(", ");
        setId(Integer.parseInt(data[0]));
        setFirstName(data[1].toUpperCase());
        setLastName(data[2].toUpperCase());
        setYear(Integer.parseInt(data[3]));
    }

    private void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        if(firstName.length() >= 1)
            this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        if(lastName.length() >= 1)
            this.lastName = lastName;
    }

    public void setYear(int year) {
        if(year > 1900 && year < 3000)
            this.year = year;
    }

    private void setDocumentForm(String documentForm){
        this.documentForm = documentForm;
    }

    public void updateDocumentForm(){
        String space = ", ";
        setDocumentForm(getId() + space
                + getFirstName() + space
                + getLastName() + space
                + getYear());
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getYear() {
        return year;
    }

    public String DocumentForm() {
        return documentForm;
    }

    @Override
    public String toString() {
        return "Employee ID: " + getId()
                + "\nName: " + getFirstName() + " " + getLastName()
                +"\nHire Year: " + getYear();
    }
}
