package com.revature.model;

public class Employee {
    public enum Roles {
        STANDARD("STANDARD"),
        MANAGER("MANAGER");

        Roles(String role) { }
    }
    private String email;
    private String password;
    private Roles role = Roles.STANDARD;

    //Required for Jackson
    public Employee(){ super(); }

    public Employee(String email, String password, Roles role){
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public Roles getRole() {return role;}

    public void setRole(Roles role){
        this.role = role;
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
