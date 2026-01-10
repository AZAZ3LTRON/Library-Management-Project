package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.main.LibraryException;

import java.time.LocalDate;
import java.util.List;

public class ListBooks implements Command {

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        List<Book> booksList = library.getAllBooks();
        
        if (booksList.isEmpty()) {
            System.out.println("\nNo books found.\n");
            return;
        }

        System.out.println("Books in the library");
        System.out.println("Total books in the library is" + booksList.size() + " book(s)");
        for (Book book : booksList) {
            System.out.println(book.getDetailsShort());
        }

    }
}
