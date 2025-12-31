package bcu.d3.librarysystem.model;

import bcu.d3.librarysystem.main.LibraryException;
import java.time.LocalDate;
import java.util.*;

public class Patron {
    
    private final int id;
    private final String name;
    private final String phone;
    private final List<Book> books;
    
    // TODO: implement constructor here
    public Patron(int id, String name, String phone, List<Book> books) {
    	this.id = id;
    	this.name = name;
    	this.phone = phone;
    	this.books = new ArrayList<>();
    }
    
    // Getters (added by user)
    public int getId() {
    	return id;
    }
    
    public String getName() {
    	return name;
    }
    
    public String getPhone() {
    	return phone;
    }
    
    public List<Book> getBooks(){
    	return new ArrayList<>(books);
    }

    // Extra: Additional Functions
    public int getBookCount(){
        return books.size();
    }
    
    public boolean hasBook(Book book){
        return books.contains(book);
    }

    public boolean hasOverdueBooks(LocalDate currentDate) {
        for (Book book : books) {
            if (book.isOverdue(currentDate)) {
                return true;
            }
        }
        return false;
    }

    public List<Book> getOverdueBooks(LocalDate currentDate) {
        List<Book> overdueBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.isOverdue(currentDate)) {
                overdueBooks.add(book);
            }
        }
        return overdueBooks;
    }    
    
    @Override
    public String toString() {
        return "Patron ID: " + id + ", Name: " + name + ", Phone: " + phone + ", Books Borrowed: " + books.size();
    }

    // Existed

    public void borrowBook(Book book, LocalDate dueDate) throws LibraryException{
        if (books.size() >= 5) {
            throw new LibraryException("Patron " + name + " has reached the maximum borrowing limit of 5 books.");
        }
        
        // Check if patron already has this book
        if (books.contains(book)) {
            throw new LibraryException("Patron already has book");
        }
        
        // Check if book is already borrowed
        if (book.isOnLoan()) {
            throw new LibraryException("Book " + book.getTitle() + " is already borrowed.");
        }
        
        // Borrow the book
        book.borrow(this, dueDate);
        books.add(book);
    }

    public void renewBook(Book book, LocalDate newDueDate) throws LibraryException {
        // Check if patron has this book
        if (!books.contains(book)) {
            throw new LibraryException("Patron " + name + " cannot renew a book they haven't borrowed: " + book.getTitle());
        }
        
        // Check if book is overdue (optional restriction)
        if (book.isOverdue(LocalDate.now())) {
            throw new LibraryException("Cannot renew overdue book: " + book.getTitle());
        }
        
        // Check if renewal limit reached (assuming max 2 renewals)
        if (book.getRenewalCount() >= 2) {
            throw new LibraryException("Maximum renewals reached for book: " + book.getTitle());
        }
        
        // Renew the book
        book.renew(newDueDate);
    }

    public void returnBook(Book book) throws LibraryException {
        // Check if patron has this book
        if (!books.contains(book)) {
            throw new LibraryException("Patron " + name + " cannot return a book they haven't borrowed: " + book.getTitle());
        }
        
        // Return the book
        book.returnBook();
        books.remove(book);
    }
    
    public void addBook(Book book) {
        // Directly add a book without checks (used when loading data)
        if (!books.contains(book)) {
            books.add(book);
        }
    }
    
    // Extras 2
    public void removeBook(Book book) {
        books.remove(book);
    }
    
    public void clearBooks() {
        books.clear();
    }
}