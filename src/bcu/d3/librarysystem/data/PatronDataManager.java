package bcu.d3.librarysystem.data;

import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Patron;
import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.main.LibraryException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class PatronDataManager implements DataManager {
    
    private final String RESOURCE = "./resources/data/patrons.txt";
    
    @Override
    public void loadData(Library library) throws IOException, LibraryException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                String[] properties = line.split(SEPARATOR, -1);
                if (properties.length < 5) {
                    throw new LibraryException("Invalid data format on line " + line_idx  + "\nExpected format: id|name|phone|email|deleted");
                }
                
                try {
                    int id = Integer.parseInt(properties[0]);
                    String name = properties[1];
                    String phone = properties[2];
                    String email = properties[3];
                    
                    //Read delete flag (7.2)
                    boolean userdeleted = Boolean.parseBoolean(properties[4]);
                    
                    // Modified (To handle the patron's borrowed)
                    List<Book> books = new ArrayList<>();

                    if (properties.length > 5 && !properties[5].isEmpty()){
                        String[] bookIds = properties[5].split("\\|");
                        for (String bookIdString : bookIds){
                            try {
                                int bookId = Integer.parseInt(bookIdString.trim());
                                Book book = library.getBookByID(bookId);
                                if (book != null && !book.isDeleted()){
                                    books.add(book);
                                }
                            } catch (NumberFormatException | LibraryException e) {
                                System.err.println("Invalid book ID");
                            }
                        }
                    }

                    Patron patron = new Patron(id, name, phone, email, books);
                    library.addPatron(patron);
                    
                } catch (NumberFormatException ex) {
                    throw new LibraryException("Unable to parse patron id " + properties[0] + " on line " + line_idx + "\nError: " + ex);
                }
                line_idx++;
            }
        }
    }
    
    @Override
    public void storeData(Library library) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Patron patron : library.getAllPatrons()) {
                out.println(patron.getId() + SEPARATOR + 
                           patron.getName() + SEPARATOR + 
                           patron.getPhone() + SEPARATOR +
                           patron.getEmail() + SEPARATOR +
                           patron.isDeleted());
            }
        }
    }
}