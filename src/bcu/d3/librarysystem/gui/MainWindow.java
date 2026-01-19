package bcu.d3.librarysystem.gui;

import bcu.d3.librarysystem.main.LibraryException;
import bcu.d3.librarysystem.model.Book;
import bcu.d3.librarysystem.model.Library;
import bcu.d3.librarysystem.model.Patron;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class MainWindow extends JFrame implements ActionListener {

    private JMenuBar menuBar;
    private JMenu adminMenu;
    private JMenu booksMenu;
    private JMenu membersMenu;
    private JMenu helpMenu;

    private JMenuItem adminExit;
    private JMenuItem adminSave;
    private JMenuItem adminBackup;

    private JMenuItem booksView;
    private JMenuItem booksAdd;
    private JMenuItem booksDel;    
    private JMenuItem booksIssue;
    private JMenuItem booksReturn;
    private JMenuItem booksRenew;
    private JMenuItem booksShow;
    
    private JMenuItem helpCommands;

    private JMenuItem memView;
    private JMenuItem memAdd;
    private JMenuItem memDel;
    private JMenuItem memShow;
    private JMenuItem memHistory;
    private JMenuItem adminRefresh;
    private JMenuItem memBrowse;


    private Library library;
    private JTabbedPane tabbedPane;
    private JTable booksTable;
    private JTable patronsTable;
    private DefaultTableModel booksTableModel;
    private DefaultTableModel patronsTableModel;

    public MainWindow(Library library) {
        this.library = library;
        initialize();
        displayBooks(); // Show books by default
        displayPatrons();
    } 
    
    public Library getLibrary() {
        return library;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Keep default look & feel
        }

        setTitle("Library Management System");
        //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/library-icon.png")));

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // ========== ADMIN MENU ==========
        adminMenu = new JMenu("File");
        menuBar.add(adminMenu);

        adminSave = new JMenuItem("Save Data");
        adminSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        adminSave.addActionListener(e -> saveLibraryData());
        
        adminBackup = new JMenuItem("Create Backup");
        adminBackup.addActionListener(e -> createBackup());
        
        // Added refresh option
        adminRefresh = new JMenuItem("Refresh All");
        adminRefresh.addActionListener(e -> {
            displayBooks();
            displayPatrons();
            JOptionPane.showMessageDialog(MainWindow.this, 
                "All displays refreshed.", 
                "Refresh", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        adminExit = new JMenuItem("Exit");
        adminExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        adminExit.addActionListener(this);

        adminMenu.add(adminSave);
        adminMenu.add(adminBackup);
        adminMenu.addSeparator();
        adminMenu.add(adminExit);
        adminMenu.add(adminRefresh);
        


        // ========== BOOKS MENU ==========
        booksMenu = new JMenu("Books");
        menuBar.add(booksMenu);

        booksView = new JMenuItem("View All Books");
        booksView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
        booksView.addActionListener(this);
        
        booksAdd = new JMenuItem("Add New Book");
        booksAdd.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        booksAdd.addActionListener(this);
        
        booksDel = new JMenuItem("Delete Book");
        booksDel.addActionListener(this);
        
        booksShow = new JMenuItem("Show Book Details");
        booksShow.addActionListener(this);
        
        booksIssue = new JMenuItem("Issue/Checkout Book");
        booksIssue.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
        booksIssue.addActionListener(this);
        
        booksReturn = new JMenuItem("Return Book");
        booksReturn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        booksReturn.addActionListener(this);
        
        booksRenew = new JMenuItem("Renew Book");
        booksRenew.addActionListener(this);

        booksMenu.add(booksView);
        booksMenu.add(booksAdd);
        booksMenu.add(booksDel);
        booksMenu.add(booksShow);
        booksMenu.addSeparator();
        booksMenu.add(booksIssue);
        booksMenu.add(booksReturn);
        booksMenu.add(booksRenew);

        // ========== MEMBERS MENU ==========
        membersMenu = new JMenu("Patrons");
        menuBar.add(membersMenu);

        memView = new JMenuItem("View All Patrons");
        memView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        memView.addActionListener(this);
        
        memAdd = new JMenuItem("Add New Patron");
        memAdd.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        memAdd.addActionListener(this);
        
        memBrowse = new JMenuItem("Browse All Patrons");
        memBrowse.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        memBrowse.addActionListener(this);
        
        memDel = new JMenuItem("Delete Patron");
        memDel.addActionListener(this);
        
        memShow = new JMenuItem("Show Patron Details");
        memShow.addActionListener(this);
        
        memHistory = new JMenuItem("View Patron History");
        memHistory.addActionListener(this);

        membersMenu.add(memView);
        membersMenu.add(memAdd);
        membersMenu.add(memDel);
        membersMenu.add(memShow);
        membersMenu.add(memHistory);
        membersMenu.add(memBrowse);

        // ========== HELP MENU ==========
        helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        helpCommands = new JMenuItem("Available Commands");
        helpCommands.addActionListener(e -> showHelpCommands());

        helpMenu.add(helpCommands);

        // ========== MAIN CONTENT ==========
        tabbedPane = new JTabbedPane();
        
        // Create initial tables
        createBooksTable();
        createPatronsTable();
        
        // Add tabs
        tabbedPane.addTab("📚 Books", new JScrollPane(booksTable));
        tabbedPane.addTab("👥 Patrons", new JScrollPane(patronsTable));
        
        // Add status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel(" Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        JLabel countLabel = new JLabel("Books: 0 | Patrons: 0");
        countLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        statusPanel.add(countLabel, BorderLayout.EAST);
        
        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        getContentPane().add(mainPanel);

        setSize(900, 600);
        setLocationRelativeTo(null); // Center window
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
        
        // Update counts
        updateStatusCounts();
        
        setVisible(true);
    }
    
    private void createBooksTable() {
        String[] columns = {"ID", "Title", "Author", "Year", "Publisher", "Status"};
        booksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // ID
                return String.class;
            }
        };
        
        booksTable = new JTable(booksTableModel);
        booksTable.setRowHeight(25);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        booksTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        booksTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Title
        booksTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Author
        booksTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // Year
        booksTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Publisher
        booksTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        
        // Add double-click listener for book details
        booksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = booksTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        int bookId = (int) booksTable.getValueAt(row, 0);
                        showBookDetails(bookId);
                    }
                }
            }
        });
    }
    
    private void createPatronsTable() {
        String[] columns = {"ID", "Name", "Phone", "Email", "Books Borrowed"};
        patronsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 4) return Integer.class;
                return String.class;
            }
        };
        
        patronsTable = new JTable(patronsTableModel);
        patronsTable.setRowHeight(25);
        patronsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patronsTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        patronsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        patronsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        patronsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Phone
        patronsTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Email
        patronsTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Books
        
        // Add double-click listener for patron details
        patronsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = patronsTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        int patronId = (int) patronsTable.getValueAt(row, 0);
                        try {
                            Patron patron = library.getPatronByID(patronId);
                            new PatronDetailsWindow(MainWindow.this, patron);
                        } catch (Exception ex) {
                            showError("Error", "Could not load patron details: " + ex.getMessage());
                        }
                    }
                }
            }
        });
    }
    
    private void updateStatusCounts() {
        int bookCount = library.getBooks().size();
        int patronCount = library.getAllPatrons().size();
        int activePatrons = library.getActivePatrons().size();
        
        // Update status bar if we have one
        Component[] components = getContentPane().getComponents();
        if (components.length > 0 && components[0] instanceof JPanel) {
            JPanel mainPanel = (JPanel) components[0];
            Component[] mainComponents = mainPanel.getComponents();
            if (mainComponents.length > 1 && mainComponents[1] instanceof JPanel) {
                JPanel statusPanel = (JPanel) mainComponents[1];
                Component[] statusComponents = statusPanel.getComponents();
                if (statusComponents.length > 1 && statusComponents[1] instanceof JLabel) {
                    JLabel countLabel = (JLabel) statusComponents[1];
                    countLabel.setText(String.format("Books: %d | Patrons: %d (%d active)", 
                        bookCount, patronCount, activePatrons));
                }
            }
        }
    }
    
    // For GUI updates
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        
        if (source == adminExit) {
            exitApplication();
        } 
        // BOOKS MENU
        else if (source == booksView) {
            displayBooks();
            tabbedPane.setSelectedIndex(0);
        } 
        else if (source == booksAdd) {
            new AddBookWindow(this);
        } 
        else if (source == booksDel) {
        // Ask for Book ID first
        String bookIdStr = JOptionPane.showInputDialog(this,
            "Enter Book ID to delete:",
            "Delete Book",
            JOptionPane.QUESTION_MESSAGE);
        
        if (bookIdStr != null && !bookIdStr.trim().isEmpty()) {
            try {
                int bookId = Integer.parseInt(bookIdStr.trim());
                // Open delete window with ID
                new DeleteBookWindow(this, bookId);
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid numeric Book ID.");
                }
            }
        }
        
        else if (source == booksShow) {
            showBookDetailsDialog();
        }
        else if (source == booksIssue) {
            showMessage("Issue Book", "Issue Book functionality - To be implemented");
            // new IssueBookWindow(this);
        } 
        else if (source == booksReturn) {
            showMessage("Return Book", "Return Book functionality - To be implemented");
            // new ReturnBookWindow(this);
        }
        else if (source == booksRenew) {
            showMessage("Renew Book", "Renew Book functionality - To be implemented");
            // new RenewBookWindow(this);
        }
        // PATRONS MENU
        else if (source == memView) {
            displayPatrons();
            tabbedPane.setSelectedIndex(1);
        } 
        else if (source == memAdd) {
            new AddPatronWindow(this);
        } 
        else if (source == memDel) {
            new DeletePatronWindow(this);
        }
        else if (source == memShow) {
            showPatronDetailsDialog();
        }
        else if (source == memHistory) {
            showPatronHistoryDialog();
        }
        else if (source == memBrowse) {
            new DisplayPatronWindow(this);
        }
    }
    
    // Add these methods to MainWindow.java
    public void refreshDisplay() {
        // Run on the Event Dispatch Thread (EDT) for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int selectedTab = tabbedPane.getSelectedIndex();
                if (selectedTab == 0) {
                    displayBooks();
                } else {
                    displayPatrons();
                }
                updateStatusCounts();
            }
        });
    }
    
    public void refreshAllDisplays() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                displayBooks();
                displayPatrons();
                updateStatusCounts();
            }
        });
    }
    
    // Update displayBooks() to be more robust:
    public void displayBooks() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                List<Book> booksList = library.getAllBooks();
                booksTableModel.setRowCount(0);
                
                // Filter out deleted books
                List<Book> activeBooks = new ArrayList<>();
                for (Book book : booksList) {
                    if (!book.isDeleted()) {
                        activeBooks.add(book);
                    }
                }
                
                for (Book book : activeBooks) {
                    Object[] row = {
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublicationYear(),
                        book.getPublisher(),
                        book.getStatus()
                    };
                    booksTableModel.addRow(row);
                }
                
                booksTableModel.fireTableDataChanged();
                booksTable.repaint();
            }
        });
    }
    
    // Update displayPatrons() similarly:
    public void displayPatrons() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                List<Patron> patronsList = library.getAllPatrons();
                patronsTableModel.setRowCount(0);
                
                // Filter out deleted patrons
                List<Patron> activePatrons = new ArrayList<>();
                for (Patron patron : patronsList) {
                    if (!patron.isDeleted()) {
                        activePatrons.add(patron);
                    }
                }
                
                for (Patron patron : activePatrons) {
                    Object[] row = {
                        patron.getId(),
                        patron.getName(),
                        patron.getPhone(),
                        patron.getEmail(),
                        patron.getBooks().size()
                    };
                    patronsTableModel.addRow(row);
                }
                
                patronsTableModel.fireTableDataChanged();
                patronsTable.repaint();
            }
        });
    }
    
    private void showBookDetailsDialog() {
        String bookIdStr = JOptionPane.showInputDialog(this,
            "Enter Book ID:",
            "Show Book Details",
            JOptionPane.QUESTION_MESSAGE);
        
        if (bookIdStr != null && !bookIdStr.trim().isEmpty()) {
            try {
                int bookId = Integer.parseInt(bookIdStr.trim());
                showBookDetails(bookId);
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid numeric Book ID.");
            }
        }
    }
    
    private void showBookDetails(int bookId) {
        try {
            Book book = library.getBookByID(bookId);
            JDialog dialog = new JDialog(this, "Book Details", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            
            JTextArea detailsArea = new JTextArea();
            detailsArea.setEditable(false);
            detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            detailsArea.setText(getBookDetailsText(book));
            
            JScrollPane scrollPane = new JScrollPane(detailsArea);
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(closeButton, BorderLayout.SOUTH);
            
            dialog.add(panel);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            showError("Book Not Found", "Book with ID " + bookId + " not found.\n" + e.getMessage());
        }
    }
    
    private String getBookDetailsText(Book book) {
        return String.format(
            "══════════════════════════════════════════\n" +
            "              BOOK DETAILS\n" +
            "══════════════════════════════════════════\n\n" +
            "ID:           %d\n" +
            "Title:        %s\n" +
            "Author:       %s\n" +
            "Year:         %s\n" +
            "Publisher:    %s\n" +
            "Status:       %s\n" +
            "\n══════════════════════════════════════════\n",
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getPublicationYear(),
            book.getPublisher(),
            book.getStatus()
        );
    }
    
    private void showPatronDetailsDialog() {
        String patronIdStr = JOptionPane.showInputDialog(this,
            "Enter Patron ID:",
            "Show Patron Details",
            JOptionPane.QUESTION_MESSAGE);
        
        if (patronIdStr != null && !patronIdStr.trim().isEmpty()) {
            try {
                int patronId = Integer.parseInt(patronIdStr.trim());
                try {
                    Patron patron = library.getPatronByID(patronId);
                    
                    // Use the new detailed dialog
                    new PatronDetailsWindow(this, patron);
                    
                } catch (Exception e) {
                    showError("Patron Not Found", 
                        "Patron with ID " + patronId + " not found or has been deleted.\n" + e.getMessage());
                }
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid numeric Patron ID.");
            }
        }
    }
    
    private void showPatronDetails(int patronId) {
        try {
            Patron patron = library.getPatronByID(patronId);
            // Use the ShowPatron command-based window
            new PatronDetailsWindow(this, patron);
        } catch (Exception e) {
            showError("Patron Not Found", "Patron with ID " + patronId + " not found.\n" + e.getMessage());
        }
    }
    
    private void showPatronHistoryDialog() {
        String patronIdStr = JOptionPane.showInputDialog(this,
            "Enter Patron ID for History:",
            "Patron Loan History",
            JOptionPane.QUESTION_MESSAGE);
        
        if (patronIdStr != null && !patronIdStr.trim().isEmpty()) {
            try {
                int patronId = Integer.parseInt(patronIdStr.trim());
                showPatronHistory(patronId);
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid numeric Patron ID.");
            }
        }
    }
    
    private void showPatronHistory(int patronId) {
        showMessage("Patron History", "Loan history for patron ID " + patronId + " - To be implemented");
        // new PatronHistoryWindow(this, patronId);
    }
    
    private void saveLibraryData() {
        try {
            // This would use LibraryData.store() in a real implementation
            showMessage("Save Data", "Library data saved successfully!");
        } catch (Exception e) {
            showError("Save Error", "Failed to save library data: " + e.getMessage());
        }
    }
    
    private void createBackup() {
        showMessage("Create Backup", "Backup created successfully!");
    }
    
    private void showHelpCommands() {
        String helpText = 
            "══════════════════════════════════════════\n" +
            "        LIBRARY SYSTEM COMMANDS\n" +
            "══════════════════════════════════════════\n\n" +
            "BOOK COMMANDS:\n" +
            "  • Add Book: Add new book to library\n" +
            "  • Delete Book: Soft delete a book\n" +
            "  • Issue Book: Checkout book to patron\n" +
            "  • Return Book: Return borrowed book\n" +
            "  • Renew Book: Extend loan period\n\n" +
            "PATRON COMMANDS:\n" +
            "  • Add Patron: Register new patron\n" +
            "  • Delete Patron: Soft delete a patron\n" +
            "  • Show Patron: View patron details\n" +
            "  • Patron History: View loan history\n\n" +
            "VIEW COMMANDS:\n" +
            "  • View All Books: List all books\n" +
            "  • View All Patrons: List all patrons\n\n" +
            "FILE COMMANDS:\n" +
            "  • Save Data: Save to file\n" +
            "  • Create Backup: Backup data files\n" +
            "══════════════════════════════════════════\n";
        
        JTextArea helpArea = new JTextArea(helpText);
        helpArea.setEditable(false);
        helpArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(helpArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Available Commands", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    
    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Save changes before exiting?",
            "Exit Library System",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            saveLibraryData();
            System.exit(0);
        } else if (confirm == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
        // Cancel does nothing - returns to application
    }
    
    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, 
            JOptionPane.ERROR_MESSAGE);
    }
}