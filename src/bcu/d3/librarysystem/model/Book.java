package bcu.d3.librarysystem.model;

import bcu.d3.librarysystem.main.LibraryException;
import java.time.LocalDate;

public class Book {
    
    private int id;
    private String title;
    private String author;
    private String publicationYear;
    private String publisher;
    private boolean deleted;
    private Loan loan;

    // Constrcutor
    public Book(int id, String title, String author, String publicationYear, String publisher) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.publisher = publisher;
        this.deleted = false;
    }

    /* Getters */
    public int getId() {
        return id;
    } 

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }   

    public String getPublicationYear() {
        return publicationYear;
    }

    public String getPublisher(){
        return publisher;
    }
    public String getDetailsShort() {
        return "Book:- " + id + " - " + title;
    }

    public String getDetailsLong() {
        return "Book:- " + id + "\n"
            + "Title:- " + title + "\n"
            + "Author:- " + author + "\n"
            + "Year:- " + publicationYear + "\n"
            + "Status:- " + getStatus();
    }
    
    // Updated for 7.1
    public String getStatus() {
        if (deleted) {
            return "DELETED";
        } else if (isOnLoan()) {
            if (loan != null && loan.isOverdue(LocalDate.now())) {
                return "ON LOAN (OVERDUE)";
            } else {
                return "ON LOAN";
            }
        } else {
            return "AVAILABLE";
        }
    }

    public LocalDate getDueDate() {
        if (loan != null){
            return loan.getDueDate();
        }
        return null;
    }
    
    public Loan getLoan() {
        return loan;
    }

    public int getRenewalCount(){
        if (loan != null) {
            return loan.getRenewalCount();
        }
        return 0;
    }

    // For 7.1
    public boolean isDeleted(){
        return deleted;
    }

    public void setDeleted(boolean deleted){
        this.deleted = deleted;
    }

    // Check if book can be deleted
    public boolean canDelete(){
        if (!deleted && !isOnLoan()){
            return true; 
        }
        return false;
    }

    /* Setters */
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }
  
    public void setPublisher(String publisher){
        this.publisher = publisher;
    }

    public void setDueDate(LocalDate dueDate) throws LibraryException {
        if (loan == null){
            throw new LibraryException("Book is not on loan");
        }
        loan.setDueDate(dueDate);
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    /* Extras */

    // Updated for 7.1
    public boolean isOnLoan() {
        return (loan != null && deleted == false);
    }

    public void returnToLibrary() {
       this.loan = null;
    }

    /* Extras 2 */
    public boolean isOverdue(LocalDate currentDate){
        return loan != null && loan.isOverdue(currentDate);
    }


    public boolean canRenew(){
        return loan != null && loan.getRenewalCount() < 2;
    }

    public Patron getBorrower(){
        if (loan != null){
            return loan.getPatron();
        }
        return null;
    }


    // Updated for 7.1 
    public void borrow(Patron patron, LocalDate dueDate) throws LibraryException{
        if (deleted){
            throw new LibraryException("Book doesn't exist");
        }
        if (isOnLoan()){
            throw new LibraryException("Book is already loaned out.");
        }
        this.loan = new Loan(patron, this, dueDate);
    }

    public void renew(LocalDate newDueDate) throws LibraryException {
        if (deleted){
            throw new LibraryException("Book doesn't exist");
        }
        if (!isOnLoan()) {
            throw new LibraryException("Cannot renew a book that is not on loan.");
        }
        if (!canRenew()) {
            throw new LibraryException("Maximum renewals reached for this book.");
        }
        loan.renew(newDueDate);
    }

    public void returnBook() throws LibraryException {
        if (!isOnLoan()) {
            throw new LibraryException("Cannot return a book that is not on loan.");
        }
        this.loan = null;
    }

    public long daysOverdue(LocalDate currentDate) {
        if (loan != null && loan.isOverdue(currentDate)) {
            return loan.daysOverdue(currentDate);
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "Book[id=" + id + ", title=\"" + title + "\", author=\"" + author + "\"]";
    }

}
