package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.main.LibraryException;
import java.time.LocalDate;

public class ShowBook implements Command {
    
    private final int bookId;
    
    public ShowBook(int bookId) {
        this.bookId = bookId;
    }
    
    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        Book book = library.getBookByID(bookId);
        
        if (book.isDeleted()) {
            System.out.println("\n This book has been deleted.");
            return;
        }
        
        
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("ID:           " + book.getId());
        System.out.println("Title:        " + book.getTitle());
        System.out.println("Author:       " + book.getAuthor());
        System.out.println("Year:         " + book.getPublicationYear());
        System.out.println("Publisher:    " + book.getPublisher());
        System.out.println("Status:       " + book.getStatus());
        
        if (book.isOnLoan()) {
            System.out.println("\n📅 LOAN INFORMATION:");
            System.out.println("  Due Date:     " + book.getDueDate());
            System.out.println("  Days Overdue: " + book.daysOverdue(currentDate));
            System.out.println("  Renewals:     " + book.getRenewalCount() + "/2");
            
            if (book.getBorrower() != null) {
                System.out.println("  Borrowed by:  " + book.getBorrower().getName() + 
                                 " (ID: " + book.getBorrower().getId() + ")");
            }
        }
        
        System.out.println("══════════════════════════════════════════\n");
    }
}