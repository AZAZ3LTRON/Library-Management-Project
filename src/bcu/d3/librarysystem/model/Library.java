package bcu.d3.librarysystem.model;

import bcu.d3.librarysystem.main.LibraryException;
import java.util.*;

public class Library {
    
    private final int loanPeriod = 7;
    private final Map<Integer, Patron> patrons = new TreeMap<>();
    private final Map<Integer, Book> books = new TreeMap<>();

    public int getLoanPeriod() {
        return loanPeriod;
    }

    public List<Book> getBooks() {
        List<Book> out = new ArrayList<>(books.values());
        return Collections.unmodifiableList(out);
    }

    public Book getBookByID(int id) throws LibraryException {
        if (!books.containsKey(id)) {
            throw new LibraryException("There is no such book with that ID.");
        }
        return books.get(id);
    }

    public Patron getPatronByID(int id) throws LibraryException {
        if (!patrons.containsKey(id)){
            throw new LibraryException("No such person possess the id");
        }
        return patrons.get(id);
    }

    public void addBook(Book book) {
        if (books.containsKey(book.getId())) {
            throw new IllegalArgumentException("Duplicate book ID.");
        }
        books.put(book.getId(), book);
    }

    public void addPatron(Patron patron) {
        if (!patrons.containsKey(patron.getId())){
            throw new IllegalArgumentException("A person already possess this id.");
        }
        patrons.put(patron.getId(), patron);
    }

    // Extras: Additional Functions
    public List<Patron> getAllPatrons(){
        List<Patron> out = new ArrayList<>(patrons.values());
        return Collections.unmodifiableList(out);
    }
}
 