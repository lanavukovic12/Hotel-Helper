package gui;

import javax.swing.*;
import csv.CSVHandler;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import csvServices.SobaricaCSVService;
import interfejsi.OsobljeZaCiscenje;
import interfejsi.GlumiGosta;
import uloge.Administrator;
import uloge.Employee;
import uloge.Recepcioner;
import uloge.Sobarica;
import enumeratori.Amenities;
import enumeratori.Obrazovanje;
import enumeratori.Pol;
import enumeratori.TipSobe;
import enumeratori.Uloga;
import usluge.Cenovnik;
import usluge.RacunHotela;
import usluge.Rezervacija;
import usluge.LicniRacun;
import usluge.Soba;

public class AdministratorFrame extends JFrame {
    private Administrator admin;
    private JPanel roomPanel;
    private JLabel cleanedRoomsCounterLabel;
    private CSVHandler csvHandler;

    public AdministratorFrame(Administrator admin) {
        this.admin = admin;
        this.csvHandler = new CSVHandler();

        // Enrich the admin with additional data
        enrichAdminWithCleaningData(admin);
        enrichAdminWithGostData(admin);

        // GUI setup
        setTitle("Administrator GUI");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Admin-specific Panel
        JPanel adminPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        JButton addEmployeeButton = new JButton("Add New Employee");
        JButton listEmployeesButton = new JButton("List All Employees");
        JButton addPriceListButton = new JButton("Add New Price List");
        JButton showStatisticsButton = new JButton("Show Statistics");

        adminPanel.add(addEmployeeButton);
        adminPanel.add(listEmployeesButton);
        adminPanel.add(addPriceListButton);
        adminPanel.add(showStatisticsButton);

        // Add action listeners to the buttons
        addEmployeeButton.addActionListener(e -> showAddEmployeeDialog());
        listEmployeesButton.addActionListener(e -> showListEmployeesDialog());
        addPriceListButton.addActionListener(e -> showAddTimeBoundPriceListDialog());
        showStatisticsButton.addActionListener(e -> showStatisticsDialog());

        // Sobarica (Cleaning Staff) Panel
        JPanel sobaricaPanel = new JPanel();
        sobaricaPanel.setLayout(new BoxLayout(sobaricaPanel, BoxLayout.Y_AXIS));

        roomPanel = new JPanel();
        roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));

        loadRoomList();

        JScrollPane scrollPane = new JScrollPane(roomPanel);
        sobaricaPanel.add(scrollPane);

        cleanedRoomsCounterLabel = new JLabel("Total Rooms Cleaned: " + admin.getTotalCleanedRoomsCounter());
        sobaricaPanel.add(cleanedRoomsCounterLabel, BorderLayout.SOUTH);

        // Load data for GostPanel (acting as a guest)
        List<Soba> sobe = csvHandler.loadSobeFromCSV(); // Load rooms from CSV
        Cenovnik cenovnik = csvHandler.loadTimeBoundPriceListsFromCSV().getPriceList(LocalDate.now());
        List<Rezervacija> ukupneRezervacije = csvHandler.loadRezervacijeFromCSV(csvHandler.loadGosteFromCSV(), sobe, cenovnik);
        RacunHotela hotelAccount = new RacunHotela(50000.0); // Example value, adjust accordingly

        GostPanel gostPanel = new GostPanel(admin, sobe, cenovnik, ukupneRezervacije, hotelAccount);
        tabbedPane.addTab("Act as Guest", gostPanel); // Add new tab

        RecepcionerPanel recepcionerPanel = new RecepcionerPanel(admin, hotelAccount);
        tabbedPane.addTab("Act as Receptionist", recepcionerPanel); // Add RecepcionerPanel as a new tab

        tabbedPane.addTab("Administrator", adminPanel);
        tabbedPane.addTab("Cleaning Staff", sobaricaPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void enrichAdminWithCleaningData(Administrator admin) {
        List<OsobljeZaCiscenje> cleaningStaff = csvHandler.loadOsobljeZaCiscenjeFromCSV(csvHandler.loadSobeFromCSV(), csvHandler.loadTimeBoundPriceListsFromCSV());

        for (OsobljeZaCiscenje staff : cleaningStaff) {
            if (staff.getUloga() == Uloga.ADMINISTRATOR && staff.getUsername().equals(admin.getUsername())) {
                // Merge the cleaning-specific fields into the admin object
                System.out.println("Enriching Admin with cleaning data...");
                admin.setSobeObaveze(staff.getSobeObaveze());
                admin.setCleaningDates(staff.getCleaningDates());
                admin.setTotalCleanedRoomsCounter(staff.getTotalCleanedRoomsCounter());

                // Debugging output
                System.out.println("Loaded sobe_obaveze for Admin: " + admin.getSobeObaveze());
                break; // Stop searching after finding the matching admin
            }
        }
    }

    private void enrichAdminWithGostData(Administrator admin) {
        System.out.println("Enriching Admin with guest data...");

        List<GlumiGosta> guests = csvHandler.loadGosteFromCSV();  // Load the guest data
        List<Rezervacija> allReservations = csvHandler.loadRezervacijeFromCSV(
            guests, 
            csvHandler.loadSobeFromCSV(), 
            csvHandler.loadTimeBoundPriceListsFromCSV().getPriceList(LocalDate.now())
        );

        List<Rezervacija> personalReservations = new ArrayList<>();
        for (Rezervacija rezervacija : allReservations) {
            if (rezervacija.getGost().getUsername().equals(admin.getUsername())) {
                personalReservations.add(rezervacija);
            }
        }

        // Set the personal reservations
        admin.setLicneRezervacije(personalReservations);

        // Update the bank account balance
        for (GlumiGosta guest : guests) {
            if (guest.getUsername().equals(admin.getUsername())) {
                admin.getRacun().setBalance(guest.getRacun().getBalance());
                System.out.println("Loaded personal bank balance for Admin: " + admin.getRacun().getBalance());
                break; // Stop searching after finding the matching guest
            }
        }

        System.out.println("Loaded personal reservations for Admin: " + admin.getLicneRezervacije());
    }

    private void loadRoomList() {
        roomPanel.removeAll(); // Clear previous content
        List<Soba> sobeObaveze = admin.getSobeObaveze();

        if (sobeObaveze != null && !sobeObaveze.isEmpty()) {
            for (Soba soba : sobeObaveze) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel roomLabel = new JLabel("Room " + soba.getBrojSobe() + " (" + soba.getTipSobe() + ")");
                JButton cleanButton = new JButton("Clean the room");

                cleanButton.addActionListener(e -> {
                    admin.zavrsiSredjivanjeSobe(soba);
                    cleanedRoomsCounterLabel.setText("Total Rooms Cleaned: " + admin.getTotalCleanedRoomsCounter());
                    loadRoomList(); // Refresh the list after cleaning a room
                    roomPanel.revalidate();
                    roomPanel.repaint();
                });

                panel.add(roomLabel);
                panel.add(cleanButton);
                roomPanel.add(panel);
            }
        } else {
            JLabel noRoomsLabel = new JLabel("No rooms assigned for cleaning.");
            roomPanel.add(noRoomsLabel);
        }

        roomPanel.revalidate();
        roomPanel.repaint();
    }

    // Add New Employee Dialog
    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog(this, "Add New Employee", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fields for the employee data
        JTextField nameField = new JTextField();
        JTextField surnameField = new JTextField();
        JComboBox<Uloga> roleBox = new JComboBox<>(Uloga.values());
        JComboBox<Pol> polBox = new JComboBox<>(Pol.values());
        JTextField dobField = new JTextField("yyyy-mm-dd");
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField balanceField = new JTextField();
        JTextField experienceField = new JTextField();
        JComboBox<Obrazovanje> educationBox = new JComboBox<>(Obrazovanje.values());

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Surname:"), gbc);
        gbc.gridx = 1;
        dialog.add(surnameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        dialog.add(roleBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        dialog.add(polBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        dialog.add(dobField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        dialog.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        dialog.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        dialog.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        dialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        dialog.add(new JLabel("Starting Balance:"), gbc);
        gbc.gridx = 1;
        dialog.add(balanceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        dialog.add(new JLabel("Experience:"), gbc);
        gbc.gridx = 1;
        dialog.add(experienceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        dialog.add(new JLabel("Education:"), gbc);
        gbc.gridx = 1;
        dialog.add(educationBox, gbc);

        // Buttons to add the employee or cancel
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Employee");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, gbc);

        // Action listeners for the buttons
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String surname = surnameField.getText();
                Uloga role = (Uloga) roleBox.getSelectedItem();
                Pol pol = (Pol) polBox.getSelectedItem();
                LocalDate dob = LocalDate.parse(dobField.getText());
                String phone = phoneField.getText();
                String address = addressField.getText();
                String username = usernameField.getText();
                String password = passwordField.getText();
                double balance = Double.parseDouble(balanceField.getText());
                double experience = Double.parseDouble(experienceField.getText());
                Obrazovanje education = (Obrazovanje) educationBox.getSelectedItem();
                
                Employee employee;

                // Instantiate the correct subclass based on the selected role
                switch (role) {
                    case ADMINISTRATOR:
                        employee = new Administrator(name, surname, pol, dob, phone, address, username, password, new LicniRacun(balance), education, "", experience, admin.getTimeBoundPriceList());
                        break;
                    case SOBARICA:
                        employee = new Sobarica(name, surname, pol, dob, phone, address, username, password, new LicniRacun(balance), education, "", experience, admin.getTimeBoundPriceList());
                        break;
                    case RECEPCIONER:
                        employee = new Recepcioner(name, surname, pol, dob, phone, address, username, password, new LicniRacun(balance), education, "", experience, admin.getTimeBoundPriceList());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid role selected");
                }

                admin.dodajZaposlenog(employee);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding employee. Please check the input fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // List All Employees
    private void showListEmployeesDialog() {
        JDialog dialog = new JDialog(this, "List of Employees", true);
        dialog.setLayout(new BorderLayout());

        String[] columnNames = {"Name", "Surname", "Role", "Date of Birth", "Phone", "Address", "Username", "Balance"};
        List<Employee> employees = admin.getZaposlene();
        String[][] data = new String[employees.size()][8];

        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            data[i][0] = employee.getIme();
            data[i][1] = employee.getPrezime();
            data[i][2] = employee.getUloga().name();
            data[i][3] = employee.getDatumRodjenja().toString();
            data[i][4] = employee.getBrojTelefona();
            data[i][5] = employee.getAdresa();
            data[i][6] = employee.getUsername();
            data[i][7] = String.format("%.2f", employee.getRacun().getBalance());
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Add New Price List Dialog
    private void showAddTimeBoundPriceListDialog() {
        JDialog dialog = new JDialog(this, "Add New Time-Bound Price List", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fields for the price list data
        JTextField baseSalaryField = new JTextField();
        JTextField experienceBonusField = new JTextField();
        JTextField loyaltyThresholdField = new JTextField();
        JTextField loyaltyDiscountField = new JTextField();
        JTextField startDateField = new JTextField("yyyy-mm-dd");

        // Education coefficients
        Map<Obrazovanje, JTextField> educationFields = new EnumMap<>(Obrazovanje.class);
        for (Obrazovanje obrazovanje : Obrazovanje.values()) {
            JTextField coefField = new JTextField();
            educationFields.put(obrazovanje, coefField);
        }

        // Room prices
        Map<TipSobe, JTextField> roomPriceFields = new EnumMap<>(TipSobe.class);
        for (TipSobe roomType : TipSobe.values()) {
            JTextField roomField = new JTextField();
            roomPriceFields.put(roomType, roomField);
        }

        // Amenity prices
        Map<Amenities, JTextField> amenityPriceFields = new EnumMap<>(Amenities.class);
        for (Amenities amenity : Amenities.values()) {
            JTextField amenityField = new JTextField();
            amenityPriceFields.put(amenity, amenityField);
        }

        // Adding components to the dialog
        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y++;
        dialog.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        dialog.add(startDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y++;
        dialog.add(new JLabel("Base Salary:"), gbc);
        gbc.gridx = 1;
        dialog.add(baseSalaryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y++;
        dialog.add(new JLabel("Experience Bonus:"), gbc);
        gbc.gridx = 1;
        dialog.add(experienceBonusField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y++;
        dialog.add(new JLabel("Loyalty Threshold:"), gbc);
        gbc.gridx = 1;
        dialog.add(loyaltyThresholdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y++;
        dialog.add(new JLabel("Loyalty Discount (%):"), gbc);
        gbc.gridx = 1;
        dialog.add(loyaltyDiscountField, gbc);

        // Education coefficients
        for (Map.Entry<Obrazovanje, JTextField> entry : educationFields.entrySet()) {
            gbc.gridx = 0;
            gbc.gridy = y++;
            dialog.add(new JLabel(entry.getKey().toString() + " Coefficient:"), gbc);
            gbc.gridx = 1;
            dialog.add(entry.getValue(), gbc);
        }

        // Room prices
        for (Map.Entry<TipSobe, JTextField> entry : roomPriceFields.entrySet()) {
            gbc.gridx = 0;
            gbc.gridy = y++;
            dialog.add(new JLabel(entry.getKey().toString() + " Price:"), gbc);
            gbc.gridx = 1;
            dialog.add(entry.getValue(), gbc);
        }

        // Amenity prices
        for (Map.Entry<Amenities, JTextField> entry : amenityPriceFields.entrySet()) {
            gbc.gridx = 0;
            gbc.gridy = y++;
            dialog.add(new JLabel(entry.getKey().toString() + " Price:"), gbc);
            gbc.gridx = 1;
            dialog.add(entry.getValue(), gbc);
        }

        // Buttons to add the price list or cancel
        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Price List");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, gbc);

        // Action listeners for the buttons
        addButton.addActionListener(e -> {
            try {
                double baseSalary = Double.parseDouble(baseSalaryField.getText());
                double experienceBonus = Double.parseDouble(experienceBonusField.getText());
                double loyaltyThreshold = Double.parseDouble(loyaltyThresholdField.getText());
                double loyaltyDiscount = Double.parseDouble(loyaltyDiscountField.getText());
                LocalDate startDate = LocalDate.parse(startDateField.getText());

                // Collect education coefficients
                Map<Obrazovanje, Double> educationCoefficients = new EnumMap<>(Obrazovanje.class);
                for (Map.Entry<Obrazovanje, JTextField> entry : educationFields.entrySet()) {
                    educationCoefficients.put(entry.getKey(), Double.parseDouble(entry.getValue().getText()));
                }

                // Collect room prices
                Map<TipSobe, Double> roomPrices = new EnumMap<>(TipSobe.class);
                for (Map.Entry<TipSobe, JTextField> entry : roomPriceFields.entrySet()) {
                    roomPrices.put(entry.getKey(), Double.parseDouble(entry.getValue().getText()));
                }

                // Collect amenity prices
                Map<Amenities, Double> amenityPrices = new EnumMap<>(Amenities.class);
                for (Map.Entry<Amenities, JTextField> entry : amenityPriceFields.entrySet()) {
                    amenityPrices.put(entry.getKey(), Double.parseDouble(entry.getValue().getText()));
                }

                Cenovnik newCenovnik = new Cenovnik(baseSalary, experienceBonus, educationCoefficients, roomPrices, amenityPrices, loyaltyThreshold, loyaltyDiscount);
                admin.getTimeBoundPriceList().addPriceList(startDate, newCenovnik);

                // Add the new price list to the CSV file
                csvHandler.writeTimeBoundPriceListToCSV(startDate, newCenovnik);

                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Price List added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding price list. Please check the input fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Show Statistics Dialog
    private void showStatisticsDialog() {
        JDialog dialog = new JDialog(this, "Statistics", true);
        dialog.setLayout(new BorderLayout());

        JPanel statisticsPanel = new JPanel();
        statisticsPanel.setLayout(new GridLayout(2, 2)); // 2x2 grid for the 4 graphics

        // Buttons for different time periods
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton lastMonthButton = new JButton("Last Month");
        JButton lastYearButton = new JButton("Last Year");
        JButton totalButton = new JButton("Total");

        buttonPanel.add(lastMonthButton);
        buttonPanel.add(lastYearButton);
        buttonPanel.add(totalButton);

        dialog.add(statisticsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Load the appropriate statistics graphics based on the button pressed
        lastMonthButton.addActionListener(e -> {
            statisticsPanel.removeAll();
            //statisticsPanel.add(admin.getLastMonthStatistics());
            statisticsPanel.revalidate();
            statisticsPanel.repaint();
        });

        lastYearButton.addActionListener(e -> {
            statisticsPanel.removeAll();
            //statisticsPanel.add(admin.getLastYearStatistics());
            statisticsPanel.revalidate();
            statisticsPanel.repaint();
        });

        totalButton.addActionListener(e -> {
            statisticsPanel.removeAll();
            //statisticsPanel.add(admin.getTotalStatistics());
            statisticsPanel.revalidate();
            statisticsPanel.repaint();
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        CSVHandler csvHandlerMain = new CSVHandler();
        List<OsobljeZaCiscenje> osoblje = csvHandlerMain.loadOsobljeZaCiscenjeFromCSV(csvHandlerMain.loadSobeFromCSV(), csvHandlerMain.loadTimeBoundPriceListsFromCSV());

        // Example: Find or create an Administrator instance
        Administrator admin = null;
        for (OsobljeZaCiscenje osoba : osoblje) {
            if (osoba.getUloga() == Uloga.ADMINISTRATOR) {
                admin = (Administrator) osoba;
                break;
            }
        }

        if (admin == null) {
            System.out.println("No Administrator found.");
            return;
        }

        final Administrator finalAdmin = admin;
        SwingUtilities.invokeLater(() -> {
            AdministratorFrame frame = new AdministratorFrame(finalAdmin);
            frame.setVisible(true);
        });
    }
}
