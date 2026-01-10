package bcu.d3.librarysystem.gui;

import bcu.d3.librarysystem.commands.AddPatron;
import bcu.d3.librarysystem.commands.Command;
import bcu.d3.librarysystem.main.LibraryException;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import javax.swing.*;

public class AddPatronWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField patronIdText = new JTextField();
    private JTextField patronNameText = new JTextField();
    private JTextField patronPhoneText = new JTextField();
    private JTextField patronEmailText = new JTextField();

    private JButton addBtn = new JButton("Add");
    private JButton cancelBtn = new JButton("Cancel");

    public AddPatronWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Kept default for look & feel
        } 

        setTitle("Add a New Patron to the Library System");

        setSize(300, 200);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(5, 2));
        topPanel.add(new JLabel("Patron ID : "));
        topPanel.add(patronIdText);
        topPanel.add(new JLabel("Patron Name"));
        topPanel.add(patronNameText);
        topPanel.add(new JLabel("Patron Phone Number"));
        topPanel.add(patronPhoneText);
        topPanel.add(new JLabel("Patron Email"));
        topPanel.add(patronEmailText);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));
        bottomPanel.add(new JLabel("     "));
        bottomPanel.add(addBtn);
        bottomPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addPatron();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }

    }

    private void addPatron() {
        try {
            int id = Integer.parseInt(patronIdText.getText());
            String name = patronNameText.getText();
            String phone = patronPhoneText.getText();
            String email = patronEmailText.getText();
            
            // create and execute the AddPatron Command
            Command addPatron = new AddPatron(id, name, phone, email);
            addPatron.execute(mw.getLibrary(), LocalDate.now());
            // refresh the view with the list of patrons
            mw.displayPatrons();
            // hide (close) the AddPatronWindow
            this.setVisible(false);
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
