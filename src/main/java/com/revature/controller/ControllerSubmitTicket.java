package com.revature.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class ControllerSubmitTicket implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        throw new IOException();
    }
}