package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Library;
import java.time.LocalDate;

public interface Command {

    public static final String HELP_MESSAGE = "Commands:\n"
            + "\tlistbooks                       print all books*\n"
            + "\tlistpatrons                     print all patrons\n"
            + "\taddbook                         add a new book*\n"
            + "\taddpatron                       add a new patron\n"
            + "\tshowbook                        show book details\n"
            + "\tshowpatron                      show patron details\n"
            + "\tdeletebook                      delete a book\n"
            + "\tdeletepatron                    delete a patron\n"                  
            + "\tborrow                          borrow a book\n"
            + "\trenewbook                       renew a book\n"
            + "\treturnbook                      return a book\n"
            + "\tpatronhistory                   show patron history\n"
            + "\tloadgui                         loads the GUI version of the app*\n"
            + "\thelp                            prints this help message*\n"
            + "\texit                            exits the program*";

    
    public void execute(Library library, LocalDate currentDate) throws LibraryException;
    
}
 