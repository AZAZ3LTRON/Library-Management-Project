package bcu.d3.librarysystem.data;

import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Patron;
import bcu.d3.librarysystem.model.Loan;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;

public class LoanDataManager implements DataManager {
    
    private final String RESOURCE = "./resources/data/loans.txt";
    
    @Override
    public void loadData(Library library) throws IOException, LibraryException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    try {
                        int patronId = Integer.parseInt(parts[0]);
                        int bookId = Integer.parseInt(parts[1]);
                        LocalDate startDate = LocalDate.parse(parts[2]);
                        LocalDate dueDate = LocalDate.parse(parts[3]);
                        int renewalCount = Integer.parseInt(parts[4]);
                        
                        Patron patron = library.getPatronByID(patronId);
                        Book book = library.getBookByID(bookId);
                        
                        if (patron != null && book != null) {
                            Loan loan = new Loan(patron, book, startDate, dueDate, renewalCount);
                            book.setLoan(loan);
                            patron.borrowBook(book, dueDate);                       }
                    } catch (Exception e) {
                        System.err.println("Warning: Skipping invalid loan data: " + line);
                    }
                }
            }
        }
    }
    
    @Override
    public void storeData(Library library) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Book book : library.getBooks()) {
                if (book.isOnLoan() && book.getLoan() != null) {
                    Loan loan = book.getLoan();
                    out.println(loan.getPatron().getId() + "|" +
                               book.getId() + "|" +
                               loan.getStartDate() + "|" +
                               loan.getDueDate() + "|" +
                               loan.getRenewalCount());
                }
            }
        }
    }
}