package com.revature.controller;

import com.revature.service.EmployeeService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Objects;

public class ControllerGetEmployees extends Controller {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpVerb = exchange.getRequestMethod();
        if(Objects.equals(httpVerb, "GET")) {
            sendResponse(exchange, 200, EmployeeService.getAllEmployees());
        } else {
            sendResponse(exchange, 403, "That action is prohibited.");
        }
    }
}
