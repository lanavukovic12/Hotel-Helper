package csvServices;

import java.time.LocalDate;
import java.util.List;

import csv.CSVHandler;
import usluge.Rezervacija;
import usluge.Soba;

public class RezervacijaCSVService {
	private CSVHandler csvHandler;

    public RezervacijaCSVService() {
        this.csvHandler = new CSVHandler();
    }

    // Method to add a new reservation
    public void addRezervacija(Rezervacija rezervacija) {
        csvHandler.writeRezervacijaToCSV(rezervacija);
    }

    // Method to update an existing reservation
    public void updateRezervacija(Rezervacija rezervacija) {
        csvHandler.updateRezervacijaInCSV(rezervacija);
    }

    // Method to delete a reservation
    public void deleteRezervacija(String username, int brojSobe, LocalDate checkInDate) {
        csvHandler.deleteRezervacijaFromCSV(username, brojSobe, checkInDate);
    }
    
 // Method to check if a room is available
    public boolean isRoomAvailable(Soba soba, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Rezervacija> allReservations = csvHandler.loadRezervacijeFromCSV(
            csvHandler.loadGosteFromCSV(), 
            csvHandler.loadSobeFromCSV(), 
            csvHandler.loadTimeBoundPriceListsFromCSV().getPriceList(LocalDate.now())
        );

        for (Rezervacija rezervacija : allReservations) {
            if (rezervacija.getSoba().getBrojSobe() == soba.getBrojSobe()) {
                if (!rezervacija.getCheckOutDate().isBefore(checkInDate) && !rezervacija.getCheckInDate().isAfter(checkOutDate)) {
                    return false;
                }
            }
        }
        return true;
    }

}
