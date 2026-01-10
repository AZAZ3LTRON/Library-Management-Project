package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Patron;
import java.time.LocalDate;

public class BorrowBook implements Command {
    private final int patronId;
    private final int bookId;
    
    public BorrowBook(int patronId, int bookId) {
        this.patronId = patronId;
        this.bookId = bookId;
    }

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        // Get patron & book
        Patron patron = library.getPatronByID(patronId);
        Book book = library.getBookByID(bookId); // Fixed: should be bookId, not patronId

        // Calculate due date (current date + loan period)
        LocalDate dueDate = currentDate.plusDays(library.getLoanPeriod());

        // Check if patron exists
        if (patron == null) {
            throw new LibraryException("Patron with ID " + patronId + " not found.");
        }

        // Check if book exists
        if (book == null) {
            throw new LibraryException("Book with ID " + bookId + " not found.");
        }

        // Check if book is already borrowed
        if (book.isOnLoan()) {
            throw new LibraryException("Book \"" + book.getTitle() + "\" is already borrowed.");
        }

        // Check if patron has reached borrowing limit (assuming max 5 books)
        if (patron.getBooks().size() >= 5) {
            throw new LibraryException("Patron " + patron.getName() + 
                                    " has reached the maximum borrowing limit of 5 books.");
        }
        
        // 7.3 Update: Check if patron has reached borrowing limit
        int maxBooks = library.borrowingLimit();
        if (patron.getBooks().size() >= maxBooks) {
        	throw new LibraryException("Patron has reached their max borrowing limit");
        }
        
        // Check if patron has overdue books (optional restriction)
        if (patron.hasOverdueBooks(currentDate)) {
            throw new LibraryException("Patron " + patron.getName() + 
                                    " cannot borrow books because they have overdue books.");
        }

        // Borrow the book
        patron.borrowBook(book, dueDate);

        // Confirmation message
        System.out.println("\n✅ Book borrowed successfully!");
        System.out.println("Patron: " + patron.getName() + " (ID: " + patronId + ")");
        System.out.println("Book: " + book.getTitle() + " by " + book.getAuthor());
        System.out.println("Borrowed on: " + currentDate);
        System.out.println("Due date: " + dueDate);
        System.out.println();
    }
}