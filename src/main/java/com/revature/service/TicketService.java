package com.revature.service;

import com.revature.controller.Controller;
import com.revature.model.Employee;
import com.revature.model.Ticket;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class TicketService extends Service {

    /**
     * This will finalize a ticket in the database by changing its status to be either
     * APPROVED or DENIED. A ticket which has already been finalized cannot be altered again.
     * This action requires a manager's credentials.
     * @param json A JSON String containing the relevant information: manager's credentials and a ticket ID.
     * @return A String indicating what happened when this action was attempted.
     */
    public static Controller.WebTuple finalizeTicket(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
            return new Controller.WebTuple(400, "Something went wrong. Did you send a malformed request?");
        }
        JsonNode emailNode = jsonNode.get("email");
        JsonNode passwordNode = jsonNode.get("password");
        JsonNode ticketIDNode = jsonNode.get("ticketID");
        JsonNode newStatusNode = jsonNode.get("newStatus");
        if (emailNode == null || passwordNode == null || ticketIDNode == null || newStatusNode == null) {
            return new Controller.WebTuple(400, 
                    "Could not add employee; missing data in request.\n" +
                    "Correct format for request body:\n" +
                    "  {\n" +
                    "    'email':'<email address>',\n" +
                    "    'password':'<password>',\n" +
                    "    'ticketID':<id of the ticket to finalize>,\n" +
                    "    'newStatus':'<APPROVED/DENIED>'\n" +
                    "  }"
                    );
        }
        //Manager's credentials
        String email = emailNode.asText();
        String password = passwordNode.asText();
        //Ticket information
        int ticketID = ticketIDNode.asInt();
        Ticket.StatusValues newStatus; 
        switch(newStatusNode.asText().toLowerCase()){
            case "approved": newStatus =  Ticket.StatusValues.APPROVED; break;
            case "denied": newStatus =  Ticket.StatusValues.DENIED; break;
            default: newStatus =  null;
        };

        //Input validation
        if(newStatus == null) {
            return new Controller.WebTuple(400, "That is not a valid status to update the ticket to.");
        }

        //Verifying that the credentials given are correct and come from a manager
        Employee manager = EMPLOYEE_REPOSITORY.getEmployeeByEmail(email);
        if (manager == null) return new Controller.WebTuple(403, "Failed to verify credentials.");
        if (!Objects.equals(manager.getEmail(), email) || !Objects.equals(manager.getPassword(), password.hashCode()))
            return new Controller.WebTuple(403, "Failed to verify credentials.");
        if (manager.getRole() != Employee.Roles.MANAGER) return new Controller.WebTuple(403, "Only a manager can perform this action.");

        //By this line, we have successfully verified that the person attempting this action is a manager.
        //Update the ticket in the database and tell the user what happened
        return TICKET_REPOSITORY.finalizeTicketByID(ticketID, newStatus);
    }

    /**
     * Gets every ticket from the database. This operation requires a finance manager's credentials.
     * The tickets retrieved can optionally be filtered based on information about the tickets themselves;
     * this can mean finding tickets from a specific employee, tickets that are pending/approved/denied, tickets
     * that ask to reimburse a specific type of purchase (food/lodging/etc), or any combination of these filters.
     * @param json A JSON String that contains both a manager's credentials and any applicable
     *             filters to apply to the data.
     * @return A string containing a list of all tickets that match the filters (or all tickets if there are no
     *  filters), or a string containing a relevant error message if the operation was not successful.
     */
    public static Controller.WebTuple getTicketsFiltered(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
            return new Controller.WebTuple(400, "Something went wrong. Did you send a malformed request?");
        }
        JsonNode emailNode = rootNode.get("email");
        JsonNode passwordNode = rootNode.get("password");
        JsonNode fromEmployeeNode = rootNode.get("fromEmployee");
        JsonNode statusNode = rootNode.get("status");
        JsonNode typeNode = rootNode.get("type");
        if (emailNode == null || passwordNode == null) {
            return new Controller.WebTuple(400,
                    "Could not add employee; missing data in request.\n" +
                    "Correct format for request body:\n" +
                    "  {\n" +
                    "    'email':'<email address>',\n" +
                    "    'password':'<password>'\n" +
                    "    ['fromEmployee':'<other employee's email>'] << get only the tickets that come from a specific employee (omit for all employees)\n" +
                    "    ['status':'<pending/approved/denied>'] << get only the tickets with a specific status (omit for all statuses)\n" +
                    "    ['type':'<travel/lodging/food/other>'] << get only the tickets asking to reimburse a specific type of purchase (omit for all types)\n" +
                    "  }\n" +
                    "Note that multiple filters as described above can be used in conjunction\n" +
                    "  (example: get all tickets from JonDoe@example.com which were denied)"
                    );
        }
        //Manager's credentials
        String email = emailNode.asText();
        String password = passwordNode.asText();

        //Converting the other employee's email address into their ID number
        String fromEmployee = fromEmployeeNode == null? null : fromEmployeeNode.asText();
        int emplID = EMPLOYEE_REPOSITORY.getEmployeeId(fromEmployee);

        //The status of the ticket
        Ticket.StatusValues status;
        if (statusNode == null) status = null; else switch(statusNode.asText().toLowerCase()){
            case "approved": status = Ticket.StatusValues.APPROVED; break;
            case "denied": status = Ticket.StatusValues.DENIED; break;
            case "pending": status = Ticket.StatusValues.PENDING; break;
            default: status = null;
        };

        //The type of purchase that the ticket is asking to reimburse
        Ticket.ReimbursementType type; 
        if(typeNode == null) type =  null; else switch(typeNode.asText().toLowerCase()){
            case "food": type = Ticket.ReimbursementType.FOOD; break;
            case "lodging": type = Ticket.ReimbursementType.LODGING; break;
            case "travel": type = Ticket.ReimbursementType.TRAVEL; break;
            case "other": type = Ticket.ReimbursementType.OTHER; break;
            default: type = null;
        };

        //Verifying that the credentials given are correct and come from a manager
        Employee manager = EMPLOYEE_REPOSITORY.getEmployeeByEmail(email);
        if (manager == null) return new Controller.WebTuple(403, "Failed to verify credentials.");
        if (!Objects.equals(manager.getEmail(), email) || !Objects.equals(manager.getPassword(), password.hashCode()))
            return new Controller.WebTuple(403, "Failed to verify credentials.");
        if (manager.getRole() != Employee.Roles.MANAGER) return new Controller.WebTuple(403, "Only a manager can perform this action.");

        //By this line, we have successfully verified that the person attempting this action is a manager.
        //Create a list of all tickets based on the filters
        List<Ticket> tickets = TICKET_REPOSITORY.getTicketsFiltered(emplID, status, type);
        //Format all entries as JSON and return them
        return new Controller.WebTuple(200, makeJsonOf(tickets));
    }

    //This function is similar to getTicketsFiltered, except it only gets an employee's own tickets with their
    // credentials. This operation does NOT require manager permissions to execute because an employee is only
    // viewing their own tickets.
    public static Controller.WebTuple getMyTickets(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
            return new Controller.WebTuple(400, "Something went wrong. Did you send a malformed request?");
        }
        JsonNode emailNode = jsonNode.get("email");
        JsonNode passwordNode = jsonNode.get("password");
        JsonNode statusNode = jsonNode.get("status");
        JsonNode typeNode = jsonNode.get("type");
        if (emailNode == null || passwordNode == null) {
            return new Controller.WebTuple(400,
                    "Could not add employee; missing data in request.\n" +
                    "Correct format for request body:\n" +
                      "{\n" +
                        "'email':'<email address>',\n" +
                        "'password':'<password>'\n" +
                        "['status':'<pending/approved/denied>'] << get only the tickets with a specific status (omit for all statuses)\n" +
                        "['type':'<travel/lodging/food/other>'] << get only the tickets asking to reimburse a specific type of purchase (omit for all types)\n" +
                      "}\n" +
                    "Note that multiple filters as described above can be used in conjunction\n" +
                      "(example: get all tickets which are pending and ask to reimburse travel)\n"
                    );
        }
        //Employee's credentials
        String email = emailNode.asText();
        String password = passwordNode.asText();

        //The status of the ticket
        Ticket.StatusValues status;
        if (statusNode == null) status = null;
        else switch(statusNode.asText().toLowerCase()){
            case "approved": status = Ticket.StatusValues.APPROVED; break;
            case "denied": status = Ticket.StatusValues.DENIED; break;
            case "pending": status = Ticket.StatusValues.PENDING; break;
            default: status = null;
        };

        //The type of purchase that the ticket is asking to reimburse
        Ticket.ReimbursementType type;
        if(typeNode == null) {
            type = null;
        } else {
            switch(typeNode.asText().toLowerCase()){
                case "travel": type = Ticket.ReimbursementType.TRAVEL; break;
                case "lodging": type = Ticket.ReimbursementType.LODGING; break;
                case "food": type = Ticket.ReimbursementType.FOOD; break;
                case "other": type = Ticket.ReimbursementType.OTHER; break;
                default: type = null;
            };
        }

        //Verifying that the credentials given are correct
        Employee employee = EMPLOYEE_REPOSITORY.getEmployeeByEmail(email);
        if (employee == null) return new Controller.WebTuple(403, "Failed to verify credentials");
        if (!Objects.equals(employee.getEmail(), email) || !Objects.equals(employee.getPassword(), password.hashCode()))
            return new Controller.WebTuple(403,"Failed to verify credentials.");

        //Create a list of all tickets from this employee, based on the given filters
        List<Ticket> tickets = TICKET_REPOSITORY.getTicketsFiltered(employee.getId(), status, type);
        //Format all entries as JSON and return them
        return new Controller.WebTuple(200, makeJsonOf(tickets));
    }

    /**
     * Attempts to register a reimbursement request ticket, using an employee's credentials to do so. This process
     * requires several things; the employee's email/password, the type of reimbursement,
     * the amount that the ticket is requesting, and a description of the expense
     * @param json A JSON String containing all the relevant information
     * @return A String describing what happened when this function was run
     */
    public static Controller.WebTuple submitTicket(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
            return new Controller.WebTuple(400, "Something went wrong. Did you send a malformed request?");
        }
        JsonNode emailNode = jsonNode.get("email");
        JsonNode passwordNode = jsonNode.get("password");
        JsonNode reimbursementNode = jsonNode.get("reimbursementType");
        JsonNode amountNode = jsonNode.get("amount");
        JsonNode descriptionNode = jsonNode.get("description");
        if(emailNode == null || passwordNode == null || amountNode == null || descriptionNode == null) {
            return new Controller.WebTuple(400,
                        "Could not add employee; missing data in request.\n" +
                        "Correct format for request body:\n" +
                        "  {\n" +
                        "    'email':'<email address>',\n" +
                        "    'password':'<password>',\n" +
                        "    'reimbursementType':['TRAVEL/LODGING/FOOD/OTHER'], <-- optional, defaults to OTHER\n" +
                        "    'amount':'<amount in ticket request>',\n" +
                        "    'description':'<description of the purchase>'\n" +
                        "  }\n"
                        );
        }
        //Extracting all the information from the request into a format usable by TicketRepository
        String email = emailNode.asText();
        String password = passwordNode.asText();
        Ticket.ReimbursementType reimbursementType;
        if(reimbursementNode == null) {
            reimbursementType = Ticket.ReimbursementType.OTHER;
        } else {
            switch(reimbursementNode.asText()){
                case "TRAVEL": reimbursementType = Ticket.ReimbursementType.TRAVEL; break;
                case "LODGING": reimbursementType = Ticket.ReimbursementType.LODGING; break;
                case "FOOD": reimbursementType = Ticket.ReimbursementType.FOOD; break;
                default: reimbursementType = Ticket.ReimbursementType.OTHER;
            };
        }

        //Checking the credentials of the employee to ensure that they are valid.
        Employee employee = EMPLOYEE_REPOSITORY.getEmployeeByEmail(email);
        if(employee == null) return new Controller.WebTuple(403, "Failed to verify credentials.");
        if(!Objects.equals(employee.getPassword(), password.hashCode())) return new Controller.WebTuple(403, "Failed to verify credentials.");

        //Input validation
        BigDecimal amount;
        try{
            amount = new BigDecimal(amountNode.asText());
        } catch (NumberFormatException exception){
            return new Controller.WebTuple(400, "Could not parse the amount this ticket was for");
        }
        if(amount.compareTo(new BigDecimal(0)) <= 0){
            return new Controller.WebTuple(403, "You cannot request a reimbursement for that amount of money!");
        }

        String description = descriptionNode.asText();
        if(description.equals("")) return new Controller.WebTuple(403, "Your ticket description may not be empty.");

        //Finally, running the database query
        return TICKET_REPOSITORY.createNewTicket(employee.getId(), reimbursementType, amount, description);
    }
}
