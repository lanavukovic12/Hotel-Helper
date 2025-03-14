package csvServices;

import java.time.LocalDate;

import csv.CSVHandler;
import usluge.Cenovnik;

public class TimeBoundPriceListCSVService {
	private CSVHandler csvHandler;
    
    public TimeBoundPriceListCSVService()
    {
    	this.csvHandler = new CSVHandler();
    }
    
    // Method to add a new Cenovnik to the CSV
    public void addTimeBoundPriceList(LocalDate startDate, Cenovnik cenovnik) {
        csvHandler.writeTimeBoundPriceListToCSV(startDate, cenovnik);
    }
    
    // Method to update an existing Cenovnik in the CSV
    public void updateTimeBoundPriceList(LocalDate startDate, Cenovnik updatedCenovnik) {
        csvHandler.updateTimeBoundPriceListInCSV(startDate, updatedCenovnik);
    }
}
