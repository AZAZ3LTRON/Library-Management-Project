package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Patron;
import java.time.LocalDate;

// Class that renews a book a patron has borrowed
public class RenewBook implements Command {
    private final int patronId;
    private final int bookId;
    
    public RenewBook(int patronId, int bookId) {
        this.patronId = patronId;
        this.bookId = bookId;
    }

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException{
        Patron patron = library.getPatronByID(patronId);
        Book book = library.getBookByID(bookId);

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

        // Calculate new due date
        LocalDate currentDueDate = book.getDueDate();
        if (currentDueDate == null){
            currentDueDate = currentDate.plusDays(library.getLoanPeriod());
        }

        LocalDate newDueDate = currentDueDate.plusDays(library.getLoanPeriod());

        // Renew the book
        patron.renewBook(book, newDueDate);
        
        System.out.println("\nBook renewed successfully!");
        System.out.println("Book: " + book.getTitle());
        System.out.println("New due date: " + book.getDueDate());
        System.out.println();
    }
}
