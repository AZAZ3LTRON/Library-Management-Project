package bcu.d3.librarysystem.main;

import bcu.d3.librarysystem.commands.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandParser {
    
    public static Command parse(String line) throws IOException, LibraryException {
        try {
            String[] parts = line.split(" ", 4);
            String cmd = parts[0].toLowerCase(); // Make command case-insensitive

            // Handle commands that require user input
            if (cmd.equals("addbook")) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Title: ");
                String title = br.readLine();
                System.out.print("Author: ");
                String author = br.readLine();
                System.out.print("Publication Year: ");
                String publicationYear = br.readLine();
                System.out.print("Publisher: ");
                String publisher = br.readLine();
                
                return new AddBook(title, author, publicationYear, publisher);
                
            } else if (cmd.equals("addpatron")) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Patron ID: ");
                int id = Integer.parseInt(br.readLine());
                System.out.print("Name: ");
                String name = br.readLine();
                System.out.print("Phone: ");
                String phone = br.readLine();
                System.out.print("Email: ");
                String email = br.readLine();
                
                return new AddPatron(id, name, phone, email);
                
            } else if (cmd.equals("loadgui")) {
                return new LoadGUI();
                
            } else if (parts.length == 1) {
                // Commands without arguments
                if (cmd.equals("listbooks")) {
                    return new ListBooks();
                } else if (cmd.equals("listpatrons")) {
                    return new ListPatrons();
                } else if (cmd.equals("help")) {
                    return new Help();
                }
                
            } else if (parts.length == 2) {
                // Commands with 1 argument (ID)
                int id;
                try {
                    id = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    throw new LibraryException("Invalid ID format. ID must be a number.");
                }

                if (cmd.equals("showbook")) {
                    return new ShowBook(id);
                } else if (cmd.equals("showpatron")) {
                    return new ShowPatron(id);
                } else if (cmd.equals("deletebook")) {
                    return new DeleteBook(id);
                } else if (cmd.equals("deletepatron")) {
                    return new DeletePatron(id);
                } else if (cmd.equals("patronhistory")) {
                    return new PatronHistory(id);
                }
                
            } else if (parts.length == 3) {
                // Commands with 2 arguments (patronID bookID)
                int patronID, bookID;
                try {
                    patronID = Integer.parseInt(parts[1]);
                    bookID = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    throw new LibraryException("Invalid ID format. IDs must be numbers.");
                }

                if (cmd.equals("borrow")) {
                    return new BorrowBook(patronID, bookID);
                } else if (cmd.equals("renewbook")) {
                    return new RenewBook(patronID, bookID);
                } else if (cmd.equals("returnbook")) {
                    return new ReturnBook(patronID, bookID);
                }
            }
            
            // If we get here, no command matched
            throw new LibraryException("Unknown command: '" + cmd + "'. Type 'help' for available commands.");
            
        } catch (NumberFormatException ex) {
            throw new LibraryException("Invalid number format: " + ex.getMessage());
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new LibraryException("Missing arguments for command.");
        }
    }
}