package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Patron;
import java.time.LocalDate;
import java.util.List;

public class ListPatrons implements Command {
    
    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        // Get all patrons from the library (assuming TreeMap returns values sorted by key)
        List<Patron> patronsList = library.getAllPatrons();
        
        if (patronsList.isEmpty()) {
            System.out.println("\nNo patron's found.\n");
            return;
        }
        
        System.out.println("Total No of Patrons: " + patronsList.size());
        System.out.println();

        for (Patron patron : patronsList) {
            System.out.println("\n----------------------------------");
            System.out.println("Patron ID: " + patron.getId());
            System.out.println("Patron Name: " + patron.getName());
            System.out.println("Patron Phone: " + patron.getPhone());
            System.out.println("Patron Email: " + patron.getEmail());
            System.out.println("Books borrowed: " + patron.getBooks().size());
            System.out.println("\n----------------------------------");
            
            // Check for Overdue books
            boolean hasOverdue = false;
            int overdueCount = 0; // Count how many books a patron has overdue
            for (Book book : patron.getBooks()) {
                if (book.isOnLoan() && book.getLoan() != null && 
                    book.getLoan().getDueDate().isBefore(currentDate)) {
                    hasOverdue = true;
                    overdueCount++;
                }
            }
            
            if (hasOverdue) {
                System.out.println("STATUS: HAS OVERDUE BOOKS!");
            }
            System.out.println("----------------------------------");
        }
    }
}