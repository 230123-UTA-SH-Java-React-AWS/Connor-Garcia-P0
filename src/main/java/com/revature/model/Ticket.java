package com.revature.model;

import java.math.BigDecimal;

public class Ticket {
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

    //Information about the ticket.
    // These may be public because they are finalized when this class is instantiated.
    private final StatusValues status; //The status of this ticket. All tickets start out PENDING.
    private final ReimbursementType reimbursementType;
    private final BigDecimal amount; //The amount of money, in dollars, that this ticket is asking to reimburse
    private final String description; //A description of the purchase that is intended to be reimbursed.
    private final int employeeId; //The ID of the employee who created this ticket

    public Ticket(StatusValues status, ReimbursementType reimbursementType, BigDecimal amount, String description, int employeeId) {
        //Constraints on this data
        if (employeeId <= 0) {
            throw new IllegalStateException("Cannot set the employee ID of a ticket to a value less than 1");
        }
        if (amount.compareTo(new BigDecimal("0")) < 0) {
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
        this.status = status;
        this.reimbursementType = reimbursementType;
        this.amount = amount;
        this.description = description;
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "Ticket{" + "status=" + getStatus() + ", reimbursementType=" + getReimbursementType() + ", amount=" + getAmount() + ", description='" + getDescription() + '\'' + ", employeeId=" + getEmployeeId() + '}';
    }
}
