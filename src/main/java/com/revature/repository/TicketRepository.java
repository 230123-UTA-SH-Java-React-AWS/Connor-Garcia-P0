package com.revature.repository;

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
    public String finalizeTicketByID(int id, Ticket.StatusValues newStatus) {
        //Bad input handling
        if (newStatus == null)
            return "That is not a valid status for tickets.";
        if (newStatus == Ticket.StatusValues.PENDING)
            return "Tickets cannot be made pending after having been finalized!";

        //Checking the ticket in the database to see if it exists and can be updated
        Ticket ticketInDatabase = getTicketByID(id);
        if(ticketInDatabase == null) return "That ticket does not exist!";
        if(ticketInDatabase.getStatus() != Ticket.StatusValues.PENDING) return "That ticket has already been finalized!";

        //Query the database to update the ticket
        String sql = "UPDATE tickets SET tickstatus = ? WHERE tickid = ?";
        //Create the connection
        try (Connection con = ConnectionUtil.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, newStatus.toString());
            stmt.setInt(2, id);
            //Execute the query
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Something went wrong, likely on the database side.";
        }
        return "The ticket was successfully updated.";
    }

    public Ticket getTicketByID(int id) {
        List<Ticket> ticketList = new ArrayList<>();
        Ticket ticket = null;

        //Querying the database
        String sql = "SELECT * FROM tickets WHERE tickid = ?";
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

                Ticket tckt = new Ticket(status, type, new BigDecimal(rs.getString(5)), rs.getString(6), rs.getInt(2));
                ticketList.add(tckt);
            }
        } catch (SQLException | IllegalStateException e) {
            e.printStackTrace();
            return null;
        }

        if (ticketList.size() > 0) ticket = ticketList.get(0);
        return ticket;
    }


    //Attempt to add a ticket to the database; this will return a String indicating
    // what happened when this function was called
    public String createNewTicket(int employeeID, Ticket.ReimbursementType type, BigDecimal amount, String description) {
        String sql = "INSERT INTO TICKETS (tickemplid, tickstatus, ticktype, tickamount, tickdescription) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectionUtil.getConnection()) {
            PreparedStatement prst = con.prepareStatement(sql);

            prst.setInt(1, employeeID);
            prst.setString(2, Ticket.StatusValues.PENDING.toString());
            prst.setString(3, type.toString());
            prst.setString(4, amount.toString());
            prst.setString(5, description);

            prst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Ticket could not be submitted.";
        }
        return "Ticket was successfully submitted.";
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
        String sql = "SELECT * FROM tickets";
        if(filterEmplID != -1) {
            sql += " INTERSECT SELECT * FROM TICKETS WHERE tickemplid = '" + filterEmplID + "'";
        }
        if(filterStatus != null){
            sql += " INTERSECT SELECT * FROM TICKETS WHERE tickstatus = '" + filterStatus + "'";
        }
        if(filterType != null){
            sql += " INTERSECT SELECT * FROM TICKETS WHERE ticktype = '" + filterType + "'";
        }
        //Create the connection
        try (Connection con = ConnectionUtil.getConnection()) {
            //Create the querying object
            Statement stmt = con.createStatement();
            //Execute the query
            ResultSet rs = stmt.executeQuery(sql);
            //Mapping information from a table to our data structure
            while (rs.next()) {
                Ticket.ReimbursementType reimbursementType = switch (rs.getString(4)) {
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

                Ticket ticket = new Ticket(status,
                        reimbursementType,
                        new BigDecimal(rs.getString(5)),
                        rs.getString(6),
                        rs.getInt(2)
                );
                tickets.add(ticket);
            }
        } catch (SQLException | IllegalStateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return tickets;
    }
}
