package hex.loan.entity;

public class CarLoan extends Loan {
    private String carModel;
    private int carValue;

    public CarLoan() {
        super();
    }

    public CarLoan(int loanId, Customer customer, double principalAmount, double interestRate,
                   int loanTerm, String loanType, String loanStatus, String carModel, int carValue) {
        super(loanId, customer, principalAmount, interestRate, loanTerm, loanType, loanStatus);
        this.carModel = carModel;
        this.carValue = carValue;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public int getCarValue() {
        return carValue;
    }

    public void setCarValue(int carValue) {
        this.carValue = carValue;
    }

    @Override
    public String toString() {
        return super.toString() + ", CarLoan [CarModel=" + carModel + ", CarValue=" + carValue + "]";
    }
}
