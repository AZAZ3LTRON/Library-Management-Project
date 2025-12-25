package bcu.d3.librarysystem.data;

import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Library;
import java.io.IOException;

public class LoanDataManager implements DataManager {
    
    public final String RESOURCE = "./resources/data/loans.txt";

    @Override
    public void loadData(Library library) throws IOException, LibraryException {
        // TODO: implementation here
    }

    @Override
    public void storeData(Library library) throws IOException {
        // TODO: implementation here
    }
    
}
 