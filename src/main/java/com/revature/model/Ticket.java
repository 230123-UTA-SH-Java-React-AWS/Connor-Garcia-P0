package com.revature.model;

import java.math.BigDecimal;

public class Ticket {
    //Valid statuses of a ticket. When created, the ticket must be PENDING.
    //When the ticket is processed, it becomes either APPROVED or DENIED, and cannot be changed further.
    public enum StatusValues {PENDING, APPROVED, DENIED}

    private StatusValues status = StatusValues.PENDING; //The status of this ticket. All tickets start out PENDING.
    private String amount = "0.0"; //The amount of money, in dollars, that this ticket is asking to reimburse
    private String description = "(no description)"; //A description of the purchase that is intended to be reimbursed.

    //Ensures that the Jackson library is able to create instances of this class correctly
    public Ticket() {
        super();
    }

    public Ticket(String amount, String description){
        this.amount = amount;
        this.description = description;
    }

    //Returns a BigDecimal so that comparisons and arithmetic can be done with the amount
    public BigDecimal getAmount() {
        return new BigDecimal(amount);
    }

    public String getDescription(){
        return description;
    }

    public StatusValues getStatus() {
        return status;
    }

    //Only a ticket which is PENDING can have its status changed; a ticket which has been
    // approved or denied is considered finalized.
    public void setStatus(StatusValues status) {
        if(this.status != StatusValues.PENDING) return; //Cannot update a ticket which has been finalized
        this.status = status;
    }
}
