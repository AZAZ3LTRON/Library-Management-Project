package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Patron;
import java.time.LocalDate;

public class ReturnBook implements Command {
    private final int patronId;
    private final int bookId;
    
    public ReturnBook(int patronId, int bookId) {
        this.patronId = patronId;
        this.bookId = bookId;
    }

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        Patron patron = library.getPatronByID(patronId);
        Book book = library.getBookByID(bookId);
        
        // Validate each 

        // Check if patron exist
        if (patron == null){
             throw new LibraryException("Patron not found.");
        }

        // Check if book exist
        if (book == null){
            throw new LibraryException("Book not found.");
        }

        // Check if the patron has the specified book
        if (!patron.hasBook(book)){
            throw new LibraryException("Patron doesn't have this book.");
        }
        
        // Return the book
        patron.returnBook(book);
        
        System.out.println("\nBook returned successfully!");
        System.out.println("Patron: " + patron.getName());
        System.out.println("Book: " + book.getTitle());
        System.out.println();
    }
}