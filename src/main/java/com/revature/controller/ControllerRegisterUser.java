package com.revature.controller;

import com.revature.service.EmployeeService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ControllerRegisterUser extends Controller {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpVerb = exchange.getRequestMethod();
        if(httpVerb.equals("POST")){
            String body = getRequestBodyString(exchange);
            sendResponse(exchange,
                    200,
                    EmployeeService.registerEmployee(body));
        } else {
            sendResponse(exchange, 403, "That action is prohibited.");
        }
    }
}