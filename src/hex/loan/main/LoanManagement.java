 
package hex.loan.main;

import hex.loan.dao.ILoanRepository;
import hex.loan.dao.LoanRepositoryImpl;
import hex.loan.entity.*;
import hex.loan.exception.InvalidLoanException;
import java.util.*;

public class LoanManagement {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ILoanRepository repo = new LoanRepositoryImpl();

        while (true) {
            System.out.println("\n=== *Loan Management System* ===");
            System.out.println("1. Apply Loan:");
            System.out.println("2. View All Loans:");
            System.out.println("3. Get Loan By ID:");
            System.out.println("4. Calculate Interest:");
            System.out.println("5. Check Loan Status:");
            System.out.println("6. Calculate EMI:");
            System.out.println("7. Repay Loan:");
            System.out.println("8. Exit___");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1:
                        // Apply Loan
                        Customer cust = new Customer();
                        System.out.print("Enter Name: ");
                        cust.setName(sc.nextLine());
                        System.out.print("Enter Email: ");
                        cust.setEmail(sc.nextLine());
                        System.out.print("Enter Phone: ");
                        cust.setPhone(sc.nextLine());
                        System.out.print("Enter Address: ");
                        cust.setAddress(sc.nextLine());
                        System.out.print("Enter Credit Score: ");
                        cust.setCreditScore(sc.nextInt());
                        sc.nextLine();

                        System.out.print("Enter Principal Amount: ");
                        double principal = sc.nextDouble();
                        System.out.print("Enter Interest Rate: ");
                        double rate = sc.nextDouble();
                        System.out.print("Enter Loan Term (months): ");
                        int term = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Choose Loan Type (HomeLoan/CarLoan): ");
                        String type = sc.nextLine();

                        Loan loan = new Loan(0, cust, principal, rate, term, type, "Pending") {};
                        repo.applyLoan(loan);
                        break;

                    case 2:
                        List<Loan> loans = repo.getAllLoan();
                        for (Loan l : loans) {
                            System.out.println(l);
                        }
                        break;

                    case 3:
                        System.out.print("Enter Loan ID: ");
                        int id = sc.nextInt();
                        Loan l = repo.getLoanById(id);
                        System.out.println(l);
                        break;

                    case 4:
                        System.out.print("Enter Loan ID: ");
                        int lid = sc.nextInt();
                        double interest = repo.calculateInterest(lid);
                        System.out.println("Calculated Interest: " + interest);
                        break;

                    case 5:
                        System.out.print("Enter Loan ID: ");
                        int sid = sc.nextInt();
                        String status = repo.loanStatus(sid);
                        System.out.println(status);
                        break;

                    case 6:
                        System.out.print("Enter Loan ID: ");
                        int eid = sc.nextInt();
                        double emi = repo.calculateEMI(eid);
                        System.out.printf("Monthly EMI: %.2f\n", emi);
                        break;

                    case 7:
                        System.out.print("Enter Loan ID: ");
                        int rid = sc.nextInt();
                        System.out.print("Enter Repayment Amount: ");
                        double amount = sc.nextDouble();
                        String result = repo.loanRepayment(rid, amount);
                        System.out.println(result);
                        break;

                    case 8:
                        System.out.println("Exiting... Goodbye!");
                        System.exit(0);

                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (InvalidLoanException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }
}
