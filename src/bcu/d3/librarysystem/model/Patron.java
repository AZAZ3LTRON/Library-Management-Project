package bcu.d3.librarysystem.model;

import bcu.d3.librarysystem.main.LibraryException;
import java.time.LocalDate;
import java.util.*;

public class Patron {
    
    private int id;
    private String name;
    private String phone;
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
    
    // Existed
    public void borrowBook(Book book, LocalDate dueDate) throws LibraryException {
        // TODO: implementation here
    }

    public void renewBook(Book book, LocalDate dueDate) throws LibraryException {
        // TODO: implementation here
    }

    public void returnBook(Book book) throws LibraryException {
        // TODO: implementation here
    }
    
    public void addBook(Book book) {
        // TODO: implementation here
    }
}
 