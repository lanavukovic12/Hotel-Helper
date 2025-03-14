package csvServices;

import csv.CSVHandler;
import interfejsi.OsobljeZaCiscenje;
import uloge.Employee;

public class EmployeeCSVService {
	private CSVHandler csvHandler;

    public EmployeeCSVService() {
        this.csvHandler = new CSVHandler();
    }

    public void addEmployee(Employee employee) {
        // Save to Employees.csv
        csvHandler.writeEmployeeToCSV(employee);

        // Check if the employee also needs to be saved as OsobljeZaCiscenje
        if (employee instanceof OsobljeZaCiscenje) {
            csvHandler.writeOsobljeZaCiscenjeToCSV((OsobljeZaCiscenje) employee);
        }
    }
    
    public void updateEmployee(Employee employee) {
        csvHandler.updateEmployeeInCSV(employee);

        // If the employee is also part of cleaning staff, update them in OsobljeZaCiscenje.csv as well
        if (employee instanceof OsobljeZaCiscenje) {
        	csvHandler.updateOsobljeZaCiscenjeInCSV((OsobljeZaCiscenje) employee);
        }
    }
    
}
