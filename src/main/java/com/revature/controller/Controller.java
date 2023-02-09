package com.revature.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class Controller implements HttpHandler {
    protected void sendResponse(HttpExchange exchange, int statusCode, String toSend) throws IOException {
        exchange.sendResponseHeaders(statusCode, toSend.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(toSend.getBytes());
        os.close();
    }

    protected void sendResponse(HttpExchange exchange, WebTuple tuple) throws IOException {
        sendResponse(exchange, tuple.statusCode(), tuple.response());
    }

    protected String getRequestBodyString(HttpExchange exchange) {
        InputStream is = exchange.getRequestBody();

        //Converting InputStream into a String
        StringBuilder textBuilder = new StringBuilder();

        //ASCII
        try (Reader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return textBuilder.toString();
    }

    //This subclass is used by Controllers to send both the information about what happened when fulfilling a request and
    // an HTTP status code relevant to that response.
    public record WebTuple(int statusCode, String response) {}
}
