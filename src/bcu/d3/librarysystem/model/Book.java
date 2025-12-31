package bcu.d3.librarysystem.model;

import bcu.d3.librarysystem.main.LibraryException;
import java.time.LocalDate;

public class Book {
    
    private int id;
    private String title;
    private String author;
    private String publicationYear;
    private final String publisher;

    private Loan loan;

    public Book(int id, String title, String author, String publicationYear, String publisher) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.publisher = publisher;
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
    
    public String getStatus() {
        if (isOnLoan()){
            if (loan != null && loan.isOverdue(LocalDate.now())){
                return "ON LOAN (OVERDUE)";
            }
            return "AVAILABLE";
        }
        return null;
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
    public boolean isOnLoan() {
        return (loan != null);
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

    public void borrow(Patron patron, LocalDate dueDate) throws LibraryException{
        if (isOnLoan()){
            throw new LibraryException("Book is already loaned out.");
        }
        this.loan = new Loan(patron, this, dueDate);
    }

    public void renew(LocalDate newDueDate) throws LibraryException {
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
