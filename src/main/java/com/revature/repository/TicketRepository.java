package com.revature.repository;

import com.revature.controller.Controller;
import com.revature.model.Ticket;
import com.revature.utils.ConnectionUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is how the [TicketService] class interacts with the database.
 */
public class TicketRepository {
    //Attempts to finalize a ticket, setting its status to what is specified in the parameter.
    //You cannot un-finalize a ticket by changing its status to PENDING, or by changing a ticket which is not PENDING.
    //Returns a WebTuple containing a description of what happened when this function was run and a
    // relevant HTTP status code.
    public Controller.WebTuple finalizeTicketByID(int id, Ticket.StatusValues newStatus) {
        //Bad input handling
        if (newStatus == null)
            return new Controller.WebTuple(400, "That is not a valid status for tickets.");
        if (newStatus == Ticket.StatusValues.PENDING)
            return new Controller.WebTuple(403, "Tickets cannot be made pending after having been finalized!");

        //Checking the ticket in the database to see if it exists and can be updated
        Ticket ticketInDatabase = getTicketByID(id);
        if(ticketInDatabase == null) return new Controller.WebTuple(404, "That ticket does not exist!");
        if(ticketInDatabase.getStatus() != Ticket.StatusValues.PENDING) return new Controller.WebTuple(403, "That ticket has already been finalized!");

        //Query the database to update the ticket
        String sql = "UPDATE tickets SET tickstatus = ? WHERE tickid = ?";
        //Create the connection
        try (Connection con = ConnectionUtil.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, newStatus.ordinal() + 1); //Hard to maintain, but values in the status enum map to the ones in the database like this.
            stmt.setInt(2, id);
            //Execute the query
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return new Controller.WebTuple(500, "Something went wrong.");
        }
        return new Controller.WebTuple(200, "The ticket was successfully updated.");
    }

    public Ticket getTicketByID(int id) {
        List<Ticket> ticketList = new ArrayList<>();
        Ticket result = null;

        //Querying the database
        String sql = "SELECT tickid, tickemplid, ticsname, tictname, tickamount, tickdescription FROM tickets " +
                "INNER JOIN statuses on tickstatus = ticsid " +
                "INNER JOIN tickettypes on ticktype = tictid " +
                "WHERE tickid = ?";
        //Create the connection
        try (Connection con = ConnectionUtil.getConnection()) {
            //Create the querying object
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            //Execute the query
            ResultSet rs = stmt.executeQuery();
            //Mapping information from a table to our data structure
            while (rs.next()) {
                Ticket.StatusValues status = switch(rs.getString(3)){
                    case "PENDING" -> Ticket.StatusValues.PENDING;
                    case "APPROVED" -> Ticket.StatusValues.APPROVED;
                    case "DENIED" -> Ticket.StatusValues.DENIED;
                    default -> throw new IllegalStateException("BAD DATA IN TICKET TABLE");
                };
                Ticket.ReimbursementType type = switch(rs.getString(4)){
                    case "FOOD" -> Ticket.ReimbursementType.FOOD;
                    case "LODGING" -> Ticket.ReimbursementType.LODGING;
                    case "TRAVEL" -> Ticket.ReimbursementType.TRAVEL;
                    default -> Ticket.ReimbursementType.OTHER;
                };

                Ticket ticket = new Ticket(
                        rs.getInt(2),
                        rs.getBigDecimal(5),
                        rs.getString(6),
                        type,
                        status);
                ticketList.add(ticket);
            }
        } catch (SQLException | IllegalStateException e) {
            e.printStackTrace();
            return null;
        }

        if (ticketList.size() > 0) result = ticketList.get(0);
        return result;
    }


    //Attempt to add a ticket to the database; this will return a String indicating
    // what happened when this function was called
    public Controller.WebTuple createNewTicket(int employeeID, Ticket.ReimbursementType type, BigDecimal amount, String description) {
        String sql = "INSERT INTO TICKETS (tickemplid, ticktype, tickamount, tickdescription) VALUES (?, ?, ?, ?)";
        try (Connection con = ConnectionUtil.getConnection()) {
            PreparedStatement prst = con.prepareStatement(sql);

            prst.setInt(1, employeeID);
            prst.setInt(2, type.ordinal() + 1);
            prst.setBigDecimal(3, amount);
            prst.setString(4, description);

            prst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return new Controller.WebTuple(500, "Ticket could not be submitted.");
        }
        return new Controller.WebTuple(200,"Ticket was successfully submitted.");
    }

    /**
     * This function collects every ticket from the database and filters them according to the passed-in parameters.
     * The parameters may be empty (see parameter entries), in which case this will simply be all tickets.
     *
     * @param filterEmplID The ID of an employee in the database (-1 causes this filter to be ignored)
     * @param filterStatus A filter on the status of tickets (null causes this filter to be ignored)
     * @param filterType   A filter on the category of tickets (null causes this filter to be ignored)
     * @return An ArrayList containing every ticket in the database that match the provided filters.
     */
    public ArrayList<Ticket> getTicketsFiltered(int filterEmplID, Ticket.StatusValues filterStatus, Ticket.ReimbursementType filterType) {
        ArrayList<Ticket> tickets = new ArrayList<>();

        //Querying the database
        String sql = "SELECT tickid, tickemplid, ticsname, tictname, tickamount, tickdescription FROM tickets " +
                "INNER JOIN statuses on tickstatus = ticsid " +
                "INNER JOIN tickettypes on ticktype = tictid " +
                "WHERE true";
        if(filterEmplID != -1) {
            sql += " AND tickemplid = '" + filterEmplID + "'";
        }
        if(filterStatus != null){
            sql += " AND ticsname = '" + filterStatus + "'";
        }
        if(filterType != null){
            sql += " AND tictname = '" + filterType + "'";
        }
        //Create the connection
        try (Connection con = ConnectionUtil.getConnection()) {
            //Create the querying object
            Statement stmt = con.createStatement();
            //Execute the query
            ResultSet rs = stmt.executeQuery(sql);
            //Mapping information from a table to our data structure
            while (rs.next()) {
                Ticket.ReimbursementType type = switch (rs.getString(4)) {
                    case "TRAVEL" -> Ticket.ReimbursementType.TRAVEL;
                    case "LODGING" -> Ticket.ReimbursementType.LODGING;
                    case "FOOD" -> Ticket.ReimbursementType.FOOD;
                    default -> Ticket.ReimbursementType.OTHER; //More lenient here
                };
                Ticket.StatusValues status = switch (rs.getString(3)) {
                    case "APPROVED" -> Ticket.StatusValues.APPROVED;
                    case "DENIED" -> Ticket.StatusValues.DENIED;
                    case "PENDING" -> Ticket.StatusValues.PENDING;
                    default ->
                            throw new IllegalStateException("BAD DATA IN TICKET TABLE");
                };

                Ticket ticket = new Ticket(
                        rs.getInt(2),
                        rs.getBigDecimal(5),
                        rs.getString(6),
                        type,
                        status);
                tickets.add(ticket);
            }
        } catch (SQLException | IllegalStateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return tickets;
    }
}
