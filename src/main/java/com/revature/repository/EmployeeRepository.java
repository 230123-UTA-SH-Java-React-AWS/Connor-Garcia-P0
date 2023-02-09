package com.revature.repository;

import com.revature.controller.Controller;
import com.revature.model.Employee;
import com.revature.utils.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is how [EmployeeService] interacts with the database
 */
public class EmployeeRepository {
    //Attempt to add an employee to the database; this will return a String indicating
    // what happened when this function was called
    public Controller.WebTuple createNewEmployee(String email, String password) {
        if (getEmployeeByEmail(email) != null) return new Controller.WebTuple(403, "That email address is already in use.");

        String sql = "INSERT INTO Employees (emplEmail, emplPassword) values (?, ?)";
        try (Connection con = ConnectionUtil.getConnection()) {
            PreparedStatement prst = con.prepareStatement(sql);

            prst.setString(1, email);
            prst.setInt(2, password.hashCode());

            prst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return new Controller.WebTuple(500, email + " could not be registered.");
        }

        return new Controller.WebTuple(200, email + " was successfully registered.");
    }

    /**
     * This attempts to get the Employee object associated with a particular email address.
     * @param email The email of the employee to find
     * @return An Employee representing the one in the database if it exists; null otherwise
     */
    public Employee getEmployeeByEmail(String email) {
        List<Employee> employeeList = new ArrayList<>();
        Employee employee = null;

        //Querying the database
        String sql = "SELECT emplid, emplemail, emplpassword, rolename FROM Employees " +
                "INNER JOIN roles ON emplrole = roleid " +
                "WHERE emplemail = ?";
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
                    default -> throw new IllegalStateException("BAD DATA IN EMPLOYEE TABLE");
                };
                Employee empl = new Employee(rs.getString(2), rs.getInt(3), eRole, rs.getInt(1));
                employeeList.add(empl);
            }
        } catch (SQLException | IllegalStateException e) {
            e.printStackTrace();
            return null;
        }
        if(employeeList.size() > 0) employee = employeeList.get(0);
        return employee;
    }

    //Gets the ID of an employee associated with the given email address
    public int getEmployeeId(String email){
        if(email == null) return -1;
        List<Integer> employeeIdList = new ArrayList<>();
        int employeeId = -1;

        //Querying the database
        String sql = "SELECT emplid FROM Employees WHERE emplemail = ?";
        //Create the connection
        try (Connection con = ConnectionUtil.getConnection()) {
            //Create the querying object
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, email);
            //Execute the query
            ResultSet rs = stmt.executeQuery();
            //Mapping information from a table to our data structure
            while (rs.next()) {
                employeeIdList.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        if(employeeIdList.size() > 0) employeeId = employeeIdList.get(0);
        return employeeId;
    }

    /**
     * Creates a list of Employees representing every employee in the database.
     * @return An ArrayList representing containing all information about every employee in the database.
     */
    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> employeeList = new ArrayList<>();

        //Querying the database
        String sql = "SELECT emplid, emplemail, emplpassword, rolename FROM Employees " +
                "INNER JOIN roles ON emplrole = roleid ";
        //Create the connection
        try (Connection con = ConnectionUtil.getConnection()) {
            //Create the querying object
            Statement stmt = con.createStatement();
            //Execute the query
            ResultSet rs = stmt.executeQuery(sql);
            //Mapping information from a table to our data structure
            while (rs.next()) {
                Employee.Roles eRole = Employee.Roles.STANDARD;
                if(Objects.equals(rs.getString(4), "MANAGER")) eRole  = Employee.Roles.MANAGER;

                Employee empl = new Employee(rs.getString(2), rs.getInt(3), eRole, rs.getInt(1));
                employeeList.add(empl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return employeeList;
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
     */
    public Controller.WebTuple alterEmployeeRole(String email, String password, String otherUserEmail, Employee.Roles newRole) {
        Employee manager = getEmployeeByEmail(email);
        Employee toPromote = getEmployeeByEmail(otherUserEmail);
        //Error handling - the order here is important. In this order, a potential bad actor cannot use this function to determine
        // what employees do and do not exist unless they are already a manager.
        if(manager == null) return new Controller.WebTuple(403, "Failed to verify credentials.");
        if(!Objects.equals(manager.getPassword(), password.hashCode())) return new Controller.WebTuple(403, "Failed to verify credentials.");
        if(manager.getRole() != Employee.Roles.MANAGER) return new Controller.WebTuple(403, "You are not authorized to perform this action.");
        if(toPromote == null) return new Controller.WebTuple(400, "The account whose role you are changing doesn't exist!");

        //Performing the database action
        String sql = "UPDATE employees SET emplrole = ? WHERE emplemail = ?";
        //Create the connection
        try (Connection con = ConnectionUtil.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, newRole.ordinal() + 1);
            stmt.setString(2, otherUserEmail);
            //Execute the query
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return new Controller.WebTuple(500, "Something went wrong, likely on the database side.");
        }
        return new Controller.WebTuple(200, otherUserEmail + " is now a " + newRole + " kind of employee!");
    }
}
