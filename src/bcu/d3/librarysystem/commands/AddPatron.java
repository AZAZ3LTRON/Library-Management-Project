package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Patron;
import java.time.LocalDate;
import java.util.*;

// Function adds Add new Patrons (members) to the system. 
// System should store at least the following information for each member: ID, Name, Phone Number and List of Books Borrowed.
public class AddPatron implements Command {

    // Constructor
    private final int id;
    private final String name;
    private final String phone;
    private final List<Book> booksBorrowed;

    public AddPatron(int id, String name, String phone, List<Book> booksBorrowed){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.booksBorrowed = new ArrayList<>();
    }

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        // Remember it is a TreeMap
        Patron patron = new Patron(id, name, phone, booksBorrowed);
        library.addPatron(patron);
    }
}