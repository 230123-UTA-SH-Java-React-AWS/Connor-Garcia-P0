package com.revature.service;

import com.revature.controller.Controller;
import com.revature.model.Employee;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class EmployeeService extends Service{
    /**
     * Gets a list of all employees and formats it in JSON. This action requires the authorization
     * of a manager (achieved here by checking their credentials against the database).
     *
     * @param json A JSON String containing the email address and password of a manager
     * @return JSON text representing all employee data.
     */
    public static Controller.WebTuple getAllEmployees(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
            return new Controller.WebTuple(400, "Something went wrong. Did you send a malformed request?");
        }
        JsonNode emailNode = jsonNode.get("email");
        JsonNode passwordNode = jsonNode.get("password");
        if (emailNode == null || passwordNode == null) {
            return new Controller.WebTuple(400, """
                    Could not add employee; missing data in request.
                    Correct format for request body:
                      {
                        "email":"<email address>",
                        "password":"<password>"
                      }
                    """);
        }
        String email = emailNode.asText();
        String password = passwordNode.asText();

        Employee manager = EMPLOYEE_REPOSITORY.getEmployeeByEmail(email);
        if (manager == null) return new Controller.WebTuple(403, "Failed to verify credentials.");
        if (!Objects.equals(manager.getEmail(), email) || !Objects.equals(manager.getPassword(), password.hashCode()))
            return new Controller.WebTuple(403, "Failed to verify credentials.");
        if (manager.getRole() != Employee.Roles.MANAGER) return new Controller.WebTuple(403, "Only a manager can perform this action.");

        //By this line, we have successfully verified that the person attempting this action is a manager.
        //Create a list of all employees
        List<Employee> employeeList = EMPLOYEE_REPOSITORY.getAllEmployees();
        if(employeeList == null)
            return new Controller.WebTuple(500, "Something went wrong while retrieving the employee list");
        //Format all entries as JSON and return them
        return new Controller.WebTuple(200, makeJsonOf(employeeList));
    }

    //This function expects a valid employee from JSON.
    //It will return a String which is passed to the endpoint.
    // This string indicates what happened when this function was called.
    public static Controller.WebTuple registerEmployee(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode emailNode = jsonNode.get("email");
            JsonNode passwordNode = jsonNode.get("password");
            if (emailNode == null || passwordNode == null) {
                return new Controller.WebTuple(400, """
                        Could not add employee; missing data in request.
                        Correct format for request body:
                          {
                            "email":"<email address>",
                            "password":"<password>"
                          }
                        """);
            }
            String email = emailNode.asText();
            String password = passwordNode.asText();

            if(email.equals("")) return new Controller.WebTuple(403, "You cannot register an employee without an email address!");
            if(password.equals("")) return new Controller.WebTuple(403, "You cannot register an employee without a password!");

            return EMPLOYEE_REPOSITORY.createNewEmployee(email, password);
        } catch (IOException e) {
            e.printStackTrace();
            return new Controller.WebTuple(400,"Something went wrong. Did you send a malformed request?");
        }
    }

    /**
     * Validates an employee. This means comparing the given email address and password to what is in the database
     * in order to determine if the given credentials match an existing employee
     *
     * @param json A JSON string containing the relevant information
     * @return A string describing the result of this operation; this is passed back to the endpoint.
     */
    public static Controller.WebTuple validateEmployee(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode emailNode = jsonNode.get("email");
            JsonNode passwordNode = jsonNode.get("password");
            if (emailNode == null || passwordNode == null) {
                return new Controller.WebTuple(400, """
                        Could not add employee; missing data in request.
                        Correct format for request body:
                          {
                            "email":"<email address>",
                            "password":"<password>"
                          }
                        """);
            }
            String email = emailNode.asText();
            String password = passwordNode.asText();
            Employee toValidate = EMPLOYEE_REPOSITORY.getEmployeeByEmail(email);
            if (toValidate == null) return new Controller.WebTuple(403, "Incorrect email address or password.");
            if (!Objects.equals(toValidate.getPassword(), password.hashCode())) return new Controller.WebTuple(403, "Incorrect email address or password.");
            return new Controller.WebTuple(200, "Successfully logged in");
        } catch (IOException e) {
            e.printStackTrace();
            return new Controller.WebTuple(400, "Something went wrong. Did you send a malformed request?");
        }
    }

    /**
     * Function for altering the role of an employee using the authorization of a manager.
     * Fails if either employee does not exist, the credentials do not match with the manager's credentials, or
     * if the "manager employee" is not actually a manager.
     *
     * @param json    A JSON string with three inputs:
     *                1. email - the email address of an employee authorized to promote/demote another employee (a manager)
     *                2. password - the password of that same privileged employee
     *                3. otherUserEmail - the email address of the other employee whose role is changed
     * @param newRole The new role of the user if this function was successfully called.
     * @return A descriptive String indicating what happened when this function was run.
     */
    public static Controller.WebTuple alterEmployeeRole(String json, Employee.Roles newRole) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode emailNode = jsonNode.get("email");
            JsonNode passwordNode = jsonNode.get("password");
            JsonNode otherUserNode = jsonNode.get("otherUserEmail");
            if (emailNode == null || passwordNode == null || otherUserNode == null) {
                return new Controller.WebTuple(400, """
                        Could not add employee; missing data in request.
                        Correct format for request body:
                          {
                            "email":"<email address>",
                            "password":"<password>",
                            "otherUserEmail":"<other user's email address>"
                          }
                        """);
            }
            String email = emailNode.asText();
            String password = passwordNode.asText();
            String otherUserEmail = otherUserNode.asText();
            return EMPLOYEE_REPOSITORY.alterEmployeeRole(email, password, otherUserEmail, newRole);
        } catch (IOException e) {
            e.printStackTrace();
            return new Controller.WebTuple(400, "Something went wrong. Did you send a malformed request?");
        }
    }
}
