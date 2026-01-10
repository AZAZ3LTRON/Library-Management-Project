package bcu.d3.librarysystem.gui;

import bcu.d3.librarysystem.commands.Command;
import bcu.d3.librarysystem.commands.DeleteBook;
import bcu.d3.librarysystem.main.LibraryException;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import javax.swing.*;

// For 7.4
public class DeleteBookWindow extends JFrame implements ActionListener {
 
    private MainWindow mw;
    private JTextField bookIdText = new JTextField();

    private JButton deleteBtn = new JButton("Delete");
    private JButton cancelBtn = new JButton("Cancel");
    private JButton findBtn = new JButton("Find Book");

    public DeleteBookWindow(MainWindow mw){
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Kept default for look & feel
        }
        
        setTitle("Delete a book");
        setSize(300, 200);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        JPanel bottomPanel = new JPanel();
        JPanel statusPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createTitledBorder("Book Information"));
        
        // Input panel (For searching the book)
        inputPanel.add(new JLabel("What book do you want to delete"));
        inputPanel.add(bookIdText);
        inputPanel.add(findBtn);
        findBtn.addActionListener(this);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Button Panel (For the button)
        bottomPanel.setLayout(new GridLayout(1, 3));
        deleteBtn.addActionListener(this);
        cancelBtn.addActionListener(this);       
        bottomPanel.add(deleteBtn);
        bottomPanel.add(cancelBtn);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Set Enter key to trigger find
        bookIdText.addActionListener(e -> findBookDetails());
        
        // Set initial focus
        bookIdText.requestFocus();
    }

    private void findBookDetails() {
        String idText = bookIdText.getText().trim();
        
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a Book ID.", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int bookId = Integer.parseInt(idText);
            
            // Try to get book from library
            try {
                var book = mw.getLibrary().getBookByID(bookId);
                
                // Display book details
                StringBuilder details = new StringBuilder();
                details.append("Book Found:\n");
                details.append("══════════════════════════════════════════\n");
                details.append("ID: ").append(book.getId()).append("\n");
                details.append("Title: ").append(book.getTitle()).append("\n");
                details.append("Author: ").append(book.getAuthor()).append("\n");
                details.append("Year: ").append(book.getPublicationYear()).append("\n");
                details.append("Publisher: ").append(book.getPublisher()).append("\n");
                details.append("Status: ").append(book.getStatus()).append("\n");
                details.append("══════════════════════════════════════════\n");
                
                // Check if book can be deleted
                if (book.isOnLoan()) {
                    details.append("\n⚠ WARNING: This book is currently on loan.\n");
                    details.append("Books cannot be deleted while on loan.\n");
                    deleteBtn.setEnabled(false);
                } else if (book.isDeleted()) {
                    details.append("\n⚠ WARNING: This book is already deleted.\n");
                    deleteBtn.setEnabled(false);
                } else {
                    details.append("\n✅ This book can be deleted.\n");
                    deleteBtn.setEnabled(true);
                }
                
            } catch (LibraryException e) {
                // Book not found or deleted
                JOptionPane.showMessageDialog(null, "Book not found");
                deleteBtn.setEnabled(false);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid Book ID. Please enter a number.", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
            deleteBtn.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == findBtn) {
            findBookDetails();
        } else if (ae.getSource() == deleteBtn) {
            deleteBook();
        } else if (ae.getSource() == cancelBtn) {
            dispose();
        }
    }

    private void deleteBook(){
        String idText = bookIdText.getText().trim();

        if (idText.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter a Book ID");
            return;
        }
        try{
            int bookId = Integer.parseInt(idText);
            Command deleteBook = new DeleteBook(bookId);
            deleteBook.execute(mw.getLibrary(), LocalDate.now());
            mw.displayBooks();
        }
        catch (LibraryException ex){
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

