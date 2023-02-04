package com.revature;

import com.revature.controller.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        if(server == null) throw new RuntimeException("The server failed to start.");

        //Providing endpoint URLs (actions affecting employees)
        server.createContext("/getEmployeeList", new ControllerGetEmployees());
        server.createContext("/register", new ControllerRegisterUser());
        server.createContext("/login", new ControllerLoginUser());
        server.createContext("/promoteEmployee", new ControllerPromoteUser());
        server.createContext("/demoteEmployee", new ControllerDemoteUser());

        //Providing endpoint URLs (actions affecting tickets)
        server.createContext("/submitTicket", new ControllerSubmitTicket());
        server.createContext("/allTickets", new ControllerViewAllTickets());
        server.createContext("/myTickets", new ControllerViewMyTickets());
        server.createContext("/finalizeTicket", new ControllerFinalizeTicket());

        server.setExecutor(null);
        System.out.println("Server is running...");
        server.start();
    }
}