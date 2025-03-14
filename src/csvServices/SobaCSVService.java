package csvServices;

import csv.CSVHandler;
import usluge.Soba;
import enumeratori.StanjeSobe;
import java.util.List;

public class SobaCSVService {

    private CSVHandler csvHandler;

    public SobaCSVService() {
        this.csvHandler = new CSVHandler();
    }

    // Method to add a new room
    public void addSoba(Soba soba) {
        csvHandler.writeSobaToCSV(soba);
    }

    // Method to update an existing room
    public void updateSoba(Soba soba) {
        csvHandler.updateSobaInCSV(soba);
    }

    // Method to delete a room
    public void deleteSoba(int brojSobe) {
        csvHandler.deleteSobaFromCSV(brojSobe);
    }

    // Method to change room status to "AVAILABLE"
    public void setSobaAvailable(Soba soba) {
        soba.setStanjeSobe(StanjeSobe.AVAILABLE);
        updateSoba(soba);  // Update the room in the CSV
    }

    // Method to load all rooms
    public List<Soba> loadSobe() {
        return csvHandler.loadSobeFromCSV();
    }

    // Method to find a room by its number
    public Soba findRoomByNumber(int brojSobe) {
        List<Soba> sobe = loadSobe();
        for (Soba soba : sobe) {
            if (soba.getBrojSobe() == brojSobe) {
                return soba;
            }
        }
        return null;  // Return null if the room is not found
    }
}
