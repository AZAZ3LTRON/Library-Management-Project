package bcu.d3.librarysystem.gui;

import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Patron;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;

public class PatronDetailsWindow extends JDialog implements ActionListener {

    private Patron patron;
    private MainWindow mainWindow;
    private LocalDate currentDate;
    
    private JTextArea detailsArea;
    private JTextArea booksArea;
    private JButton closeButton;
    private JButton bookDetails;
    
    public PatronDetailsWindow(MainWindow mw, Patron patron) {
        super(mw, "Patron Details", true);
        this.mainWindow = mw;
        this.patron = patron;
        this.currentDate = LocalDate.now();
        
        initialize();
        loadPatronDetails();
        setVisible(true);
    }
    
    private void initialize() {
        setSize(600, 500);
        setLocationRelativeTo(mainWindow);
        
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 1. Top panel: Patron information
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Patron Information"));
        
        JLabel titleLabel = new JLabel("PATRON DETAILS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel detailsGrid = new JPanel(new GridLayout(4, 2, 5, 5));
        detailsGrid.add(new JLabel("Patron ID:"));
        detailsGrid.add(new JLabel(String.valueOf(patron.getId())));
        detailsGrid.add(new JLabel("Name:"));
        detailsGrid.add(new JLabel(patron.getName()));
        detailsGrid.add(new JLabel("Phone:"));
        detailsGrid.add(new JLabel(patron.getPhone()));
        detailsGrid.add(new JLabel("Email:"));
        detailsGrid.add(new JLabel(patron.getEmail()));
        
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(detailsGrid);
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        // 2. Center panel: Books borrowed
        JPanel booksPanel = new JPanel(new BorderLayout());
        booksPanel.setBorder(BorderFactory.createTitledBorder("Books Currently Borrowed"));
        
        booksArea = new JTextArea(15, 50);
        booksArea.setEditable(false);
        booksArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        booksArea.setLineWrap(true);
        booksArea.setWrapStyleWord(true);
        
        JButton booksDetailsButton = new JButton("Show Books Details");
        booksDetailsButton.addActionListener(e -> showBooksDetailsPopup());
        
        JScrollPane scrollPane = new JScrollPane(booksArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        booksPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(booksPanel, BorderLayout.CENTER);
        
        // 3. Bottom panel: Status and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(booksDetailsButton);
        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton);
        
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    // Add this method:
    private void showBooksDetailsPopup() {
        new PatronBooksDetailsPopup(mainWindow, patron);
    }
        
    private void loadPatronDetails() {
        // Update books area
        List<Book> books = patron.getBooks();
        StringBuilder booksText = new StringBuilder();
        
        if (books.isEmpty()) {
            booksText.append("No books currently borrowed.\n");
        } else {
            booksText.append("Total books borrowed: ").append(books.size()).append("\n");
            booksText.append("══════════════════════════════════════════\n\n");
            
            int overdueCount = 0;
            for (int i = 0; i < books.size(); i++) {
                Book book = books.get(i);
                
                booksText.append(i + 1).append(". ").append(book.getTitle()).append("\n");
                booksText.append("   Author: ").append(book.getAuthor()).append("\n");
                booksText.append("   Book ID: ").append(book.getId()).append("\n");
                
                if (book.getDueDate() != null) {
                    booksText.append("   Due Date: ").append(book.getDueDate());
                    
                    // Check if overdue
                    if (book.getDueDate().isBefore(currentDate)) {
                        booksText.append(" (OVERDUE!)");
                        overdueCount++;
                    }
                    booksText.append("\n");
                }
                
                booksText.append("   Renewals used: ").append(book.getRenewalCount()).append("/2\n");
                
                if (i < books.size() - 1) {
                    booksText.append("\n");
                }
            }
            
            booksText.append("\n══════════════════════════════════════════\n");
            
            if (overdueCount > 0) {
                booksText.append("⚠ WARNING: ").append(overdueCount)
                         .append(" book(s) are overdue!\n");
            }
        }
        
        booksArea.setText(booksText.toString());
        booksArea.setCaretPosition(0); // Scroll to top
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeButton) {
            dispose();
        }
    }
}