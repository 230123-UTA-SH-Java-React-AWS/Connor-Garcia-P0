package com.revature.controller;

import com.revature.service.TicketService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ControllerViewMyTickets extends Controller{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpVerb = exchange.getRequestMethod();
        if(httpVerb.equals("GET")){
            String body = getRequestBodyString(exchange);
            sendResponse(exchange,
                    200,
                    TicketService.getMyTickets(body));
        } else {
            sendResponse(exchange, 403, "That action is prohibited.");
        }
    }
}
