package com.revature.service;

import com.revature.model.Employee;
import com.revature.repository.EmployeeRepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class EmployeeService {

    public static String getAllEmployees(){
        //Repository to access the database
        EmployeeRepository repo = new EmployeeRepository();
        //Create a list of all employees
        List<Employee> employeeList = repo.getAllEmployees();
        //Format all entries as JSON and return them
        return jsonize(employeeList);
    }

    //This function expects a valid employee from JSON.
    //It will return a String which is passed to the endpoint.
    // This string indicates what happened when this function was called.
    public static String registerEmployee(String json){
        EmployeeRepository repo = new EmployeeRepository();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode emailNode = jsonNode.get("email");
            JsonNode passwordNode = jsonNode.get("password");
            if(emailNode == null || passwordNode == null) {
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
            return repo.createNewEmployee(email, password);
        } catch (IOException e) {
            e.printStackTrace();
            return "Something went wrong, likely with Jackson.";
        }
    }

    /**
     * Validates an employee. This means comparing the given email address and password to what is in the database
     * in order to determine if the given credentials match an existing employee
     * @param json A JSON string containing the relevant information
     * @return A string describing the result of this operation; this is passed back to the endpoint.
     */
    public static String validateEmployee(String json){
        EmployeeRepository repo = new EmployeeRepository();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode emailNode = jsonNode.get("email");
            JsonNode passwordNode = jsonNode.get("password");
            if(emailNode == null || passwordNode == null) {
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
            return repo.loginEmployee(email, password);
        } catch (IOException e) {
            e.printStackTrace();
            return "Something went wrong, likely with Jackson.";
        }
    }


    public static String alterEmployeeRole(String json, Employee.Roles newRole) {
        EmployeeRepository repo = new EmployeeRepository();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode emailNode = jsonNode.get("email");
            JsonNode passwordNode = jsonNode.get("password");
            JsonNode otherUserNode = jsonNode.get("otherUserEmail");
            if(emailNode == null || passwordNode == null || otherUserNode == null) {
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
            return repo.alterEmployeeRole(email, password, otherUserEmail, newRole);
        } catch (IOException e) {
            e.printStackTrace();
            return "Something went wrong, likely with Jackson.";
        }
    }

    public static String demoteEmployee(String json){
        return null;
    }

    //Uses Jackson to convert any object to a string.
    private static String jsonize(Object o){
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

}
