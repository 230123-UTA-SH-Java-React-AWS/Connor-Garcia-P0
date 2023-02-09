package com.revature.model;

public class Employee {
    //Information about the employee.
    private String email; //This employee's email address
    private int password; //This employee's password
    private Roles role; //This employee's role (this is used to determine their permissions)
    private int id; //This employee's ID in the database (this is mostly unused)

    //Required by Jackson
    public Employee(){}

    public Employee(String email, int password, Roles role, int id) {
        //Validation
        if (email == null) {
            throw new IllegalStateException("The email of an employee may not be null");
        }
        if (role == null) {
            throw new IllegalStateException("The role of an employee may not be null (try Employee.Roles.STANDARD)");
        }
        if(id < 1){
            throw new IllegalStateException("The ID of an employee must be at least 1");
        }

        //Initialization
        this.email = email;
        this.password = password;
        this.role = role;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public int getPassword() {
        return password;
    }

    public Roles getRole() {
        return role;
    }

    public int getId() {
        return id;
    }

    //Roles that an employee can have. These define their permissions -- for instance,
    // a finance manager may view all tickets in the database, while a standard
    // employee can only view tickets that they have created.
    public enum Roles {STANDARD, MANAGER}
}