package hex.loan.exception;

public class InvalidLoanException extends Exception {

    public InvalidLoanException() {
        super("Invalid loan operation.");
    }

    public InvalidLoanException(String message) {
        super(message);
    }
}
