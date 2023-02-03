package com.revature.model;

import java.math.BigDecimal;

public class Ticket {
    //Valid statuses of a ticket. When created, the ticket must be PENDING.
    //When the ticket is processed, it becomes either APPROVED or DENIED, and cannot be changed further.
    public enum StatusValues {
        PENDING("PENDING"),
        APPROVED("APPROVED"),
        DENIED("DENIED");

        StatusValues(String status) { }
    }

    private StatusValues status; //The status of this ticket. All tickets start out PENDING.
    public final String amount; //The amount of money, in dollars, that this ticket is asking to reimburse
    public final String description; //A description of the purchase that is intended to be reimbursed.
    public final int employeeId; //The name of the employee which created this ticket

    public Ticket(String amount, String description, int employeeId) {
        this.amount = amount;
        this.description = description;
        this.employeeId = employeeId;
        this.status = StatusValues.PENDING;
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
