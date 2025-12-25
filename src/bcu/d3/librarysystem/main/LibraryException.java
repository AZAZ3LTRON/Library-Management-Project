package bcu.d3.librarysystem.main;

/**
 * LibraryException extends {@link Exception} class and is a custom exception
 * that is used to notify the user about errors or invalid commands.
 * 
 */
public class LibraryException extends Exception {

    public LibraryException(String message) {
        super(message);
    }
}
 