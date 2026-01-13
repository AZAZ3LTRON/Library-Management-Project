package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.main.LibraryException;
import java.time.LocalDate;

public class AddBook implements  Command {

    private final String title;
    private final String author;
    private final String publicationYear;
    private final String publisher;

    public AddBook(String title, String author, String publicationYear, String publisher) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.publisher = publisher;
    }
    
    @Override
    public void execute(Library library, LocalDate currentDate) throws LibraryException {
        int maxId = 0;
    	if (!library.getBooks().isEmpty()) {
    		int lastIndex = library.getBooks().size() - 1;
            maxId = library.getBooks().get(lastIndex).getId();
    	}
        int newId = maxId +1;
        Book book = new Book(newId, title, author, publicationYear, publisher);
        library.addBook(book);
        System.out.println("Book #" + book.getId() + " added successfully.");
    }
}
 