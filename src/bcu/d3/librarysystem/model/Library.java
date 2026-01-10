package bcu.d3.librarysystem.model;

import bcu.d3.librarysystem.main.LibraryException;
import java.util.*;
import java.util.stream.Collectors;

public class Library {
    
    private final int loanPeriod = 7;
    private final int borrowingLimit = 2; //7.3
    private final Map<Integer, Patron> patrons = new TreeMap<>();
    private final Map<Integer, Book> books = new TreeMap<>();

    public int getLoanPeriod() {
        return loanPeriod;
    }
    
    // Rubric 7.3
    public int borrowingLimit() {
    	return borrowingLimit;
    }
    
    // Borrowing Limit Checker (7.3)
    public boolean canPatronBorrowMore(int patronId) throws LibraryException{
    	Patron patron = getPatronByID(patronId);
    	if (patron.getBooks().size() < borrowingLimit) {
    		return false;
    	}
		return true;
    }
    
    //Borrowing limit hecker 2
    // Get how many more books a patron can borrow
    public int getRemainingBorrowingCapacity(int patronId) throws LibraryException {
        Patron patron = getPatronByID(patronId);
        return borrowingLimit - patron.getBooks().size();
    }
    // Book Methods
    public List<Book> getBooks() {
        List<Book> out = new ArrayList<>(books.values());
        return Collections.unmodifiableList(out);
    }

    // For 7.1 
    public List<Book> getDeletedBooks() {
        return books.values().stream()
            .filter(Book::isDeleted)
            .collect(Collectors.toList());
    }

    //Updatted for 7.1
    public Book getBookByID(int id) throws LibraryException {
        if (!books.containsKey(id)) {
            throw new LibraryException("There is no such book with that ID.");
        }
        Book book = books.get(id);
        if (book.isDeleted()){
            throw new LibraryException("Book has been deleted");
        }
        return book;
    }

    public List<Book> getAllBooks(){
    	List<Book> out = new ArrayList<>(books.values());
    	return Collections.unmodifiableList(out);
    }

    public void addBook(Book book) {
        if (books.containsKey(book.getId())) {
            throw new IllegalArgumentException("Duplicate book ID.");
        }
        books.put(book.getId(), book);
    }

    // For 7.1
    public void deleteBook(int bookId) throws LibraryException{
        Book book = getBookByID(bookId);

        if (book.isOnLoan()){
            throw new LibraryException("Book is on loan: Can't delete");
        }
        book.setDeleted(true);
    }

    // Patron methods
    
    // Added for 7.2
    public List<Patron> getDeletedPatrons() {
        return patrons.values().stream()
            .filter(Patron::isDeleted)
            .collect(Collectors.toList());
    }
    
    // Added for 7.2
    public List<Patron> getActivePatrons(){
    	return patrons.values().stream()
    			.filter(patron -> !patron.isDeleted() && (patron.getBooks().size()) < 5)
    			.collect(Collectors.toList());
    }
    
    // Updated for 7.2
    public Patron getPatronByID(int id) throws LibraryException {
        if (!patrons.containsKey(id)){
            throw new LibraryException("No such person possess the id");
        }
        Patron patron = patrons.get(id);
        if (patron.isDeleted()) {
        	throw new LibraryException("Patron has been deleted");
        }
        return patron;
    }

    public void addPatron(Patron patron) {
        if (!patrons.containsKey(patron.getId())){
            throw new IllegalArgumentException("A person already possess this id.");
        }
        patrons.put(patron.getId(), patron);
    }

    
    // Added for 7.2
    public void deletePatron(int patronId) throws LibraryException{
    	Patron patron = getPatronByID(patronId);
    	
    	if (!patron.getBooks().isEmpty()) {
    		throw new LibraryException("Cannot delete patron | Patron must return books on loan");
    	}
    	patron.setDeleted(true);
    }
    public List<Patron> getAllPatrons(){
        List<Patron> out = new ArrayList<>(patrons.values());
        return Collections.unmodifiableList(out);
    }
}
 