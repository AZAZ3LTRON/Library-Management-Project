package bcu.d3.librarysystem.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Loan {
    
    private Patron patron;
    private Book book;
    private LocalDate startDate;
    private LocalDate dueDate;
    private int renewalCount;
    private boolean returned; // 8.1
    private LocalDate returnDate; //8.1 Track when book are returned

    // Constructor for new loans
    public Loan(Patron patron, Book book, LocalDate dueDate) {
        this.patron = patron;
        this.book = book;
        this.startDate = LocalDate.now();
        this.dueDate = dueDate;
        this.renewalCount = 0;
        this.returned = false;
        this.returnDate = null;
    }
    
    // Constructor for loading existing loans (with all parameters) (constructor overloading)
    public Loan(Patron patron, Book book, LocalDate startDate, LocalDate dueDate, int renewalCount) {
        this.patron = patron;
        this.book = book;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.renewalCount = renewalCount;
        this.returnDate = null;
    }

    /*Getters */

    public Patron getPatron(){
        return patron;
    }

    public Book getBook(){
        return book;
    }

    public LocalDate getStartDate(){
        return startDate;
    }

    public LocalDate getDueDate(){
        return dueDate;
    }

    public int getRenewalCount(){
        return renewalCount;
    }

    //8.1 
    public LocalDate getReturnDate(){
        return returnDate;
    }


    // 8.1
    public boolean isReturned(){
        return returned;
    }

    /*Setters */
    public void setPatron(Patron patron){
        this.patron = patron;
    }

    public void setBook(Book book){
        this.book = book;
    }

    public void setStartDate(LocalDate startDate){
        this.startDate = startDate;
    }

    public void setDueDate(LocalDate dueDate){
        this.dueDate = dueDate;
    }



    // 8.1 
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.returned = (returnDate != null);
    }
    
    // 8.1
    public void setReturned(boolean returned){
        this.returned = returned;
        if (returned && returnDate == null){
            this.returnDate = LocalDate.now();
        }
    }

    // 8.1
    public void setRenewalCount(int renewalCount){
        this.renewalCount = renewalCount;
    }

    /* Extras: Additional Method */
    public boolean isOverdue(LocalDate currentDate){
        return currentDate.isAfter(dueDate);
    }

    public long daysOverdue(LocalDate currentDate) {
        if (!isOverdue(currentDate)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, currentDate);
    }
    
    public long daysRemaining(LocalDate currentDate) {
        if (isOverdue(currentDate)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(currentDate, dueDate);
    }
    
    public void renew(LocalDate newDueDate) {
        this.dueDate = newDueDate;
        this.renewalCount++;
    }
    
    public boolean canRenew(int maxRenewals) {
        return renewalCount < maxRenewals;
    }
    
    public String getStatus(LocalDate currentDate) {
        if (isOverdue(currentDate)) {
            return "OVERDUE (by " + daysOverdue(currentDate) + " days)";
        } else {
            return "DUE in " + daysRemaining(currentDate) + " days";
        }
    }
    
    public String getHistoryEntry() {
        StringBuilder entry = new StringBuilder();
        entry.append("Book: ").append(book.getTitle()).append("\n");
        entry.append("Borrowed: ").append(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n");
        entry.append("Due: ").append(dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n");
        entry.append("Renewals: ").append(renewalCount).append("\n");
        
        if (returned && returnDate != null) {
            entry.append("Returned: ").append(returnDate.format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n");
            long loanDuration = ChronoUnit.DAYS.between(startDate, returnDate);
            entry.append("Loan Duration: ").append(loanDuration).append(" days\n");
        } else {
            entry.append("Status: ACTIVE LOAN\n");
        }
        
        return entry.toString();
    }

    @Override
    public String toString(){
        if (returned) {
            return "Completed Loan[Patron: " + patron.getName() +
                   ", Book: " + book.getTitle() + 
                   ", Borrowed: " + startDate +
                   ", Returned: " + returnDate + "]";
        } else {
            return "Active Loan[Patron: " + patron.getName() +
                   ", Book: " + book.getTitle() + 
                   ", Due: " + dueDate + "]";
        }
    }

    public boolean isActive() {
        return book != null && book.isOnLoan() && book.getLoan() == this;
    }
}
 