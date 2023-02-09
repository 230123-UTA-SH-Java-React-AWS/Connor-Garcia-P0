package com.revature.model;

/**
 * @param email    Information about the employee. These may be public because they are finalized when this class is instantiated.This employee's email address
 * @param password This employee's password
 * @param role     This employee's role (this is used to determine their permissions)
 * @param id       This employee's ID in the database (this is mostly unused)
 */
public record Employee(String email, int password, com.revature.model.Employee.Roles role, int id) {
    public Employee {
        //Validation
        if (email == null) {
            throw new IllegalStateException("The email of an employee may not be null");
        }
        if (role == null) {
            throw new IllegalStateException("The role of an employee may not be null (try Employee.Roles.STANDARD)");
        }
        if (id < 1) {
            throw new IllegalStateException("The ID of an employee must be at least 1");
        }

        //Initialization
    }

    @Override
    public String toString() {
        return "Employee{" +
                "email='" + email() + '\'' +
                ", password='" + password() + '\'' +
                ", role=" + role() +
                '}';
    }

    //Roles that an employee can have. These define their permissions -- for instance,
    // a finance manager may view all tickets in the database, while a standard
    // employee can only view tickets that they have created.
    public enum Roles {STANDARD, MANAGER}
}