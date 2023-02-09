package com.revature.controller;

import com.revature.model.Employee;
import com.revature.service.EmployeeService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ControllerPromoteUser extends Controller {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpVerb = exchange.getRequestMethod();
        if(httpVerb.equals("PUT")){
            String body = getRequestBodyString(exchange);
            sendResponse(exchange,
                    EmployeeService.alterEmployeeRole(body, Employee.Roles.MANAGER));
        } else {
            sendResponse(exchange, 405, "That action is prohibited.");
        }
    }
}