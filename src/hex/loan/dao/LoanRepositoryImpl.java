package hex.loan.dao;

import java.sql.*;
import java.util.*;
import hex.loan.entity.*;
import hex.loan.util.DBConnUtil;
import hex.loan.exception.InvalidLoanException;

public class LoanRepositoryImpl implements ILoanRepository {

    @Override
    public void applyLoan(Loan loan) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Do you want to apply for this loan? (Yes/No): ");
        String confirm = sc.nextLine();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Loan application cancelled.***");
            return;
        }

        String insertCustomerSQL = "INSERT INTO Customer (name, email, phone, address, credit_score) VALUES (?, ?, ?, ?, ?)";
        String insertLoanSQL = "INSERT INTO Loan (customer_id, principal_amount, interest_rate, loan_term, loan_type, loan_status) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement customerStmt = null;
        PreparedStatement loanStmt = null;

        try {conn = DBConnUtil.getDbConnection();

            conn.setAutoCommit(false);

            // 1. Insert Customer
            Customer customer = loan.getCustomer();
            customerStmt = conn.prepareStatement(insertCustomerSQL, Statement.RETURN_GENERATED_KEYS);
            customerStmt.setString(1, customer.getName());
            customerStmt.setString(2, customer.getEmail());
            customerStmt.setString(3, customer.getPhone());
            customerStmt.setString(4, customer.getAddress());
            customerStmt.setInt(5, customer.getCreditScore());
            customerStmt.executeUpdate();

            ResultSet rs = customerStmt.getGeneratedKeys();
            int customerId = -1;
            if (rs.next()) {
                customerId = rs.getInt(1);
            }

            // 2. Insert Loan
            loanStmt = conn.prepareStatement(insertLoanSQL);
            loanStmt.setInt(1, customerId);
            loanStmt.setDouble(2, loan.getPrincipalAmount());
            loanStmt.setDouble(3, loan.getInterestRate());
            loanStmt.setInt(4, loan.getLoanTerm());
            loanStmt.setString(5, loan.getLoanType());
            loanStmt.setString(6, loan.getLoanStatus());
            loanStmt.executeUpdate();

            conn.commit();
            System.out.println("Loan applied successfully and saved to database.");

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try {
                if (customerStmt != null) customerStmt.close();
                if (loanStmt != null) loanStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    @Override
    public double calculateInterest(int loanId) throws InvalidLoanException {
        String sql = "SELECT principal_amount, interest_rate, loan_term FROM Loan WHERE loan_id = ?";
        try (Connection conn = DBConnUtil.getDbConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loanId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double p = rs.getDouble("principal_amount");
                double r = rs.getDouble("interest_rate");
                int n = rs.getInt("loan_term");

                return calculateInterest(p, r, n);
            } else {
                throw new InvalidLoanException("Loan ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidLoanException("Database error occurred.");
        }
    }

    @Override
    public double calculateInterest(double principalAmount, double interestRate, int loanTerm) {
        return (principalAmount * interestRate * loanTerm) / 12;
    }
    
@Override
public String loanStatus(int loanId) throws InvalidLoanException {
    String selectSQL = "SELECT l.loan_id, c.credit_score FROM Loan l JOIN Customer c ON l.customer_id = c.customer_id WHERE l.loan_id = ?";
    String updateSQL = "UPDATE Loan SET loan_status = ? WHERE loan_id = ?";

    try (Connection conn = DBConnUtil.getDbConnection();

         PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
         PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {

        selectStmt.setInt(1, loanId);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            int creditScore = rs.getInt("credit_score");
            String newStatus = creditScore > 650 ? "Approved" : "Pending";

            updateStmt.setString(1, newStatus);
            updateStmt.setInt(2, loanId);
            updateStmt.executeUpdate();

            return "Loan Status Updated: " + newStatus;
        } else {
            throw new InvalidLoanException("Loan not found with ID: " + loanId);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        throw new InvalidLoanException("Error checking loan status.");
    }
}

@Override
public double calculateEMI(int loanId) throws InvalidLoanException {
    String sql = "SELECT principal_amount, interest_rate, loan_term FROM Loan WHERE loan_id = ?";
    try ( Connection conn = DBConnUtil.getDbConnection();

         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, loanId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            double p = rs.getDouble("principal_amount");
            double annualRate = rs.getDouble("interest_rate");
            int n = rs.getInt("loan_term");

            return calculateEMI(p, annualRate, n);
        } else {
            throw new InvalidLoanException("Loan not found.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        throw new InvalidLoanException("Error calculating EMI.");
    }
}

@Override
public double calculateEMI(double principalAmount, double annualRate, int tenureInMonths) {
    double r = annualRate / 12 / 100;
    int n = tenureInMonths;

    return (principalAmount * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
}

@Override
public String loanRepayment(int loanId, double amount) throws InvalidLoanException {
    double emi = calculateEMI(loanId);
    if (amount < emi) {
        return "Repayment failed: Amount less than single EMI.";
    }

    int paidEmis = (int)(amount / emi);
    return "Repayment success: " + paidEmis + " EMI(s) paid.";
}

@Override
public List<Loan> getAllLoan() {
    List<Loan> loans = new ArrayList<>();
    String sql = "SELECT l.*, c.* FROM Loan l JOIN Customer c ON l.customer_id = c.customer_id";

    try ( Connection conn = DBConnUtil.getDbConnection();

         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Customer c = new Customer(
                rs.getInt("customer_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getInt("credit_score")
            );

            Loan loan = new Loan(
                rs.getInt("loan_id"),
                c,
                rs.getDouble("principal_amount"),
                rs.getDouble("interest_rate"),
                rs.getInt("loan_term"),
                rs.getString("loan_type"),
                rs.getString("loan_status")
            ) {}; // Anonymous subclass since Loan is abstract

            loans.add(loan);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return loans;
}

@Override
public Loan getLoanById(int loanId) throws InvalidLoanException {
    String sql = "SELECT l.*, c.* FROM Loan l JOIN Customer c ON l.customer_id = c.customer_id WHERE l.loan_id = ?";

    try ( Connection conn = DBConnUtil.getDbConnection();

         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, loanId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Customer c = new Customer(
                rs.getInt("customer_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getInt("credit_score")
            );

            return new Loan(
                rs.getInt("loan_id"),
                c,
                rs.getDouble("principal_amount"),
                rs.getDouble("interest_rate"),
                rs.getInt("loan_term"),
                rs.getString("loan_type"),
                rs.getString("loan_status")
            ) {};
        } else {
            throw new InvalidLoanException("Loan ID not found.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        throw new InvalidLoanException("Error retrieving loan.");
    }
}
}

