package bcu.d3.librarysystem.model;

import bcu.d3.librarysystem.main.LibraryException;
import java.time.LocalDate;

public class Book {
    
    private int id;
    private String title;
    private String author;
    private String publicationYear;

    private Loan loan;

    public Book(int id, String title, String author, String publicationYear) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
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
        // TODO: implementation here
        return null;
    }

    public LocalDate getDueDate() {
        // TODO: implementation here
        return null;
    }
    
    public Loan getLoan() {
        return loan;
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
        // TODO: implementation here
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    /* Extras */
    public boolean isOnLoan() {
        return (loan != null);
    }

    public void returnToLibrary() {
        loan = null;
    }
}
