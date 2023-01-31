package com.revature.model;

public class Employee {
    public enum Roles {
        STANDARD("STANDARD"),
        MANAGER("MANAGER");

        Roles(String role) { }
    }
    private final String email;
    private final String password;
    private Roles role = Roles.STANDARD;

    public Employee(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public Roles getRole() {return role;}

    /**
     * This attempts to update an employee's role to a manager (and can be used to
     * demote a manager back to a standard employee). This function requires a
     * manager's permission in order to do so, and obtains this permission
     * by querying the database to see if the provided username/password
     * match a manager's account.
     * @param mEmail The email address of an employee who has the managerial
     *               permissions necessary to modify this employee's role.
     * @param mPass The password of the manager.
     * @param role The new role to set role to if the manager check is successful
     */
    public void setRole(String mEmail, String mPass, Roles role){
        //Query the database for someone who matches the email/password combination
        //If that employee does not exist, fail outright.
        //If that employee does exist, check if they have the manager role.
        //If that employee has manager permissions, update this user's role.
        this.role = role;
    }

}
