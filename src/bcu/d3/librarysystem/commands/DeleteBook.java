package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Book;
import java.time.LocalDate;

public class DeleteBook implements Command {
    
    private final int bookId;
    
    public DeleteBook(int bookId) {
        this.bookId = bookId;
    }
    
    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        // Find the book
        Book book = library.getBookByID(bookId);
        
        if (book == null) {
            throw new LibraryException("Book not found.");
        }
        // Check if book is already deleted
        if (book.isDeleted()) {
            throw new LibraryException("Book doesn't exist or is already deleted.");
        }
        // Check if book is on loan
        if (book.isOnLoan()) {
            throw new LibraryException("Cannot delete book because it is currently on loan.");
        }
        // Soft delete the book
        book.setDeleted(true);
        
        System.out.println("Book "+ book.getTitle() + "has been deleted.");
    }
}