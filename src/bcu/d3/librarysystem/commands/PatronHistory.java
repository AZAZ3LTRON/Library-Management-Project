package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Loan;
import bcu.d3.librarysystem.model.Patron;
import java.time.*;
import java.util.*;

public class PatronHistory implements Command {
    private final int patronId;

    public PatronHistory(int patronId){
        this.patronId = patronId;
    }

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException{
        Patron patron = library.getPatronByID(patronId);
        List<Loan> history = library.getPatronLoanHistory(patronId);

        System.out.println("Patron ID:- " + patronId);
        System.out.println("Name: " + patron.getName());
        System.out.println("Total Loans: " + history.size());
        
        if (history.isEmpty()) {
            System.out.println("No loan history found for this patron.");
            return;
        }
    }
}
