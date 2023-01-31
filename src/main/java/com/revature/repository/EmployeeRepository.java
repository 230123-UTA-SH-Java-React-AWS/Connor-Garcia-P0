package com.revature.repository;

import com.revature.model.Employee;

/**
 * This class is how [EmployeeService] interacts with the database
 */
public class EmployeeRepository {

    /**
     * This function attempts to add an employee to the database.
     * It returns a string which should then be passed to the endpoint, indicating what happened when
     * this function was run. For instance, if the new employee was successfully added, it should be
     * "Employee successfully registered". If not, it should have a specific reason such as "There is already an
     * employee with that email address!"
     *
     * @param employee The employee to add; should be generated by [EmployeeService]
     * @return a string which indicates what was done.
     */
    public String createNewEmployee(Employee employee){

        return "THIS TEXT SHOULD NEVER APPEAR.";
    }

    /**
     * This attempts to log in a user, given an email address and password.
     * I do not yet have the implementation details down, so the void return
     * type should not be considered final.
     *
     * @param email The email address of the employee who is attempting to log in
     * @param password The password of the employee who is attempting to log in
     */
    public void loginEmployee(String email, String password){

    }
}
