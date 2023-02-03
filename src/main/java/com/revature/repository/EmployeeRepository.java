package com.revature.repository;

import com.revature.model.Employee;
import com.revature.utils.ConnectionUtil;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is how [EmployeeService] interacts with the database
 */
public class EmployeeRepository {

    //Attempt to add an employee to the database; this will return a String indicating
    // What happened when this function was called
    public String createNewEmployee(String email, String password) {
        try {
            if (getEmployeeByEmail(email) != null) return "That email address is already in use.";
        } catch (IOException e) {
            return "Unknown problem occurred.";
        }

        String sql = "INSERT INTO Employees (emplEmail, emplPassword, emplRole) values (?, ?, ?)";
        try (Connection con = ConnectionUtil.getConnection()) {
            PreparedStatement prst = con.prepareStatement(sql);

            prst.setString(1, email);
            prst.setString(2, password);
            prst.setString(3, Employee.Roles.STANDARD.toString());

            prst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return email + " could not be registered.";
        }

        return email + " was successfully registered.";
    }

    /**
     * This attempts to get the Employee object associated with a particular email address.
     * @param email The email of the employee to find
     * @return An Employee representing the one in the database if it exists; null otherwise
     * @throws IOException if the database connection fails.
     */
    public Employee getEmployeeByEmail(String email) throws IOException {
        List<Employee> employeeList = new ArrayList<>();
        Employee employee = null;

        //Querying the database
        String sql = "SELECT * FROM Employees WHERE emplemail = ?";
        //Create the connection
        try (Connection con = ConnectionUtil.getConnection()) {
            //Create the querying object
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, email);
            //Execute the query
            ResultSet rs = stmt.executeQuery();
            //Mapping information from a table to our data structure
            while (rs.next()) {
                //This switch statement (which should be an if statement) is here because it adds to scalability.
                //If more roles are added later (ADMIN, etc.), then this is easier to update.
                Employee.Roles eRole = switch (rs.getString(4)) {
                    case "MANAGER" -> Employee.Roles.MANAGER;
                    case "STANDARD" -> Employee.Roles.STANDARD;
                    default -> null;
                };
                if(eRole == null) eRole = Employee.Roles.STANDARD;
                Employee empl = new Employee(rs.getString(2), rs.getString(3), eRole);
                employeeList.add(empl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IOException(e);
        }

        if(employeeList.size() > 0) employee = employeeList.get(0);
        return employee;
    }

    /**
     * Creates a list of Employees representing every employee in the database.
     * @return An ArrayList representing containing all information about every employee in the database.
     */
    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> employeeList = new ArrayList<>();

        //Querying the database
        String sql = "SELECT * FROM Employees";
        //Create the connection
        try (Connection con = ConnectionUtil.getConnection()) {
            //Create the querying object
            Statement stmt = con.createStatement();
            //Execute the query
            ResultSet rs = stmt.executeQuery(sql);
            //Mapping information from a table to our data structure
            while (rs.next()) {
                //This switch statement (which should be an if statement) is here because it adds to scalability.
                //If more roles are added later (ADMIN, etc.), then this is easier to update.
                Employee.Roles eRole = switch (rs.getString(4)) {
                    case "MANAGER" -> Employee.Roles.MANAGER;
                    case "STANDARD" -> Employee.Roles.STANDARD;
                    default -> null;
                };
                if(eRole == null) eRole = Employee.Roles.STANDARD;

                Employee empl = new Employee(rs.getString(2), rs.getString(3), eRole);
                employeeList.add(empl);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return employeeList;
    }

    /**
     * This attempts to log in an employee, and returns a string to signify what happened as a result of that operation.
     * This differs from validateEmployee() in that validate only returns true/false depending on whether the credentials are
     * valid, while login describes if the login succeeded or failed (and why that was the case) using a String.
     * @param email The email address of the employee who is logging in.
     * @param password The password of the employee who is logging in.
     * @return A string (which should be passed to the endpoint) describing the result of this operation.
     * @throws IOException if the underlying interaction with the database fails.
     */
    public String loginEmployee(String email, String password) throws IOException {
        Employee toValidate = getEmployeeByEmail(email);
        if(toValidate == null) return "That employee does not exist.";
        if(!Objects.equals(toValidate.getPassword(), password)) return "Incorrect password for " + email;
        return "Successfully logged in";
    }

    /**
     * This validates a user's credentials against the database.
     * @param email The email address of the user to be validated.
     * @param password The password of the user to be validated.
     * @return true if the user's credentials match an entry in the database, false otherwise.
     * @throws IOException If the underlying interaction with the database fails.
     */
    public boolean validateEmployee(String email, String password) throws IOException {
        Employee toValidate = getEmployeeByEmail(email);
        if(toValidate == null) return false;
        return Objects.equals(toValidate.getPassword(), password);
    }

    //Alternate way to validate an employee when there is already an Employee object representing them.
    //This just saves some typing later on.
    public boolean validateEmployee(Employee employee, String email, String password){
        return employee.getEmail().equals(email) && employee.getPassword().equals(password);
    }

    /**
     * This attempts to promote/demote the employee specified in otherUserEmail to a new role, but can only do so if the
     * credentials supplied (email/password) point to a manager's account in the database.
     * This will return a String describing the result of this operation, whether successful or otherwise.
     *
     * @param email          The email address which should point to a manager's account
     * @param password       The password which is used to log in to the manager's account.
     * @param otherUserEmail The email address of the account whose role is to change.
     * @param newRole        The new role of the account.
     * @return A string describing the result of this operation. This operation will fail if the
     * supplied credentials are incorrect or otherUserEmail does not point to any entry in the database.
     * @throws IOException if the underlying database query fails.
     */
    public String alterEmployeeRole(String email, String password, String otherUserEmail, Employee.Roles newRole) throws IOException {
        Employee manager = getEmployeeByEmail(email);
        Employee toPromote = getEmployeeByEmail(otherUserEmail);
        //Error handling
        if(manager == null) return "That manager account doesn't exist!";
        if(toPromote == null) return "The account to be promoted doesn't exist!";
        if(!validateEmployee(manager, email, password)) return "The manager account failed to log in!";
        if(manager.getRole() != Employee.Roles.MANAGER) return "That account does not have permission to perform this action!";

        //Performing the database action
        String sql = "UPDATE employees SET emplrole = ? WHERE emplemail = ?";
        //Create the connection
        try (Connection con = ConnectionUtil.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, newRole.toString());
            stmt.setString(2, otherUserEmail);
            //Execute the query
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return otherUserEmail + " is now a " + newRole + " kind of employee!";
    }
}
