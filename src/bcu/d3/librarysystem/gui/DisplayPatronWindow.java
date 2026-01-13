package bcu.d3.librarysystem.gui;

import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Patron;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;

public class DisplayPatronWindow extends JDialog implements ActionListener {

    private MainWindow mw;
    private List<Patron> allPatrons;
    private int currentIndex;
    private LocalDate currentDate;
    
    // Components
    private JLabel nameLabel = new JLabel();
    private JLabel idLabel = new JLabel();
    private JLabel booksCountLabel = new JLabel();
    private JTextArea booksArea = new JTextArea(10, 40);
    private JButton nextBtn = new JButton("Next");
    private JButton prevBtn = new JButton("Previous");
    private JButton closeBtn = new JButton("Close");
    
    public DisplayPatronWindow(MainWindow mw) {
        super(mw, "Browse Patrons", true);
        this.mw = mw;
        this.currentDate = LocalDate.now();
        initialize();
        loadPatrons();
    }
    
    private void initialize() {
        setTitle("Browse Patrons");
        setSize(550, 450);
        setLocationRelativeTo(getParent());
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header panel
        JPanel headerPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        headerPanel.setBorder(BorderFactory.createTitledBorder("Patron Information"));
        
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        idLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        booksCountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        headerPanel.add(nameLabel);
        headerPanel.add(idLabel);
        headerPanel.add(booksCountLabel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Books panel
        booksArea.setEditable(false);
        booksArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(booksArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Borrowed Books"));
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        prevBtn.addActionListener(this);
        nextBtn.addActionListener(this);
        closeBtn.addActionListener(this);
        
        navPanel.add(prevBtn);
        navPanel.add(closeBtn);
        navPanel.add(nextBtn);
        
        mainPanel.add(navPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadPatrons() {
        allPatrons = mw.getLibrary().getAllPatrons();
        currentIndex = 0;
        
        if (!allPatrons.isEmpty()) {
            updateDisplay();
        } else {
            showNoPatronsMessage();
        }
    }
    
    private void showNoPatronsMessage() {
        nameLabel.setText("No patrons found in the system");
        idLabel.setText("");
        booksCountLabel.setText("");
        booksArea.setText("Add patrons using the 'Patrons → Add New Patron' menu.");
        prevBtn.setEnabled(false);
        nextBtn.setEnabled(false);
    }
    
    private void updateDisplay() {
        if (allPatrons.isEmpty()) {
            showNoPatronsMessage();
            return;
        }
        
        // Ensure currentIndex is within bounds
        if (currentIndex < 0) currentIndex = 0;
        if (currentIndex >= allPatrons.size()) currentIndex = allPatrons.size() - 1;
        
        Patron currentPatron = allPatrons.get(currentIndex);
        
        // Update header
        nameLabel.setText("Name: " + currentPatron.getName());
        idLabel.setText("ID: " + currentPatron.getId() + 
                       " | Phone: " + currentPatron.getPhone() + 
                       " | Email: " + currentPatron.getEmail());
        booksCountLabel.setText("Books Borrowed: " + currentPatron.getBooks().size());
        
        // Update books list
        updateBooksList(currentPatron);
        
        // Update navigation buttons
        prevBtn.setEnabled(currentIndex > 0);
        nextBtn.setEnabled(currentIndex < allPatrons.size() - 1);
    }
    
    private void updateBooksList(Patron patron) {
        List<Book> books = patron.getBooks();
        
        if (books.isEmpty()) {
            booksArea.setText("No books currently borrowed.");
            return;
        }
        
        StringBuilder booksText = new StringBuilder();
        
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            
            // Add book number and title
            booksText.append(i + 1).append(". ").append(book.getTitle()).append("\n");
            
            // Add author
            booksText.append("   Author: ").append(book.getAuthor()).append("\n");
            
            // Add due date if available
            if (book.getDueDate() != null) {
                booksText.append("   Due: ").append(book.getDueDate());
                
                // Check if overdue
                if (book.getDueDate().isBefore(currentDate)) {
                    booksText.append(" (OVERDUE)");
                }
                booksText.append("\n");
            }
            
            // Add book ID
            booksText.append("   Book ID: ").append(book.getId()).append("\n");
            
            // Add separator between books
            if (i < books.size() - 1) {
                booksText.append("\n");
            }
        }
        
        booksArea.setText(booksText.toString());
        booksArea.setCaretPosition(0);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == prevBtn && currentIndex > 0) {
            currentIndex--;
            updateDisplay();
        } else if (e.getSource() == nextBtn && currentIndex < allPatrons.size() - 1) {
            currentIndex++;
            updateDisplay();
        } else if (e.getSource() == closeBtn) {
            dispose();
        }
    }
}