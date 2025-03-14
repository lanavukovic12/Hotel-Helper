package usluge;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RacunHotela {
	private LicniRacun hotelAccount;
	private Map<LocalDate, Double> refunds;
	private Map<LocalDate, Double> wages;
	

    public RacunHotela(double initialBalance) {
        this.hotelAccount = new LicniRacun(initialBalance);
        this.refunds = new HashMap<>();
        this.wages = new HashMap<>();
    }

    public LicniRacun getHotelAccount() {
        return hotelAccount;
    }
    
    public void processRefund(double amount) {
        this.hotelAccount.withdraw(amount);
        LocalDate today = LocalDate.now();
        this.refunds.put(today, this.refunds.getOrDefault(today, 0.0) + amount); // racunajuci vec postojece danasnje refunds
    }
    
    public double getTotalRefunds(LocalDate startDate, LocalDate endDate) {
        double totalRefunds = 0.0;
        for (Map.Entry<LocalDate, Double> entry : this.refunds.entrySet()) {
            LocalDate date = entry.getKey();
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                totalRefunds += entry.getValue();
            }
        }
        return totalRefunds;
    }
    
    public void processWagePayment(double amount) {
        this.hotelAccount.withdraw(amount);
        LocalDate today = LocalDate.now();
        this.wages.put(today, this.wages.getOrDefault(today, 0.0) + amount); // racunajuci vec postojece danasnje wage payments
    }
    
    public double getTotalWages(LocalDate startDate, LocalDate endDate) {
        double totalWages = 0.0;
        for (Map.Entry<LocalDate, Double> entry : this.wages.entrySet()) {
            LocalDate date = entry.getKey();
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                totalWages += entry.getValue();
            }
        }
        return totalWages;
    }
}
