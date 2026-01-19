package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.main.LibraryException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ListBooks implements Command {

    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        // Get all books and filter out deleted ones
        List<Book> booksList = library.getAllBooks().stream()
            .filter(book -> !book.isDeleted())  // Filter out deleted books
            .collect(Collectors.toList());
        
        if (booksList.isEmpty()) {
            System.out.println("\nNo active books found.\n");
            return;
        }

        System.out.println("Books in the library");
        System.out.println("Total active books in the library: " + booksList.size() + " book(s)");
        for (Book book : booksList) {
            System.out.println(book.getDetailsShort());
        }
        
        // Optional: Show count of deleted books
        long deletedCount = library.getAllBooks().stream()
            .filter(Book::isDeleted)
            .count();
        if (deletedCount > 0) {
            System.out.println("\nNote: " + deletedCount + " book(s) are deleted and not shown above.");
        }
    }
}