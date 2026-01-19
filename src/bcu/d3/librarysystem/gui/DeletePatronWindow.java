package bcu.d3.librarysystem.gui;

import bcu.d3.librarysystem.commands.DeletePatron;
import bcu.d3.librarysystem.commands.Command;
import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Patron;
import bcu.d3.librarysystem.model.Book;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;

public class DeletePatronWindow extends JDialog implements ActionListener {

    private MainWindow mw;
    private JTextField patronIdText = new JTextField(10);
    private JTextArea statusArea = new JTextArea(5, 30);
    private JTextArea booksArea = new JTextArea(10, 30);
    
    private JButton deleteBtn = new JButton("Delete Patron");
    private JButton cancelBtn = new JButton("Cancel");
    private JButton findBtn = new JButton("Find Patron");

    public DeletePatronWindow(MainWindow mw) {
        super(mw, "Delete Patron", true);
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("Delete Patron from Library");
        setSize(600, 500);
        setLocationRelativeTo(mw);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Main panel with tabbed interface
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Find Patron
        JPanel findPanel = createFindPanel();
        tabbedPane.addTab("Find Patron", findPanel);
        
        // Tab 2: Current Loans (if any)
        JPanel loansPanel = createLoansPanel();
        tabbedPane.addTab("Current Loans", loansPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        deleteBtn.setBackground(new Color(220, 80, 80));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(this);
        deleteBtn.setEnabled(false);
        cancelBtn.addActionListener(this);
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(deleteBtn);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        
        // Set Enter key to trigger find
        patronIdText.addActionListener(e -> findPatronDetails());
        
        // Set initial focus
        patronIdText.requestFocus();
    }
    
    private JPanel createFindPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Patron Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        inputPanel.add(new JLabel("Patron ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(patronIdText, gbc);
        gbc.gridx = 2;
        findBtn.addActionListener(this);
        inputPanel.add(findBtn, gbc);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Status panel
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(statusArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Patron Details"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        booksArea.setEditable(false);
        booksArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        booksArea.setLineWrap(true);
        booksArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(booksArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Borrowed Books"));
        
        JLabel infoLabel = new JLabel("This tab will show books borrowed by the patron.");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        
        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == findBtn) {
            findPatronDetails();
        } else if (ae.getSource() == deleteBtn) {
            deletePatron();
        } else if (ae.getSource() == cancelBtn) {
            dispose();
        }
    }

    private void findPatronDetails() {
        String idText = patronIdText.getText().trim();
        
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a Patron ID.", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int patronId = Integer.parseInt(idText);
            
            // Try to get patron from library
            try {
                Patron patron = mw.getLibrary().getPatronByID(patronId);
                
                // Display patron details
                StringBuilder details = new StringBuilder();
                details.append("Patron Found:\n");
                details.append("══════════════════════════════════════════\n");
                details.append("ID: ").append(patron.getId()).append("\n");
                details.append("Name: ").append(patron.getName()).append("\n");
                details.append("Phone: ").append(patron.getPhone()).append("\n");
                details.append("Email: ").append(patron.getEmail()).append("\n");
                details.append("Books Borrowed: ").append(patron.getBooks().size()).append("\n");
                details.append("Status: ACTIVE\n");
                details.append("══════════════════════════════════════════\n");
                
                // Check if patron can be deleted
                List<Book> books = patron.getBooks();
                if (!books.isEmpty()) {
                    details.append("\n⚠ WARNING: This patron has ").append(books.size())
                           .append(" book(s) on loan.\n");
                    details.append("Patrons cannot be deleted while they have books.\n");
                    deleteBtn.setEnabled(false);
                    
                    // Update books area
                    updateBooksArea(patron);
                } else if (patron.isDeleted()) {
                    details.append("\n⚠ WARNING: This patron is already deleted.\n");
                    deleteBtn.setEnabled(false);
                } else {
                    details.append("\n✅ This patron can be deleted.\n");
                    deleteBtn.setEnabled(true);
                }
                
                statusArea.setText(details.toString());
                statusArea.setCaretPosition(0);
                
            } catch (LibraryException e) {
                // Patron not found or deleted
                statusArea.setText("Patron not found or has been deleted.\n" +
                                 "Error: " + e.getMessage());
                deleteBtn.setEnabled(false);
                booksArea.setText("");
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid Patron ID. Please enter a number.", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
            deleteBtn.setEnabled(false);
        }
    }
    
    private void updateBooksArea(Patron patron) {
        List<Book> books = patron.getBooks();
        
        if (books.isEmpty()) {
            booksArea.setText("No books currently borrowed.");
            return;
        }
        
        StringBuilder booksText = new StringBuilder();
        booksText.append("Books currently borrowed by ").append(patron.getName()).append(":\n");
        booksText.append("══════════════════════════════════════════\n\n");
        
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            booksText.append(i + 1).append(". Book ID: ").append(book.getId()).append("\n");
            booksText.append("   Title: ").append(book.getTitle()).append("\n");
            booksText.append("   Author: ").append(book.getAuthor()).append("\n");
            
            if (book.getDueDate() != null) {
                booksText.append("   Due Date: ").append(book.getDueDate()).append("\n");
            }
            
            booksText.append("\n");
        }
        
        booksText.append("══════════════════════════════════════════\n");
        booksText.append("Return all books before deleting this patron.");
        
        booksArea.setText(booksText.toString());
        booksArea.setCaretPosition(0);
    }

    private void deletePatron() {
        String idText = patronIdText.getText().trim();
        
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a Patron ID.", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int patronId = Integer.parseInt(idText);
            
            // Confirm deletion
            int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to soft delete this patron?\n" +
                "The patron will be marked as deleted but not removed from the system.\n\n" +
                "Deleted patrons cannot borrow or return books.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Create and execute the DeletePatron Command
            Command deletePatronCmd = new DeletePatron(patronId);
            deletePatronCmd.execute(mw.getLibrary(), LocalDate.now());
            
            // FIX: Use the new refresh method
            mw.refreshAllDisplays();
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "Patron has been successfully soft deleted.\n" +
                "They will no longer appear in regular listings.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose(); // Close the window
            
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error deleting patron: " + ex.getMessage(), 
                "Deletion Failed", 
                JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid Patron ID. Please enter a number.", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
            }
        }
}