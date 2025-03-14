package usluge;

public class LicniRacun {
	private double balance;

    public LicniRacun() {
        this.balance = 0.0;
    }
    
    public LicniRacun(double initialBalance) {
        this.balance = initialBalance;
    }

    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance)
    {
    	this.balance = balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        } else {
            throw new IllegalArgumentException("Depozit mora biti pozitivan broj");
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        } else {
            //throw new IllegalArgumentException("Pogresna suma podizanja");
        	return false; // nedostaje novca na racunu;
        }
    }

    @Override
    public String toString() {
        return "\nLicni racun { balance = " + balance + '}';
    }
}
