package com.revature.model;

public class Employee {
    //Information about the employee.
    // These may be public because they are finalized when this class is instantiated.
    private final String email; //This employee's email address
    private final String password; //This employee's password
    private final Roles role; //This employee's role (this is used to determine their permissions)
    private final int id; //This employee's ID in the database (this is mostly unused)

    public Employee(String email, String password, Roles role, int id) {
        //Validation
        if (email == null) {
            throw new IllegalStateException("The email of an employee may not be null");
        }
        if (password == null) {
            throw new IllegalStateException("The password of an employee may not be null");
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

    public String getPassword() {
        return password;
    }

    public Roles getRole() {
        return role;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "email='" + getEmail() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", role=" + getRole() +
                '}';
    }

    //Roles that an employee can have. These define their permissions -- for instance,
    // a finance manager may view all tickets in the database, while a standard
    // employee can only view tickets that they have created.
    public enum Roles {STANDARD, MANAGER}
}