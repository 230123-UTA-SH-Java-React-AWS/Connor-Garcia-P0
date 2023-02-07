package com.revature.service;

import com.revature.model.Employee;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class EmployeeService extends Service{
    /**
     * Gets a list of all employees and formats it in JSON. This action requires the authorization
     * of a manager (achieved here by checking their credentials against the database.
     *
     * @param json A JSON String containing the email address and password of a manager
     * @return JSON text representing all employee data.
     */
    public static String getAllEmployees(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to get all employees";
        }
        JsonNode emailNode = jsonNode.get("email");
        JsonNode passwordNode = jsonNode.get("password");
        if (emailNode == null || passwordNode == null) {
            return """
                    Could not add employee; malformed request.
                    Correct format for request body:
                      {
                        "email":"<email address>",
                        "password":"<password>"
                      }
                    """;
        }
        String email = emailNode.asText();
        String password = passwordNode.asText();

        Employee manager = EMPLOYEE_REPOSITORY.getEmployeeByEmail(email);
        if (manager == null) return "That email address does not belong to any employee!";
        if (!Objects.equals(manager.getEmail(), email) || !Objects.equals(manager.getPassword(), password))
            return "Failed to verify credentials; incorrect password.";
        if (manager.getRole() != Employee.Roles.MANAGER) return "Only a manager can perform this action.";

        //By this line, we have successfully verified that the person attempting this action is a manager.
        //Create a list of all employees
        List<Employee> employeeList = EMPLOYEE_REPOSITORY.getAllEmployees();
        //Format all entries as JSON and return them
        return makeJsonOf(employeeList);
    }

    //This function expects a valid employee from JSON.
    //It will return a String which is passed to the endpoint.
    // This string indicates what happened when this function was called.
    public static String registerEmployee(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode emailNode = jsonNode.get("email");
            JsonNode passwordNode = jsonNode.get("password");
            if (emailNode == null || passwordNode == null) {
                return """
                        Could not add employee; malformed request.
                        Correct format for request body:
                          {
                            "email":"<email address>",
                            "password":"<password>"
                          }
                        """;
            }
            String email = emailNode.asText();
            String password = passwordNode.asText();
            //Under normal circumstances, validation is performed here. Emails are checked for correctness, passwords hashed etc.
            return EMPLOYEE_REPOSITORY.createNewEmployee(email, password);
        } catch (IOException e) {
            e.printStackTrace();
            return "Something went wrong, likely with Jackson.";
        }
    }

    /**
     * Validates an employee. This means comparing the given email address and password to what is in the database
     * in order to determine if the given credentials match an existing employee
     *
     * @param json A JSON string containing the relevant information
     * @return A string describing the result of this operation; this is passed back to the endpoint.
     */
    public static String validateEmployee(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode emailNode = jsonNode.get("email");
            JsonNode passwordNode = jsonNode.get("password");
            if (emailNode == null || passwordNode == null) {
                return """
                        Could not add employee; malformed request.
                        Correct format for request body:
                          {
                            "email":"<email address>",
                            "password":"<password>"
                          }
                        """;
            }
            String email = emailNode.asText();
            String password = passwordNode.asText();
            Employee toValidate = EMPLOYEE_REPOSITORY.getEmployeeByEmail(email);
            if (toValidate == null) return "That employee does not exist.";
            if (!Objects.equals(toValidate.getPassword(), password)) return "Incorrect password for " + email;
            return "Successfully logged in";
        } catch (IOException e) {
            e.printStackTrace();
            return "Something went wrong, likely with Jackson.";
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
    public static String alterEmployeeRole(String json, Employee.Roles newRole) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode emailNode = jsonNode.get("email");
            JsonNode passwordNode = jsonNode.get("password");
            JsonNode otherUserNode = jsonNode.get("otherUserEmail");
            if (emailNode == null || passwordNode == null || otherUserNode == null) {
                return """
                        Could not add employee; malformed request.
                        Correct format for request body:
                          {
                            "email":"<email address>",
                            "password":"<password>",
                            "otherUserEmail":"<other user's email address>"
                          }
                        """;
            }
            String email = emailNode.asText();
            String password = passwordNode.asText();
            String otherUserEmail = otherUserNode.asText();
            return EMPLOYEE_REPOSITORY.alterEmployeeRole(email, password, otherUserEmail, newRole);
        } catch (IOException e) {
            e.printStackTrace();
            return "Something went wrong, likely with Jackson.";
        }
    }
}
