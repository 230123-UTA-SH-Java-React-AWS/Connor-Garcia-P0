package com.revature;

import com.revature.model.Employee;
import com.revature.repository.EmployeeRepository;
import com.revature.utils.ConnectionUtil;

public class Main {
    public static void main(String[] args) {
        EmployeeRepository e = new EmployeeRepository();
        e.createNewEmployee(
                new Employee(
                        "jimothyjones@inter.net",
                        "password123"));
    }
}