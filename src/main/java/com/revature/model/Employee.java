package com.revature.model;

public class Employee {
    //Roles that an employee can have. These define their permissions -- for instance,
    // a finance manager may view all tickets in the database, while a standard
    // employee can only view tickets that they have created.
    public enum Roles {STANDARD, MANAGER}

    //Information about the employee.
    // These may be public because they are finalized when this class is instantiated.
    public final String email; //This employee's email address
    public final String password; //This employee's password
    public final Roles role; //This employee's role (this is used to determine their permissions)
    public final int id; //This employee's ID in the database (this is mostly unused)

    public Employee(String email, String password, Roles role, int id){
        this.email = email;
        this.password = password;
        this.role = role;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}