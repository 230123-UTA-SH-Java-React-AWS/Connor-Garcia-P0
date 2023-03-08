package com.revature.controller;

import com.revature.service.EmployeeService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Objects;

public class ControllerGetEmployees extends Controller {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpVerb = exchange.getRequestMethod();
        String body = getRequestBodyString(exchange);

        if(Objects.equals(httpVerb, "GET")) {
            sendResponse(exchange, EmployeeService.getAllEmployees(body));
        } else {
            sendResponse(exchange, 405, "That action is prohibited.");
        }
    }
}
