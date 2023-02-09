package com.revature.model;

import java.math.BigDecimal;
public class Ticket {

    private StatusValues status; //Whether this ticket is pending, approved, or declined
    private ReimbursementType reimbursementType; //What category of item this ticket is asking to reimburse
    private BigDecimal amount; //The amount, in dollars, that this ticket is asking for
    private String description; //A description of the purchase
    private int employeeId; //The employee associated with this ticket.

    public StatusValues getStatus() {
        return status;
    }

    public ReimbursementType getReimbursementType() {
        return reimbursementType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    //Valid statuses of a ticket. When created, the ticket must be PENDING.
    //When the ticket is processed, it becomes either APPROVED or DENIED, and cannot be changed further.
    public enum StatusValues {PENDING, APPROVED, DENIED}

    //The type of purchase that this ticket is intended to reimburse.
    public enum ReimbursementType {TRAVEL, LODGING, FOOD, OTHER}

    //Required by Jackson
    public Ticket(){}

    public Ticket (int employeeId, BigDecimal amount, String description, ReimbursementType reimbursementType, StatusValues status){
        //Constraints on this data
        if (employeeId <= 0) {
            throw new IllegalStateException("Cannot set the employee ID of a ticket to a value less than 1");
        }
        if (amount.compareTo(new BigDecimal(0)) < 0) {
            throw new IllegalStateException("Cannot set the reimbursement amount of a ticket to a negative number");
        }
        if (description == null) {
            throw new IllegalStateException("The description of a ticket may not be null");
        }
        if (reimbursementType == null) {
            throw new IllegalStateException("The reimbursement type of a ticket may not be null (try Ticket.ReimbursementType.OTHER)");
        }
        if (status == null) {
            throw new IllegalStateException("The status of a ticket may not be null (Try Ticket.StatusValues.PENDING)");
        }

        //Initialization
        this.employeeId = employeeId;
        this.amount = amount;
        this.description = description;
        this.reimbursementType = reimbursementType;
        this.status = status;
    }
}
