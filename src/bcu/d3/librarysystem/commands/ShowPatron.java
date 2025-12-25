package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Patron;
import java.time.LocalDate;

// Show a patron’s details in the system – 
// should also display the borrowed books if implemented the borrow feature.
public class ShowPatron implements Command {
    
    private final int patronId; // The patron's Id
    
    public ShowPatron(int patronId){
        this.patronId = patronId;
    }

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException{
        Patron patron = library.getPatronByID(patronId);
        
        // Print out patron detail (make sure necessary methods are established)
        System.out.println("Patron Details \n");
        System.out.println("Patron ID:- " + patron.getId());
        System.out.println("Name:- " + patron.getName());
        System.out.println("Phone:- " + patron.getPhone());
        System.out.println("No of Borrowed books:- " + patron.getBooks().size());
        
        // Make sure books is not empty
        if (patron.getBooks().isEmpty()){
            System.out.println("No books currently borrowed.");
        } else {
            System.out.println("Borrowed books:- ");
            for (Book book : patron.getBooks()){
                System.out.println(" - " book.getDetailsShort());
            }
        }
    }
}
