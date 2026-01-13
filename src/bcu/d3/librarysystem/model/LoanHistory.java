package bcu.d3.librarysystem.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


// Created for 8.1
public class LoanHistory {
    private final List<Loan> allLoans;
    
    public LoanHistory() {
        this.allLoans = new ArrayList<>();
    }
    
    public void addLoan(Loan loan) {
        allLoans.add(loan);
    }
    
    public void completeLoan(Loan loan, LocalDate returnDate) {
        loan.setReturnDate(returnDate);
    }
    
    public List<Loan> getPatronHistory(int patronId) {
        return allLoans.stream()
            .filter(loan -> loan.getPatron().getId() == patronId)
            .collect(Collectors.toList());
    }
    
    public List<Loan> getActiveLoans() {
        return allLoans.stream()
            .filter(loan -> !loan.isReturned())
            .collect(Collectors.toList());
    }
    
    public List<Loan> getCompletedLoans() {
        return allLoans.stream()
            .filter(Loan::isReturned)
            .collect(Collectors.toList());
    }
    
    public List<Loan> getOverdueLoans(LocalDate currentDate) {
        return allLoans.stream()
            .filter(loan -> loan.isOverdue(currentDate))
            .collect(Collectors.toList());
    }
    
    public int getTotalLoans() {
        return allLoans.size();
    }
    
    public int getCompletedLoanCount() {
        return (int) allLoans.stream()
            .filter(Loan::isReturned)
            .count();
    }
    
    public double getAverageLoanDuration() {
        List<Loan> completed = getCompletedLoans();
        if (completed.isEmpty()) {
            return 0.0;
        }
        
        double totalDays = completed.stream()
            .mapToLong(loan -> {
                if (loan.getReturnDate() != null) {
                    return java.time.temporal.ChronoUnit.DAYS.between(
                        loan.getStartDate(), loan.getReturnDate());
                }
                return 0;
            })
            .sum();
        
        return totalDays / completed.size();
    }
}