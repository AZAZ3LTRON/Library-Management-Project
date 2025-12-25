package bcu.d3.librarysystem.commands;

import bcu.d3.librarysystem.model.Library;

import java.time.LocalDate;

public class Help implements Command {

    @Override
    public void execute(Library library, LocalDate currentDate) {
        System.out.println(Command.HELP_MESSAGE);
    }
}
 