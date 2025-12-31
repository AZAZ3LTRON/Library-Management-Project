package bcu.d3.librarysystem.commands;

import java.time.LocalDate;
import java.util.List;
import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Patron;

public class ListPatrons implements Command {
    
    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        // Get all patrons from the library (assuming TreeMap returns values sorted by key)
        List<Patron> patrons = library.getAllPatrons();
        
        if (patrons.isEmpty()) {
            System.out.println("\nNo patron's found.\n");
            return;
        }
        

        System.out.println("Total No of Patrons: " + patrons.size());
        System.out.println();

        for (Patron patron : patrons) {
            System.out.println("\n----------------------------------");
            System.out.println("ID: " + patron.getId());
            System.out.println("Name: " + patron.getName());
            System.out.println("Phone: " + patron.getPhone());
            System.out.println("Books borrowed: " + patron.getBooks().size());
            System.out.println("\n----------------------------------");
            
            // Check for Overdue books
            boolean hasOverdue = false;
            for (var book : patron.getBooks()) {
                if (book.isOnLoan() && book.getLoan() != null && 
                    book.getLoan().getDueDate().isBefore(currentDate)) {
                    hasOverdue = true;
                    break;
                }
            }
            
            if (hasOverdue) {
                System.out.println("STATUS: HAS OVERDUE BOOKS!");
            }
            System.out.println("----------------------------------");
        }
    }
}