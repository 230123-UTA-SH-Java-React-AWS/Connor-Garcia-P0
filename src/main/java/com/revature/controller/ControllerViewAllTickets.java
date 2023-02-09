package com.revature.controller;

import com.revature.service.TicketService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ControllerViewAllTickets extends Controller {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpVerb = exchange.getRequestMethod();
        if(httpVerb.equals("GET")){
            String body = getRequestBodyString(exchange);
            sendResponse(exchange,
                    TicketService.getTicketsFiltered(body));
        } else {
            sendResponse(exchange, 405, "That action is prohibited.");
        }
    }
}
