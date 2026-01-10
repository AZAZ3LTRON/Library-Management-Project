package bcu.d3.librarysystem.model;

import bcu.d3.librarysystem.main.LibraryException;
import java.time.LocalDate;
import java.util.*;

public class Patron {
    
    private final int id;
    private final String name;
    private final String phone;
    private final String email; // 5.2
    private List<Book> books;
    private boolean userdeleted; // For 7.2
    private final int borrowingLimit;
    
    // For New Patrons
    public Patron(int id, String name, String phone, String email){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.books = new ArrayList<>();
        this.email = email; // 5.2
        this.borrowingLimit = 2;
    }

    // For existing patrons
    public Patron(int id, String name, String phone, String email, List<Book> books) {
    	this.id = id;
    	this.name = name;
    	this.phone = phone;
        this.email = email; // For 5.2
    	this.books = new ArrayList<>(books);
    	this.userdeleted = false; // For 7.2
    	this.borrowingLimit = 2; // For 7.3
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
    

    // For 5.2
    public String getEmail(){
        return email;
    }

    public List<Book> getBooks(){
    	return new ArrayList<>(books);
    }

    // For 7.2
    public boolean isDeleted() {
    	return userdeleted;
    }
    
    // For 7.2
    public void setDeleted(boolean userdeleted) throws LibraryException {
        if (userdeleted && !books.isEmpty()){
            throw new LibraryException("Patron hasn't returned books| Can't delete patron ");
        }
    	this.userdeleted = userdeleted;
    }

    // For 7.2
    public boolean isActive(){
        return !userdeleted;
    }
    
    // For 7.3
    public int getBorrowingLimit() {
    	return borrowingLimit;
    }

    public int getRemainingBorrowingCapacity() {
        return borrowingLimit - books.size();
    }
    
    public boolean canBorrowMore() {
        return books.size() < borrowingLimit;
    }
    
    public boolean hasReachedBorrowingLimit() {
        return books.size() >= borrowingLimit;
    }
    
    // Extra: Additional Functions
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
        // Changed for 7.2
    	if (userdeleted) {
    		throw new LibraryException("Patron Deleted: Can't borrow book");
    	}
    	
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
        // Updated for 7.2 	
    	if (userdeleted) {
        	throw new LibraryException("Patron Deleted: Can't borrow book");
        }
    	
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
}