package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Patron;
import java.time.LocalDate;

public class DeletePatron implements Command {
    
    private final int patronId;
    
    public DeletePatron(int patronId) {
        this.patronId = patronId;
    }
    
    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        // Find the patron
        Patron patron = library.getPatronByID(patronId);
        
        if (patron == null) {
            throw new LibraryException("Patron with ID " + patronId + " not found.");
        }
        
        // Check if patron has books on loan
        if (!patron.getBooks().isEmpty()) {
            throw new LibraryException("Cannot delete patron because they have book(s) on loan.");
        }
        
        // Check if patron is already deleted
        if (patron.isDeleted()) {
            throw new LibraryException("Patron is already deleted.");
        }
        
        // Soft delete the patron
        patron.setDeleted(true);
        
        System.out.println("Patron '" + patron.getName() + "' has been soft deleted.");
    }
}