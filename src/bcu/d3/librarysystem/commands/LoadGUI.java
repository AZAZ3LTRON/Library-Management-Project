package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.gui.MainWindow;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.main.LibraryException;
import java.time.LocalDate;

public class LoadGUI implements Command {

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        new MainWindow(library);
    }
    
}
 