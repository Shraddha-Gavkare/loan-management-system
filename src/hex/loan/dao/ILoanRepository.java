package hex.loan.dao;

import hex.loan.entity.Loan;
import hex.loan.exception.InvalidLoanException;
import java.util.List;

public interface ILoanRepository {

    // a. Apply Loan with user confirmation
    void applyLoan(Loan loan);

    // b. Calculate Interest from DB
    double calculateInterest(int loanId) throws InvalidLoanException;

    // b.i Overloaded - calculate interest using parameters
    double calculateInterest(double principalAmount, double interestRate, int loanTerm);

    // c. Approve/Reject based on credit score
    String loanStatus(int loanId) throws InvalidLoanException;

    // d. Calculate EMI from DB
    double calculateEMI(int loanId) throws InvalidLoanException;

    // d.i Overloaded - calculate EMI using parameters
    double calculateEMI(double principalAmount, double annualRate, int tenureInMonths);

    // e. Repay Loan using a lump sum amount
    String loanRepayment(int loanId, double amount) throws InvalidLoanException;

    // f. Get All Loans
    List<Loan> getAllLoan();

    // g. Get Loan By ID
    Loan getLoanById(int loanId) throws InvalidLoanException;
}
