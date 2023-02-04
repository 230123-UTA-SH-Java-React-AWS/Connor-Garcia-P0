package com.revature.controller;

import com.revature.model.Employee;
import com.revature.service.EmployeeService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ControllerDemoteUser extends Controller {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpVerb = exchange.getRequestMethod();
        if(httpVerb.equals("PUT")){
            String body = getRequestBodyString(exchange);
            sendResponse(exchange,
                    200,
                    EmployeeService.alterEmployeeRole(body, Employee.Roles.STANDARD));
        } else {
            sendResponse(exchange, 403, "That action is prohibited.");
        }
    }
}