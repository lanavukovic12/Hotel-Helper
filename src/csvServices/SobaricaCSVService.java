package csvServices;

import csv.CSVHandler;
import interfejsi.OsobljeZaCiscenje;
import uloge.Administrator;
import uloge.Sobarica;
import uloge.Employee;

public class SobaricaCSVService {

    private CSVHandler csvHandler;

    public SobaricaCSVService() {
        this.csvHandler = new CSVHandler();
    }

    // Method to update an existing cleaning lady
    public void updateSobarica(OsobljeZaCiscenje sobarica) {
        // Update the Sobarica in the OsobljeZaCiscenje.csv file
        csvHandler.updateOsobljeZaCiscenjeInCSV(sobarica);
        
        // Update the Sobarica in the Employees.csv file
        csvHandler.updateEmployeeInCSV((Employee) sobarica);
    }

    // Method to delete a cleaning lady
    public void deleteSobarica(String username) {
        // Delete the Sobarica from the OsobljeZaCiscenje.csv file
        csvHandler.deleteOsobljeZaCiscenjeFromCSV(username);
        
        // Delete the Sobarica from the Employees.csv file
        csvHandler.deleteEmployeeFromCSV(username);
    }
}
