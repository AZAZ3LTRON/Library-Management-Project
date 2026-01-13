package bcu.d3.librarysystem.test;

import bcu.d3.librarysystem.model.Patron;
import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.main.LibraryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PatronTest {
    
    private Patron patron;
    private Book book1;
    private Book book2;
    private LocalDate currentDate;
    private LocalDate dueDate;
    
    @BeforeEach
    public void setUp() {
        patron = new Patron(1, "John Doe", "1234567890", "john@test.com");
        book1 = new Book(1, "Book One", "Author One", "2023", "Publisher One");
        book2 = new Book(2, "Book Two", "Author Two", "2024", "Publisher Two");
        currentDate = LocalDate.of(2024, 1, 15);
        dueDate = currentDate.plusDays(14);
    }
    
    // Test 5.2: Email field
    @Test
    public void testPatronCreationWithEmail() {
        assertEquals(1, patron.getId());
        assertEquals("John Doe", patron.getName());
        assertEquals("1234567890", patron.getPhone());
        assertEquals("john@test.com", patron.getEmail()); // 5.2 feature
        assertTrue(patron.isActive());
        assertFalse(patron.isDeleted());
        assertTrue(patron.getBooks().isEmpty());
    }
    
    @Test
    public void testPatronConstructorWithBooks() {
        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);
        
        Patron patronWithBooks = new Patron(2, "Jane Doe", "0987654321", "jane@test.com", books);
        
        assertEquals("jane@test.com", patronWithBooks.getEmail());
        assertEquals(2, patronWithBooks.getBooks().size());
        assertTrue(patronWithBooks.hasBook(book1));
        assertTrue(patronWithBooks.hasBook(book2));
    }
    
    // Test 7.2: Soft Deletion for Patrons
    @Test
    public void testSoftDeletion() throws LibraryException {
        assertFalse(patron.isDeleted());
        assertTrue(patron.isActive());
        
        // Can delete when no books borrowed
        patron.setDeleted(true);
        assertTrue(patron.isDeleted());
        assertFalse(patron.isActive());
        
        // Restore
        patron.setDeleted(false);
        assertFalse(patron.isDeleted());
        assertTrue(patron.isActive());
    }
    
    @Test
    public void testCannotDeleteWithBorrowedBooks() throws LibraryException {
        patron.borrowBook(book1, dueDate);
        assertEquals(1, patron.getBooks().size());
        
        assertThrows(LibraryException.class, () -> 
            patron.setDeleted(true));
        assertFalse(patron.isDeleted());
    }
    
    // Test 7.3: Borrowing Limits
    @Test
    public void testBorrowingLimit() {
        assertEquals(2, patron.getBorrowingLimit()); // 7.3 feature
        assertEquals(2, patron.getRemainingBorrowingCapacity());
        assertTrue(patron.canBorrowMore());
        assertFalse(patron.hasReachedBorrowingLimit());
    }
    
    @Test
    public void testBorrowingCapacityUpdates() throws LibraryException {
        // Initial capacity
        assertEquals(2, patron.getRemainingBorrowingCapacity());
        
        // Borrow one book
        patron.borrowBook(book1, dueDate);
        assertEquals(1, patron.getRemainingBorrowingCapacity());
        assertTrue(patron.canBorrowMore());
        
        // Borrow second book
        patron.borrowBook(book2, dueDate);
        assertEquals(0, patron.getRemainingBorrowingCapacity());
        assertFalse(patron.canBorrowMore());
        assertTrue(patron.hasReachedBorrowingLimit());
    }
    
    @Test
    public void testCannotExceedBorrowingLimit() throws LibraryException {
        Book book3 = new Book(3, "Book Three", "Author Three", "2024", "Publisher Three");
        
        patron.borrowBook(book1, dueDate);
        patron.borrowBook(book2, dueDate);
        
        // Should not be able to borrow third book
        assertThrows(LibraryException.class, () -> 
            patron.borrowBook(book3, dueDate));
    }
    
    // Test Borrow Book functionality
    @Test
    public void testBorrowBookSuccess() throws LibraryException {
        patron.borrowBook(book1, dueDate);
        
        assertEquals(1, patron.getBooks().size());
        assertTrue(patron.hasBook(book1));
        assertTrue(book1.isOnLoan());
        assertEquals(patron, book1.getBorrower());
    }
    
    @Test
    public void testBorrowAlreadyBorrowedBook() throws LibraryException {
        patron.borrowBook(book1, dueDate);
        
        assertThrows(LibraryException.class, () -> 
            patron.borrowBook(book1, dueDate));
    }
    
    @Test
    public void testBorrowBookWhenDeleted() throws LibraryException {
        patron.setDeleted(true);
        
        assertThrows(LibraryException.class, () -> 
            patron.borrowBook(book1, dueDate));
    }
    
    // Test Return Book functionality
    @Test
    public void testReturnBookSuccess() throws LibraryException {
        patron.borrowBook(book1, dueDate);
        assertTrue(patron.hasBook(book1));
        
        patron.returnBook(book1);
        assertFalse(patron.hasBook(book1));
        assertTrue(patron.getBooks().isEmpty());
        assertFalse(book1.isOnLoan());
    }
    
    @Test
    public void testReturnBookNotBorrowed() {
        assertThrows(LibraryException.class, () -> 
            patron.returnBook(book1));
    }
    
    // Test Renew Book functionality
    @Test
    public void testRenewBookSuccess() throws LibraryException {
        patron.borrowBook(book1, dueDate);
        LocalDate newDueDate = dueDate.plusDays(14);
        
        patron.renewBook(book1, newDueDate);
        assertEquals(newDueDate, book1.getDueDate());
        assertEquals(1, book1.getRenewalCount());
    }
    
    @Test
    public void testRenewBookNotBorrowed() {
        assertThrows(LibraryException.class, () -> 
            patron.renewBook(book1, dueDate));
    }
    
    @Test
    public void testRenewBookWhenDeleted() throws LibraryException {
        patron.borrowBook(book1, dueDate);
        patron.setDeleted(true);
        
        assertThrows(LibraryException.class, () -> 
            patron.renewBook(book1, dueDate.plusDays(14)));
    }
    
    @Test
    public void testRenewOverdueBook() throws LibraryException {
        LocalDate pastDueDate = currentDate.minusDays(1);
        patron.borrowBook(book1, pastDueDate);
        
        assertThrows(LibraryException.class, () -> 
            patron.renewBook(book1, currentDate.plusDays(14)));
    }
    
    @Test
    public void testRenewMaxRenewals() throws LibraryException {
        patron.borrowBook(book1, dueDate);
        
        // First renewal
        patron.renewBook(book1, dueDate.plusDays(14));
        
        // Second renewal
        patron.renewBook(book1, dueDate.plusDays(28));
        
        // Should not allow third renewal
        assertThrows(LibraryException.class, () -> 
            patron.renewBook(book1, dueDate.plusDays(42)));
    }
    
    // Test Overdue Books functionality
    @Test
    public void testHasOverdueBooks() throws LibraryException {
        LocalDate pastDueDate = currentDate.minusDays(1);
        patron.borrowBook(book1, pastDueDate);
        
        assertTrue(patron.hasOverdueBooks(currentDate));
        assertEquals(1, patron.getOverdueBooks(currentDate).size());
        assertTrue(patron.getOverdueBooks(currentDate).contains(book1));
    }
    
    @Test
    public void testHasNoOverdueBooks() throws LibraryException {
        LocalDate futureDueDate = currentDate.plusDays(1);
        patron.borrowBook(book1, futureDueDate);
        
        assertFalse(patron.hasOverdueBooks(currentDate));
        assertTrue(patron.getOverdueBooks(currentDate).isEmpty());
    }
    
    @Test
    public void testMultipleOverdueBooks() throws LibraryException {
        LocalDate pastDueDate = currentDate.minusDays(1);
        patron.borrowBook(book1, pastDueDate);
        patron.borrowBook(book2, pastDueDate);
        
        assertTrue(patron.hasOverdueBooks(currentDate));
        assertEquals(2, patron.getOverdueBooks(currentDate).size());
    }
    
    // Test Book Management
    @Test
    public void testHasBook() throws LibraryException {
        assertFalse(patron.hasBook(book1));
        
        patron.borrowBook(book1, dueDate);
        assertTrue(patron.hasBook(book1));
        
        patron.returnBook(book1);
        assertFalse(patron.hasBook(book1));
    }
    
    // Test Book List Immutability
    @Test
    public void testBooksListImmutability() throws LibraryException {
        patron.borrowBook(book1, dueDate);
        
        List<Book> books = patron.getBooks();
        assertEquals(1, books.size());
        
        // Attempting to modify the returned list should not affect patron's internal list
        books.clear();
        assertEquals(1, patron.getBooks().size()); // Should still have 1 book
    }
    
    // Test Edge Cases
    @Test
    public void testBorrowNullBook() {
        assertThrows(NullPointerException.class, () -> 
            patron.borrowBook(null, dueDate));
    }
    
    @Test
    public void testReturnNullBook() {
        assertThrows(NullPointerException.class, () -> 
            patron.returnBook(null));
    }
    
    @Test
    public void testRenewNullBook() {
        assertThrows(NullPointerException.class, () -> 
            patron.renewBook(null, dueDate));
    }
    
    // Test String Representation
    @Test
    public void testToString() {
        String expected = "Patron ID: 1, Name: John Doe, Phone: 1234567890, Books Borrowed: 0";
        assertEquals(expected, patron.toString());
    }
    
    @Test
    public void testToStringWithBooks() throws LibraryException {
        patron.borrowBook(book1, dueDate);
        patron.borrowBook(book2, dueDate);
        
        String expected = "Patron ID: 1, Name: John Doe, Phone: 1234567890, Books Borrowed: 2";
        assertEquals(expected, patron.toString());
    }
    
    // Test Borrowing from another patron's perspective
    @Test
    public void testIndependentPatrons() throws LibraryException {
        Patron patron2 = new Patron(2, "Jane Doe", "0987654321", "jane@test.com");
        
        patron.borrowBook(book1, dueDate);
        
        // patron2 should not have book1
        assertFalse(patron2.hasBook(book1));
        assertEquals(0, patron2.getBooks().size());
        
        // patron2 should be able to borrow book2
        patron2.borrowBook(book2, dueDate);
        assertTrue(patron2.hasBook(book2));
    }
    
    // Test Return Book Affects Borrowing Capacity
    @Test
    public void testReturnBookUpdatesCapacity() throws LibraryException {
        patron.borrowBook(book1, dueDate);
        patron.borrowBook(book2, dueDate);
        
        assertFalse(patron.canBorrowMore()); // At limit
        
        patron.returnBook(book1);
        assertTrue(patron.canBorrowMore()); // Below limit
        assertEquals(1, patron.getRemainingBorrowingCapacity());
    }
}