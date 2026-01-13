package bcu.d3.librarysystem.test;

import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Loan;
import bcu.d3.librarysystem.model.Patron;
import bcu.d3.librarysystem.main.LibraryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {
    
    private Book book;
    private Patron patron;
    private LocalDate currentDate;
    private LocalDate dueDate;
    
    @BeforeEach
    public void setUp() {
        book = new Book(1, "Test Book", "Test Author", "2023", "Test Publisher");
        patron = new Patron(1, "John Doe", "1234567890", "john@test.com");
        currentDate = LocalDate.of(2024, 1, 15);
        dueDate = currentDate.plusDays(14);
    }
    
    // Test 5.2: Basic Book properties
    @Test
    public void testBookCreation() {
        assertEquals(1, book.getId());
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("2023", book.getPublicationYear());
        assertEquals("Test Publisher", book.getPublisher());
        assertFalse(book.isDeleted());
        assertFalse(book.isOnLoan());
    }
    
    // Test 7.1: Soft Deletion functionality
    @Test
    public void testSoftDeletion() {
        // Initially not deleted
        assertFalse(book.isDeleted());
        assertEquals("AVAILABLE", book.getStatus());
        
        // Soft delete
        book.setDeleted(true);
        assertTrue(book.isDeleted());
        assertEquals("DELETED", book.getStatus());
        
        // Restore
        book.setDeleted(false);
        assertFalse(book.isDeleted());
    }
    
    @Test
    public void testCanDeleteWhenNotOnLoan() {
        // Book not on loan should be deletable
        assertFalse(book.isOnLoan());
        assertTrue(book.canDelete());
        
        // Set deleted flag shouldn't affect deletability check
        book.setDeleted(true);
        assertFalse(book.canDelete()); // Already deleted
    }
    
    @Test
    public void testCannotDeleteWhenOnLoan() throws LibraryException {
        // Borrow book
        book.borrow(patron, dueDate);
        assertTrue(book.isOnLoan());
        assertFalse(book.canDelete());
    }
    
    // Test Book Status
    @Test
    public void testBookStatusAvailable() {
        assertEquals("AVAILABLE", book.getStatus());
    }
    
    @Test
    public void testBookStatusOnLoan() throws LibraryException {
        book.borrow(patron, dueDate);
        assertEquals("ON LOAN", book.getStatus());
    }
    
    @Test
    public void testBookStatusOnLoanOverdue() throws LibraryException {
        LocalDate pastDueDate = currentDate.minusDays(1);
        book.borrow(patron, pastDueDate);
        assertEquals("ON LOAN (OVERDUE)", book.getStatus());
    }
    
    @Test
    public void testBookStatusDeleted() {
        book.setDeleted(true);
        assertEquals("DELETED", book.getStatus());
    }
    
    // Test Borrow functionality
    @Test
    public void testBorrowBookSuccess() throws LibraryException {
        assertFalse(book.isOnLoan());
        book.borrow(patron, dueDate);
        
        assertTrue(book.isOnLoan());
        assertEquals(dueDate, book.getDueDate());
        assertEquals(patron, book.getBorrower());
    }
    
    @Test
    public void testBorrowAlreadyBorrowedBook() throws LibraryException {
        book.borrow(patron, dueDate);
        
        Patron anotherPatron = new Patron(2, "Jane Doe", "0987654321", "jane@test.com");
        assertThrows(LibraryException.class, () -> 
            book.borrow(anotherPatron, dueDate));
    }
    
    @Test
    public void testBorrowDeletedBook() {
        book.setDeleted(true);
        assertThrows(LibraryException.class, () -> 
            book.borrow(patron, dueDate));
    }
    
    // Test Return functionality
    @Test
    public void testReturnBookSuccess() throws LibraryException {
        book.borrow(patron, dueDate);
        assertTrue(book.isOnLoan());
        
        book.returnBook();
        assertFalse(book.isOnLoan());
        assertNull(book.getDueDate());
    }
    
    @Test
    public void testReturnBookNotOnLoan() {
        assertFalse(book.isOnLoan());
        assertThrows(LibraryException.class, () -> book.returnBook());
    }
    
    // Test Renew functionality
    @Test
    public void testRenewBookSuccess() throws LibraryException {
        book.borrow(patron, dueDate);
        LocalDate newDueDate = dueDate.plusDays(14);
        
        book.renew(newDueDate);
        assertEquals(newDueDate, book.getDueDate());
        assertEquals(1, book.getRenewalCount());
    }
    
    @Test
    public void testRenewBookNotOnLoan() {
        assertThrows(LibraryException.class, () -> 
            book.renew(dueDate));
    }
    
    @Test
    public void testRenewDeletedBook() {
        book.setDeleted(true);
        assertThrows(LibraryException.class, () -> 
            book.renew(dueDate));
    }
    
    @Test
    public void testCanRenew() throws LibraryException {
        book.borrow(patron, dueDate);
        assertTrue(book.canRenew()); // 0 renewals
        
        book.renew(dueDate.plusDays(14)); // 1 renewal
        assertTrue(book.canRenew()); // 1 renewal, max is 2
        
        book.renew(dueDate.plusDays(28)); // 2 renewals
        assertFalse(book.canRenew()); // Max renewals reached
    }
    
    // Test Overdue functionality
    @Test
    public void testIsOverdue() throws LibraryException {
        LocalDate pastDueDate = currentDate.minusDays(1);
        book.borrow(patron, pastDueDate);
        
        assertTrue(book.isOverdue(currentDate));
        assertEquals(1, book.daysOverdue(currentDate));
    }
    
    @Test
    public void testIsNotOverdue() throws LibraryException {
        LocalDate futureDueDate = currentDate.plusDays(1);
        book.borrow(patron, futureDueDate);
        
        assertFalse(book.isOverdue(currentDate));
        assertEquals(0, book.daysOverdue(currentDate));
    }
    
    // Test Loan Management
    @Test
    public void testSetLoan() {
        Loan loan = new Loan(patron, book, dueDate);
        book.setLoan(loan);
        
        assertTrue(book.isOnLoan());
        assertEquals(loan, book.getLoan());
        assertEquals(patron, book.getBorrower());
    }
    
    @Test
    public void testReturnToLibrary() throws LibraryException {
        book.borrow(patron, dueDate);
        assertTrue(book.isOnLoan());
        
        book.returnToLibrary();
        assertFalse(book.isOnLoan());
        assertNull(book.getLoan());
    }
    
    // Test Edge Cases
    @Test
    public void testGetDueDateWhenNotOnLoan() {
        assertNull(book.getDueDate());
    }
    
    @Test
    public void testGetBorrowerWhenNotOnLoan() {
        assertNull(book.getBorrower());
    }
    
    @Test
    public void testSetDueDateWithoutLoan() {
        assertThrows(LibraryException.class, () -> 
            book.setDueDate(dueDate));
    }
    
    // Test String Representations
    @Test
    public void testGetDetailsShort() {
        String expected = "Book:- 1 - Test Book";
        assertEquals(expected, book.getDetailsShort());
    }
    
    @Test
    public void testGetDetailsLong() {
        String expected = "Book:- 1\n" +
                         "Title:- Test Book\n" +
                         "Author:- Test Author\n" +
                         "Year:- 2023\n" +
                         "Status:- AVAILABLE";
        assertEquals(expected, book.getDetailsLong());
    }
    
    @Test
    public void testToString() {
        String expected = "Book[id=1, title=\"Test Book\", author=\"Test Author\"]";
        assertEquals(expected, book.toString());
    }
}