package csvServices;

import java.util.List;
import csv.CSVHandler;
import interfejsi.GlumiGosta;
import uloge.Administrator;
import uloge.Employee;

public class GostCSVService {

    private CSVHandler csvHandler;

    public GostCSVService() {
        this.csvHandler = new CSVHandler();
    }

    // Method to add a new guest
    public void addGost(GlumiGosta gost) {
        // Write the guest to the goste.csv file
        csvHandler.writeGostToCSV(gost);
        
        // If the guest is also an instance of Administrator, write to employees.csv as well
        if (gost instanceof Administrator) {
            csvHandler.writeEmployeeToCSV((Administrator) gost);
        }
    }

    // Method to update an existing guest
    public void updateGuest(GlumiGosta updatedGost) {
        // Update the guest in the goste.csv file
        csvHandler.updateGostInCSV(updatedGost);
        
        // If the guest is also an instance of Administrator, update in employees.csv as well
        if (updatedGost instanceof Administrator) {
            csvHandler.updateEmployeeInCSV((Administrator) updatedGost);
        }
    }

    // Method to delete a guest
    public void deleteGuest(String username) {
        // Delete the guest from the goste.csv file
        csvHandler.deleteGostFromCSV(username);
        
        // If the guest is also an instance of Administrator, delete from employees.csv as well
        if (isAdmin(username)) {
            csvHandler.deleteEmployeeFromCSV(username);
        }
    }

    // Method to retrieve all guests
    public List<GlumiGosta> getAllGuests() {
        return csvHandler.loadGosteFromCSV();
    }

    // Helper method to check if a guest is an admin
    private boolean isAdmin(String username) {
        List<Employee> employees = csvHandler.loadEmployeesFromCSV(null);
        for (Employee employee : employees) {
            if (employee.getUsername().equals(username) && employee instanceof Administrator) {
                return true;
            }
        }
        return false;
    }
}
