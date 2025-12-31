package bcu.d3.librarysystem.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {
    
    private Patron patron;
    private Book book;
    private LocalDate startDate;
    private LocalDate dueDate;
    private int renewalCount;

    public Loan(Patron patron, Book book, LocalDate dueDate) {
        this.patron = patron;
        this.book = book;
        this.startDate = LocalDate.now();
        this.dueDate = dueDate;
        this.renewalCount = 0;
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
    
    @Override
    public String toString(){
        return "Loan[Patron" + patron.getId() +
               " Book:- " +book.getTitle() + 
               " Due:- " + dueDate;
    }

    public boolean isActive() {
        return book != null && book.isOnLoan() && book.getLoan() == this;
    }
}
 