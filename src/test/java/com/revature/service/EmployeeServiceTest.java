package com.revature.service;


import com.revature.model.Employee;
import com.revature.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static com.revature.service.EmployeeService.getAllEmployees;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

// -------------------- Code in this class was created as part of a demonstration during a
// lecture and does not have anything to do with the rest of this program ----------------

public class EmployeeServiceTest {

    @Test
    public void Get_All_Employees_Should_Give_All_Employees(){
        //Arrange
        // Mockito usage
        EmployeeRepository mockEmployeeRepository = Mockito.mock(EmployeeRepository.class);
        // How to inject this Mockito dependency into the class?
        // Create a constructor in the class and put into the parameter the class dependency
        // This is called the DI (Dependency Injection) pattern
        /*
            //Downside to this - it forces us to provide the dependency
            // everywhere that the class would otherwise be used.
            public EmployeeService(EmployeeRepository emplRepo){
                this.emplRepo = emplRepo;
            }
            //Upside? Frameworks can detect the DI pattern to automatically handle it.
         */
        // Telling Mockito to guarantee that if a specific method was called in our mock object,
        // return a hardcoded value
        ArrayList<Employee> expectedListOfEmployees = new ArrayList<>();
        expectedListOfEmployees.add(new Employee("JimothyJones@inter.net", "JJTheDude", Employee.Roles.STANDARD, 1));
        expectedListOfEmployees.add(new Employee("Exa.Mple@example.com", "example", Employee.Roles.MANAGER, 2));
        expectedListOfEmployees.add(new Employee("Null@?.com", "Void", Employee.Roles.STANDARD, 3));
        Mockito.when(mockEmployeeRepository.getAllEmployees()).thenReturn(expectedListOfEmployees);

        //Act
        String jsonListEmployees = getAllEmployees("""
                {
                    "email":"example@inter.net",
                    "password":"blah blah blah"
                }
                """);
        //Assert
        assertNotEquals("", jsonListEmployees);
    }
}
