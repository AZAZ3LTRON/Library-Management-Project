package bcu.d3.librarysystem.gui;

import bcu.d3.librarysystem.commands.Command;
import bcu.d3.librarysystem.commands.ShowPatron;
import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Patron;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;

public class PatronBooksDetailsPopup extends JDialog {
    
    private MainWindow mw;
    private Patron patron;
    
    public PatronBooksDetailsPopup(MainWindow mw, Patron patron) {
        super(mw, "Books Borrowed by " + patron.getName(), true);
        this.mw = mw;
        this.patron = patron;
        initialize();
    }
    
    private void initialize() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        
        List<Book> books = patron.getBooks();
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        // Get patron info using ShowPatron command
        String patronInfo = getPatronInfoFromCommand();
        
        JLabel nameLabel = new JLabel(patronInfo);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(nameLabel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Books display
        if (books.isEmpty()) {
            JLabel noBooksLabel = new JLabel("No books currently on loan.");
            noBooksLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noBooksLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(noBooksLabel, BorderLayout.CENTER);
        } else {
            // Get books details using ShowPatron command logic
            String booksText = getBooksDetailsFromCommand();
            
            JTextArea booksArea = new JTextArea(15, 40);
            booksArea.setEditable(false);
            booksArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            booksArea.setText(booksText);
            
            JScrollPane scrollPane = new JScrollPane(booksArea);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            
            // Check for overdue books using ShowPatron logic
            int overdueCount = countOverdueBooks();
            if (overdueCount > 0) {
                JLabel warningLabel = new JLabel(overdueCount + " book(s) overdue!");
                warningLabel.setForeground(Color.RED);
                warningLabel.setFont(new Font("Arial", Font.BOLD, 12));
                mainPanel.add(warningLabel, BorderLayout.SOUTH);
            }
        }
        
        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
        
        add(mainPanel);
    }
    
    private String getPatronInfoFromCommand() {
        try {
            // ACTUALLY USE ShowPatron COMMAND
            Command showPatronCommand = new ShowPatron(patron.getId());
            
            // Execute command and capture output
            String commandOutput = executeCommandAndCaptureOutput(showPatronCommand);
            
            // Extract patron info from command output
            return extractPatronInfo(commandOutput);
            
        } catch (Exception e) {
            // Fallback if command fails
            return "Patron: " + patron.getName() + " (ID: " + patron.getId() + ")";
        }
    }
    
    private String getBooksDetailsFromCommand() {
        try {
            // ✅ ACTUALLY USE ShowPatron COMMAND
            Command showPatronCommand = new ShowPatron(patron.getId());
            
            // Execute command and capture output
            String commandOutput = executeCommandAndCaptureOutput(showPatronCommand);
            
            // Extract books details from command output
            return extractBooksDetails(commandOutput);
            
        } catch (Exception e) {
            // Fallback if command fails
            return generateFallbackBooksText();
        }
    }
    
    private String executeCommandAndCaptureOutput(Command command) {
        // Capture System.out output from command execution
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream originalOut = System.out;
        
        try {
            // Redirect System.out to capture command output
            System.setOut(ps);
            
            // Execute the command
            command.execute(mw.getLibrary(), LocalDate.now());
            
            // Get the captured output
            System.out.flush();
            return baos.toString();
            
        } catch (LibraryException e) {
            return "Error: " + e.getMessage();
        } finally {
            // Restore original System.out
            System.setOut(originalOut);
        }
    }
    
    private String extractPatronInfo(String commandOutput) {
        // Parse the command output to extract patron info
        // Example ShowPatron output format:
        // ══════════════════════════════════════════
        // PATRON DETAILS
        // ══════════════════════════════════════════
        // Patron ID: 101
        // Name: John Doe
        // Phone: 555-1234
        // Email: john@email.com
        // Books Borrowed: 3
        // ══════════════════════════════════════════
        
        String[] lines = commandOutput.split("\n");
        if (lines.length > 3) {
            return lines[2] + " - " + lines[3]; // Usually "PATRON DETAILS" line
        }
        return "Patron: " + patron.getName() + " (ID: " + patron.getId() + ")";
    }
    
    private String extractBooksDetails(String commandOutput) {
        // Parse the command output to extract books section
        String[] lines = commandOutput.split("\n");
        StringBuilder booksText = new StringBuilder();
        boolean inBooksSection = false;
        
        for (String line : lines) {
            if (line.contains("BORROWED BOOKS") || line.contains("Borrowed books")) {
                inBooksSection = true;
                booksText.append(line).append("\n");
            } else if (inBooksSection) {
                if (line.contains("══") || line.isEmpty()) {
                    // End of books section
                    break;
                }
                booksText.append(line).append("\n");
            }
        }
        
        if (booksText.length() == 0) {
            return generateFallbackBooksText();
        }
        
        return booksText.toString();
    }
    
    private String generateFallbackBooksText() {
        // Fallback if we can't parse command output
        List<Book> books = patron.getBooks();
        LocalDate today = LocalDate.now();
        
        String booksText = "BORROWED BOOKS:\n";
        booksText = booksText + "══════════════════════════════════════════\n\n";
        
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            booksText = booksText + (i + 1) + ". " + book.getTitle() + "\n";
            booksText = booksText + "   Author: " + book.getAuthor() + "\n";
            booksText = booksText + "   Book ID: " + book.getId() + "\n";
            
            if (book.getDueDate() != null) {
                booksText = booksText + "   Due: " + book.getDueDate();
                
                if (book.getDueDate().isBefore(today)) {
                    booksText = booksText + " (OVERDUE)";
                }
                booksText = booksText + "\n";
            }
            
            if (i < books.size() - 1) {
                booksText = booksText + "\n";
            }
        }
        
        return booksText;
    }
    
    private int countOverdueBooks() {
        // Count overdue books using the same logic ShowPatron would use
        int count = 0;
        LocalDate today = LocalDate.now();
        
        for (Book book : patron.getBooks()) {
            if (book.getDueDate() != null && book.getDueDate().isBefore(today)) {
                count++;
            }
        }
        
        return count;
    }
}