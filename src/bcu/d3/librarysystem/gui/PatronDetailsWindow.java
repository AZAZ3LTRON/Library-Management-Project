package bcu.d3.librarysystem.gui;

import bcu.d3.librarysystem.commands.Command;
import bcu.d3.librarysystem.commands.ShowPatron;
import bcu.d3.librarysystem.model.Patron;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class PatronDetailsWindow extends JDialog implements ActionListener {

    private Patron patron;
    private JButton closeBtn = new JButton("Close");

    public PatronDetailsWindow(MainWindow mw, Patron patron) {
        super(mw, "Patron Details", true);
        this.patron = patron;
        initialize();
    }

    private void initialize() {
        setTitle("Patron Details - ID: " + patron.getId());
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        
        // Use ShowPatron command logic to display patron details
        String patronDetails = getPatronDetailsFromCommand();
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Info panel with patron details
        JTextArea detailsArea = new JTextArea(patronDetails);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        mainPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        closeBtn.addActionListener(this);
        buttonPanel.add(closeBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private String getPatronDetailsFromCommand() {
        try {
            // Create ShowPatron command
            Command showPatron = new ShowPatron(patron.getId());
            
            // Capture the output that would go to console
            // Since ShowPatron prints to System.out, we need to capture it
            // Therefore, we'll format it ourselves to match the command output
            
            return formatPatronDetails();
            
        } catch (Exception e) {
            return "Error loading patron details: " + e.getMessage();
        }
    }
    
    private String formatPatronDetails() {
        String details = "══════════════════════════════════════════\n";
        details = details + "PATRON DETAILS\n";
        details = details + "══════════════════════════════════════════\n";
        details = details + "Patron ID: " + patron.getId() + "\n";
        details = details + "Name: " + patron.getName() + "\n";
        details = details + "Phone: " + patron.getPhone() + "\n";
        details = details + "Email: " + patron.getEmail() + "\n";
        details = details + "Books Borrowed: " + patron.getBooks().size() + "\n";
        details = details + "══════════════════════════════════════════\n";
        return details;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == closeBtn) {
            dispose();
        }
    }
}