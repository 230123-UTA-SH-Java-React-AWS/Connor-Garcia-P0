package com.revature.controller;

import com.revature.service.EmployeeService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ControllerLoginUser extends Controller {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpVerb = exchange.getRequestMethod();
        if(httpVerb.equals("POST")){
            String body = getRequestBodyString(exchange);
            sendResponse(exchange,
                    EmployeeService.validateEmployee(body));
        } else {
            sendResponse(exchange, 405, "That action is prohibited.");
        }
    }
}