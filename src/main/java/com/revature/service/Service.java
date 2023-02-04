package com.revature.service;

import com.revature.repository.EmployeeRepository;
import com.revature.repository.TicketRepository;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public abstract class Service {
    protected static final EmployeeRepository EMPLOYEE_REPOSITORY = new EmployeeRepository();
    protected static final TicketRepository TICKET_REPOSITORY = new TicketRepository();

    //Uses Jackson to convert any object to a string.
    protected static String jsonize(Object o) {
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
