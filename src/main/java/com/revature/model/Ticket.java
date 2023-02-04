package com.revature.model;

import java.math.BigDecimal;

public class Ticket {
    //Valid statuses of a ticket. When created, the ticket must be PENDING.
    //When the ticket is processed, it becomes either APPROVED or DENIED, and cannot be changed further.
    public enum StatusValues {PENDING, APPROVED, DENIED}

    //The type of purchase that this ticket is intended to reimburse.
    public enum ReimbursementType {TRAVEL, LODGING, FOOD, OTHER}

    //Information about the ticket.
    // These may be public because they are finalized when this class is instantiated.
    public final StatusValues status; //The status of this ticket. All tickets start out PENDING.
    public final ReimbursementType reimbursementType;
    public final BigDecimal amount; //The amount of money, in dollars, that this ticket is asking to reimburse
    public final String description; //A description of the purchase that is intended to be reimbursed.
    public final int employeeId; //The ID of the employee who created this ticket

    public Ticket(StatusValues status, ReimbursementType reimbursementType, BigDecimal amount, String description, int employeeId) {
        this.status = status;
        this.reimbursementType = reimbursementType;
        this.amount = amount;
        this.description = description;
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "status=" + status +
                ", reimbursementType=" + reimbursementType +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", employeeId=" + employeeId +
                '}';
    }
}
