package bcu.d3.librarysystem.data;

import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Patron;
import bcu.d3.librarysystem.main.LibraryException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

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
                if (properties.length < 3) {
                    throw new LibraryException("Invalid data format on line " + line_idx  + "\nExpected format: id|name|phone");
                }
                
                try {
                    int id = Integer.parseInt(properties[0]);
                    String name = properties[1];
                    String phone = properties[2];
                    
                    Patron patron = new Patron(id, name, phone);
                    library.addPatron(patron);
                    
                } catch (NumberFormatException ex) {
                    throw new LibraryException("Unable to parse patron id " + properties[0] + " on line " + line_idx
                        + "\nError: " + ex);
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
                           patron.getPhone());
            }
        }
    }
}