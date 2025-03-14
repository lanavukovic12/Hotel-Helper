package gui;

import javax.swing.*;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import csv.CSVHandler;
import enumeratori.Amenities;
import enumeratori.Pol;
import enumeratori.StanjeRezervacije;
import enumeratori.StanjeSobe;
import enumeratori.TipSobe;
import interfejsi.GlumiGosta;
import interfejsi.GlumiRecepcionera;
import uloge.Administrator;
import uloge.Employee;
import uloge.Gost;
import uloge.Recepcioner;
import usluge.Cenovnik;
import usluge.LicniRacun;
import usluge.RacunHotela;
import usluge.Rezervacija;
import usluge.Soba;
import usluge.TimeBoundPriceList;

public class RecepcionerPanel extends JPanel {
    private GlumiRecepcionera recepcioner;
    private List<GlumiGosta> guestList;
    private List<Soba> roomList;
    private List<Rezervacija> reservationList;
    private RacunHotela hotelAccount;
    private CSVHandler csvHandler;
    private JPanel listPanel;
    private JPanel filterPanel; // Unified filter panel area
    
    private JPanel buttonPanel; // Keep a reference to the button panel

    private JComboBox<TipSobe> roomTypeComboBox;
    private JComboBox<StanjeSobe> roomStatusComboBox;
    private JTextField maxPriceField;

    private JDateChooser checkInChooser;
    private JDateChooser checkOutChooser;
    private JComboBox<TipSobe> reservationRoomTypeComboBox;
    private JComboBox<StanjeRezervacije> reservationStatusComboBox;
    private JCheckBox filterByPriceCheckBox;
    private List<JCheckBox> amenitiesCheckBoxes;

    public RecepcionerPanel(GlumiRecepcionera recepcioner, RacunHotela hotelAccount) {
        this.hotelAccount = hotelAccount;
        this.recepcioner = recepcioner;
        this.csvHandler = new CSVHandler();

        // Load the guest, room, and reservation lists
        this.guestList = csvHandler.loadGosteFromCSV();
        this.roomList = csvHandler.loadSobeFromCSV();
        this.reservationList = csvHandler.loadRezervacijeFromCSV(guestList, roomList, csvHandler.loadTimeBoundPriceListsFromCSV().getPriceList(LocalDate.now()));
        this.recepcioner.setGoste(guestList);
        this.recepcioner.setSobe(roomList);

        setLayout(new BorderLayout());

        // Create top panel with labels and buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JLabel greetingLabel = new JLabel("Dear Recepcionist " + recepcioner.getUsername() + ", welcome!");
        JButton addGuestButton = new JButton("Add a new guest");
        JButton showGuestListButton = new JButton("Show list of guests");
        JButton showRoomListButton = new JButton("Show list of all rooms");
        JButton showReservationListButton = new JButton("Show list of all reservations");
        JLabel balanceLabel = new JLabel("Trenutno stanje na racunu: " + recepcioner.getRacun().getBalance());

        // Add components in the correct order
        topPanel.add(greetingLabel);
        topPanel.add(addGuestButton);
        topPanel.add(showGuestListButton);
        topPanel.add(showRoomListButton);
        topPanel.add(showReservationListButton);
        topPanel.add(balanceLabel);
        add(topPanel, BorderLayout.NORTH);

        // Main panel that holds the filter panel and the list panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        filterPanel = new JPanel(); // This will be swapped based on the selected list
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // Create the button panel but do not add it yet
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton makeReservationButton = new JButton("Make New Reservation");
        JButton confirmCancelReservationButton = new JButton("Confirm/Cancel Reservation");
        JButton checkInOutButton = new JButton("Check-In/Check-Out");

        buttonPanel.add(makeReservationButton);
        buttonPanel.add(confirmCancelReservationButton);
        buttonPanel.add(checkInOutButton);

        // Event listeners
        addGuestButton.addActionListener(e -> showAddGuestDialog());
        showGuestListButton.addActionListener(e -> showGuestList());
        showRoomListButton.addActionListener(e -> showRoomList());
        showReservationListButton.addActionListener(e -> showReservationList());

        // Button listeners
        makeReservationButton.addActionListener(e -> showMakeReservationDialog());
        confirmCancelReservationButton.addActionListener(e -> showConfirmCancelReservationDialog());
        checkInOutButton.addActionListener(e -> showCheckInOutDialog());

        // Show the guest list initially
        showGuestList();
    }

    private void showAddGuestDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        // If parentFrame is null, fallback to a new JFrame
        JDialog dialog = new JDialog(parentFrame != null ? parentFrame : new JFrame(), "Add New Guest", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create the input fields
        JTextField nameField = new JTextField();
        JTextField surnameField = new JTextField();
        JComboBox<Pol> genderBox = new JComboBox<>(Pol.values());
        JTextField dobField = new JTextField("yyyy-mm-dd");
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();

        // Add the labels and fields to the dialog
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
        dialog.add(new JLabel("Gender:"), gbc);

        gbc.gridx = 1;
        dialog.add(genderBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Date of Birth:"), gbc);

        gbc.gridx = 1;
        dialog.add(dobField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Phone Number:"), gbc);

        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        dialog.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        dialog.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        dialog.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        dialog.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        dialog.add(passwordField, gbc);

        // Add buttons at the bottom
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add New Guest");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, gbc);

        // Action listeners for the buttons
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String surname = surnameField.getText();
                Pol gender = (Pol) genderBox.getSelectedItem();
                LocalDate dob = LocalDate.parse(dobField.getText());
                String phone = phoneField.getText();
                String address = addressField.getText();
                String username = usernameField.getText();
                String password = passwordField.getText();
                GlumiGosta newGuest = new Gost(name,
                                     surname,
                                     gender,
                                     dob,
                                     phone,
                                     address,
                                     username,
                                     password,
                                     new LicniRacun(0.0)
                                     );
                recepcioner.dodajGosta(newGuest);
                dialog.dispose();
                showSuccessDialog("Uspeh");
                showGuestList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding guest. Please check the input fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showGuestList() {
        listPanel.removeAll();
        filterPanel.setVisible(false); // Hide filters when showing guests

        String[] columnNames = {"Ime", "Prezime", "Pol", "Datum Rođenja", "Broj Telefona", "Adresa", "Username", "Password", "Stanje na Racunu", "Uloga"};
        List<String[]> rowData = new ArrayList<>();

        for (GlumiGosta guest : guestList) {
            String[] row = new String[columnNames.length];

            if (guest instanceof Gost) {
                Gost gost = (Gost) guest;
                row[0] = gost.getIme();
                row[1] = gost.getPrezime();
                row[2] = gost.getPol().toString();
                row[3] = gost.getDatumRodjenja().toString();
                row[4] = gost.getBrojTelefona();
                row[5] = gost.getAdresa();
                row[6] = gost.getUsername();
                row[7] = gost.getPassword();
                row[8] = String.format("%.2f", gost.getRacun().getBalance());
                row[9] = gost.getUloga().toString();
            } else if (guest instanceof Administrator) {
                Administrator admin = (Administrator) guest;
                row[0] = admin.getIme();
                row[1] = admin.getPrezime();
                row[2] = admin.getPol().toString();
                row[3] = admin.getDatumRodjenja().toString();
                row[4] = admin.getBrojTelefona();
                row[5] = admin.getAdresa();
                row[6] = admin.getUsername();
                row[7] = admin.getPassword();
                row[8] = String.format("%.2f", admin.getRacun().getBalance());
                row[9] = admin.getUloga().toString();
            }

            rowData.add(row);
        }

        // Convert the list to an array for the table
        String[][] data = rowData.toArray(new String[0][]);
        JTable table = new JTable(data, columnNames);

        JScrollPane scrollPane = new JScrollPane(table);
        listPanel.setLayout(new BorderLayout());
        listPanel.add(scrollPane, BorderLayout.CENTER);

        listPanel.revalidate();
        listPanel.repaint();
    }

    private void showRoomList() {
        setupRoomFilterPanel();  // Setup and display the room filter panel

        // Reset all room filter fields to their default/empty states
        roomTypeComboBox.setSelectedItem(null);  // Clear room type filter
        roomStatusComboBox.setSelectedItem(null);  // Clear room status filter
        maxPriceField.setText("");  // Clear max price filter

        filterRoomList();         // Filter and display the list of rooms

        // Remove button panel if it's currently added
        remove(buttonPanel);
    }

    private void updateRoomTable(List<Soba> rooms) {
        listPanel.removeAll();

        String[] columnNames = {"Room Number", "Room Type", "Room Status", "Price"};
        List<String[]> rowData = new ArrayList<>();

        for (Soba room : rooms) {
            String[] row = new String[columnNames.length];
            row[0] = String.valueOf(room.getBrojSobe());
            row[1] = room.getTipSobe().getOpis();
            row[2] = room.getStanjeSobe().toString();
            row[3] = String.format("%.2f", csvHandler.loadTimeBoundPriceListsFromCSV().getPriceList(LocalDate.now()).getRoomPrice(room.getTipSobe()));

            rowData.add(row);
        }

        String[][] data = rowData.toArray(new String[0][]);
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        listPanel.setLayout(new BorderLayout());
        listPanel.add(scrollPane, BorderLayout.CENTER);

        listPanel.revalidate();
        listPanel.repaint();
    }

    private void showReservationList() {
        // Reload the reservations from the CSV to ensure the latest data is reflected
        this.reservationList = csvHandler.loadRezervacijeFromCSV(guestList, roomList, csvHandler.loadTimeBoundPriceListsFromCSV().getPriceList(LocalDate.now()));

        setupReservationFilterPanel();  // Setup and display the reservation filter panel

        // Reset all reservation filter fields to their default/empty states
        checkInChooser.setDate(null);  // Clear check-in date filter
        checkOutChooser.setDate(null);  // Clear check-out date filter
        reservationRoomTypeComboBox.setSelectedItem(null);  // Clear room type filter
        reservationStatusComboBox.setSelectedItem(null);  // Clear reservation status filter
        filterByPriceCheckBox.setSelected(false);  // Clear filter by price checkbox

        for (JCheckBox checkBox : amenitiesCheckBoxes) {
            checkBox.setSelected(false);  // Clear all amenities checkboxes
        }

        filterReservationList();  // Filter and display the list of reservations without any pre-applied filters

        // Ensure the button panel is added below the reservation list
        add(buttonPanel, BorderLayout.SOUTH);

        listPanel.revalidate();
        listPanel.repaint();
    }

    private void updateReservationTable(List<Rezervacija> reservations) {
        listPanel.removeAll();

        String[] columnNames = {"Guest Username", "Room Number", "Check-In Date", "Check-Out Date", "Status", "Total Price"};
        List<String[]> rowData = new ArrayList<>();

        for (Rezervacija reservation : reservations) {
            String[] row = new String[columnNames.length];
            row[0] = reservation.getGost().getUsername();
            row[1] = String.valueOf(reservation.getSoba().getBrojSobe());
            row[2] = reservation.getCheckInDate().toString();
            row[3] = reservation.getCheckOutDate().toString();
            row[4] = reservation.getStanjeRezervacije().toString();
            row[5] = String.format("%.2f", reservation.getTotalPrice());

            rowData.add(row);
        }

        String[][] data = rowData.toArray(new String[0][]);
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        listPanel.setLayout(new BorderLayout());
        listPanel.add(scrollPane, BorderLayout.CENTER);

        listPanel.revalidate();
        listPanel.repaint();
    }

    private void setupRoomFilterPanel() {
        filterPanel.removeAll(); // Clear the panel before setting up room filters
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Rooms"));

        roomTypeComboBox = new JComboBox<>(TipSobe.values());
        roomStatusComboBox = new JComboBox<>(StanjeSobe.values());
        maxPriceField = new JTextField(10);

        filterPanel.add(new JLabel("Room Type:"));
        filterPanel.add(roomTypeComboBox);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(new JLabel("Room Status:"));
        filterPanel.add(roomStatusComboBox);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(new JLabel("Max Price:"));
        filterPanel.add(maxPriceField);
        filterPanel.add(Box.createVerticalStrut(10));

        JButton filterButton = new JButton("Filter Rooms");
        JButton resetButton = new JButton("Reset Filters");
        filterButton.addActionListener(e -> filterRoomList());
        resetButton.addActionListener(e -> resetRoomFilters());

        filterPanel.add(filterButton);
        filterPanel.add(resetButton);

        add(filterPanel, BorderLayout.WEST);
        filterPanel.setVisible(true);
        revalidate();
        repaint();
    }

    private void setupReservationFilterPanel() {
        filterPanel.removeAll(); // Clear the panel before setting up reservation filters
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Reservations"));

        checkInChooser = new JDateChooser();
        checkOutChooser = new JDateChooser();
        reservationRoomTypeComboBox = new JComboBox<>(TipSobe.values());
        reservationStatusComboBox = new JComboBox<>(StanjeRezervacije.values());
        filterByPriceCheckBox = new JCheckBox("Filter by Price?");

        filterPanel.add(new JLabel("Check-In Date:"));
        filterPanel.add(checkInChooser);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(new JLabel("Check-Out Date:"));
        filterPanel.add(checkOutChooser);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(new JLabel("Room Type:"));
        filterPanel.add(reservationRoomTypeComboBox);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(new JLabel("Reservation Status:"));
        filterPanel.add(reservationStatusComboBox);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(filterByPriceCheckBox);
        filterPanel.add(Box.createVerticalStrut(10));

        amenitiesCheckBoxes = new ArrayList<>();
        filterPanel.add(new JLabel("Amenities:"));
        for (Amenities amenity : Amenities.values()) {
            JCheckBox checkBox = new JCheckBox(amenity.getOpis());
            amenitiesCheckBoxes.add(checkBox);
            filterPanel.add(checkBox);
        }

        JButton filterButton = new JButton("Filter Reservations");
        JButton resetButton = new JButton("Reset Filters");
        filterButton.addActionListener(e -> filterReservationList());
        resetButton.addActionListener(e -> resetReservationFilters());

        filterPanel.add(filterButton);
        filterPanel.add(resetButton);

        add(filterPanel, BorderLayout.WEST);
        filterPanel.setVisible(true);
        revalidate();
        repaint();
    }

    private void filterRoomList() {
        listPanel.removeAll();

        String[] columnNames = {"Broj Sobe", "Tip Sobe", "Stanje Sobe"};
        List<String[]> rowData = new ArrayList<>();

        TipSobe selectedTipSobe = (TipSobe) roomTypeComboBox.getSelectedItem();
        StanjeSobe selectedStanjeSobe = (StanjeSobe) roomStatusComboBox.getSelectedItem();
        String maxPriceText = maxPriceField.getText();
        Double maxPrice = maxPriceText.isEmpty() ? null : Double.parseDouble(maxPriceText);

        for (Soba soba : recepcioner.filterRooms(roomList, selectedStanjeSobe, selectedTipSobe, maxPrice, csvHandler.loadTimeBoundPriceListsFromCSV().getPriceList(LocalDate.now()))) {
            String[] row = new String[columnNames.length];
            row[0] = String.valueOf(soba.getBrojSobe());
            row[1] = soba.getTipSobe().getOpis();
            row[2] = soba.getStanjeSobe().toString();

            rowData.add(row);
        }

        String[][] data = rowData.toArray(new String[0][]);
        JTable table = new JTable(data, columnNames);

        JScrollPane scrollPane = new JScrollPane(table);
        listPanel.setLayout(new BorderLayout());
        listPanel.add(scrollPane, BorderLayout.CENTER);

        listPanel.revalidate();
        listPanel.repaint();
    }

    private void filterReservationList() {
        listPanel.removeAll();

        String[] columnNames = {"Guest Name", "Room Number", "Check-In Date", "Check-Out Date", "Status", "Total Price"};
        List<String[]> rowData = new ArrayList<>();

        LocalDate checkInDate = checkInChooser.getDate() != null ? checkInChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate checkOutDate = checkOutChooser.getDate() != null ? checkOutChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null;
        TipSobe selectedTipSobe = (TipSobe) reservationRoomTypeComboBox.getSelectedItem();
        StanjeRezervacije selectedStanjeRezervacije = (StanjeRezervacije) reservationStatusComboBox.getSelectedItem();
        boolean sortByPrice = filterByPriceCheckBox.isSelected();
        List<Amenities> selectedAmenities = new ArrayList<>();
        for (JCheckBox checkBox : amenitiesCheckBoxes) {
            if (checkBox.isSelected()) {
                selectedAmenities.add(csvHandler.findAmenitiesByOpis(checkBox.getText()));
            }
        }

        // Filtering the reservations
        List<Rezervacija> filteredReservations = new ArrayList<>();
        for (Rezervacija rezervacija : reservationList) {
            if (checkInDate != null && checkOutDate != null && (rezervacija.getCheckInDate().isBefore(checkInDate) || rezervacija.getCheckOutDate().isAfter(checkOutDate))) {
                continue;
            }
            if (selectedTipSobe != null && rezervacija.getSoba().getTipSobe() != selectedTipSobe) {
                continue;
            }
            if (selectedStanjeRezervacije != null && rezervacija.getStanjeRezervacije() != selectedStanjeRezervacije) {
                continue;
            }
            if (!selectedAmenities.isEmpty() && !rezervacija.getAmenities().containsAll(selectedAmenities)) {
                continue;
            }
            filteredReservations.add(rezervacija);
        }

        // Sorting by total price if the option is selected
        if (sortByPrice) {
            filteredReservations.sort((r1, r2) -> Double.compare(r1.getTotalPrice(), r2.getTotalPrice()));
        }

        // Populating the table with filtered (and possibly sorted) data
        for (Rezervacija rezervacija : filteredReservations) {
            String[] row = new String[columnNames.length];
            row[0] = rezervacija.getGost().getIme();
            row[1] = String.valueOf(rezervacija.getSoba().getBrojSobe());
            row[2] = rezervacija.getCheckInDate().toString();
            row[3] = rezervacija.getCheckOutDate().toString();
            row[4] = rezervacija.getStanjeRezervacije().toString();
            row[5] = String.format("%.2f", rezervacija.getTotalPrice());

            rowData.add(row);
        }

        String[][] data = rowData.toArray(new String[0][]);
        JTable table = new JTable(data, columnNames);

        JScrollPane scrollPane = new JScrollPane(table);
        listPanel.setLayout(new BorderLayout());
        listPanel.add(scrollPane, BorderLayout.CENTER);

        listPanel.revalidate();
        listPanel.repaint();
    }

    private void resetRoomFilters() {
        roomTypeComboBox.setSelectedItem(null);
        roomStatusComboBox.setSelectedItem(null);
        maxPriceField.setText("");
        filterRoomList();
    }

    private void resetReservationFilters() {
        checkInChooser.setDate(null);
        checkOutChooser.setDate(null);
        reservationRoomTypeComboBox.setSelectedItem(null);
        reservationStatusComboBox.setSelectedItem(null);
        filterByPriceCheckBox.setSelected(false);
        for (JCheckBox checkBox : amenitiesCheckBoxes) {
            checkBox.setSelected(false);
        }
        filterReservationList();
    }

    private void showMakeReservationDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        // If parentFrame is null, fallback to a new JFrame
        JDialog dialog = new JDialog(parentFrame != null ? parentFrame : new JFrame(), "Make New Reservation", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Guest selection dropdown
        JComboBox<GlumiGosta> guestComboBox = new JComboBox<>();
        for (GlumiGosta guest : guestList) {
            guestComboBox.addItem(guest); // Add the actual GlumiGosta object to the JComboBox
        }
        // Custom renderer to display name and username in the dropdown
        guestComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof GlumiGosta) {
                    GlumiGosta guest = (GlumiGosta) value;
                    label.setText(guest.getIme() + " (" + guest.getUsername() + ")");
                }
                return label;
            }
        });

        // Room selection dropdown
        JComboBox<Soba> roomComboBox = new JComboBox<>();
        for (Soba room : roomList) {
            if (room.getStanjeSobe() == StanjeSobe.AVAILABLE) {
                roomComboBox.addItem(room);
            }
        }
        // Custom renderer to display room information in the dropdown
        roomComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Soba) {
                    Soba room = (Soba) value;
                    label.setText("Room " + room.getBrojSobe() + " - " + room.getTipSobe().getOpis() + " (" + room.getStanjeSobe() + ")");
                }
                return label;
            }
        });

        // Amenities checkboxes
        List<JCheckBox> amenitiesCheckBoxes = new ArrayList<>();
        for (Amenities amenity : Amenities.values()) {
            JCheckBox checkBox = new JCheckBox(amenity.getOpis());
            amenitiesCheckBoxes.add(checkBox);
        }

        // Date choosers for check-in and check-out dates
        JDateChooser checkInChooser = new JDateChooser();
        JDateChooser checkOutChooser = new JDateChooser();

        // Add components to the dialog
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Guest:"), gbc);

        gbc.gridx = 1;
        dialog.add(guestComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Room:"), gbc);

        gbc.gridx = 1;
        dialog.add(roomComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Check-In Date:"), gbc);

        gbc.gridx = 1;
        dialog.add(checkInChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Check-Out Date:"), gbc);

        gbc.gridx = 1;
        dialog.add(checkOutChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Amenities:"), gbc);

        gbc.gridx = 1;
        JPanel amenitiesPanel = new JPanel();
        amenitiesPanel.setLayout(new GridLayout(amenitiesCheckBoxes.size(), 1));
        for (JCheckBox checkBox : amenitiesCheckBoxes) {
            amenitiesPanel.add(checkBox);
        }
        dialog.add(amenitiesPanel, gbc);

        // Buttons for creating the reservation or canceling
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createReservationButton = new JButton("Create Reservation");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(createReservationButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, gbc);

        // Action listeners for the buttons
        createReservationButton.addActionListener(e -> {
            try {
                GlumiGosta selectedGuest = (GlumiGosta) guestComboBox.getSelectedItem();
                Soba selectedRoom = (Soba) roomComboBox.getSelectedItem();
                LocalDate checkInDate = checkInChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalDate checkOutDate = checkOutChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                List<Amenities> selectedAmenities = new ArrayList<>();
                for (JCheckBox checkBox : amenitiesCheckBoxes) {
                    if (checkBox.isSelected()) {
                        selectedAmenities.add(csvHandler.findAmenitiesByOpis(checkBox.getText()));
                    }
                }

                if (selectedGuest == null || selectedRoom == null || checkInDate == null || checkOutDate == null) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Cenovnik cenovnik = csvHandler.loadTimeBoundPriceListsFromCSV().getPriceList(LocalDate.now());

                recepcioner.napraviRezervacijuNekome(selectedGuest, selectedRoom, checkInDate, checkOutDate, selectedAmenities, cenovnik, hotelAccount);

                dialog.dispose();
                showSuccessDialog("Reservation created successfully.");
                showReservationList(); // Refresh the reservation list
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error creating reservation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private void showConfirmCancelReservationDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Confirm/Cancel Reservation", true);

        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Filter the reservations to show only those with the PAYED_FOR status
        List<Rezervacija> payedForReservations = new ArrayList<>();
        for (Rezervacija rezervacija : reservationList) {
            if (rezervacija.getStanjeRezervacije() == StanjeRezervacije.PAYED_FOR) {
                payedForReservations.add(rezervacija);
            }
        }

        // Reservation ComboBox with only PAYED_FOR reservations
        JComboBox<Rezervacija> reservationComboBox = new JComboBox<>(payedForReservations.toArray(new Rezervacija[0]));
        reservationComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Rezervacija) {
                    Rezervacija rezervacija = (Rezervacija) value;
                    label.setText(rezervacija.getGost().getUsername() + " - Room: " + rezervacija.getSoba().getBrojSobe());
                }
                return label;
            }
        });

        // Status ComboBox limited to "Confirmed" and "Cancelled"
        JComboBox<StanjeRezervacije> statusComboBox = new JComboBox<>(new StanjeRezervacije[]{StanjeRezervacije.CONFIRMED, StanjeRezervacije.CANCELLED});

        // Add components to the dialog
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Reservation:"), gbc);

        gbc.gridx = 1;
        dialog.add(reservationComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("New Status:"), gbc);

        gbc.gridx = 1;
        dialog.add(statusComboBox, gbc);

        // Buttons for confirming/cancelling the reservation
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton("Confirm/Cancel");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, gbc);

        // Action listeners for the buttons
        confirmButton.addActionListener(e -> {
            try {
                Rezervacija selectedReservation = (Rezervacija) reservationComboBox.getSelectedItem();
                StanjeRezervacije selectedStatus = (StanjeRezervacije) statusComboBox.getSelectedItem();

                // Use Recepcioner's method to change the reservation status
                recepcioner.promeniStanjeRezervacije(selectedReservation, selectedStatus, hotelAccount);
                showSuccessDialog("Reservation status updated successfully!");
                dialog.dispose();
                showReservationList(); // Refresh the reservation list
            } catch (Exception ex) {
                showErrorDialog("Error updating reservation status: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private void showCheckInOutDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Check-In/Check-Out Guest", true);

        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Add padding for better spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Filter to show only reservations that are CONFIRMED
        List<Rezervacija> confirmedReservations = new ArrayList<>();
        for (Rezervacija rezervacija : reservationList) {
            if (rezervacija.getStanjeRezervacije() == StanjeRezervacije.CONFIRMED) {
                confirmedReservations.add(rezervacija);
            }
        }

        JComboBox<Rezervacija> reservationComboBox = new JComboBox<>(confirmedReservations.toArray(new Rezervacija[0]));
        reservationComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Rezervacija) {
                    Rezervacija rezervacija = (Rezervacija) value;
                    label.setText("Guest: " + rezervacija.getGost().getUsername() + " | Room: " + rezervacija.getSoba().getBrojSobe() + 
                                  " | Check-In: " + rezervacija.getCheckInDate() + " | Check-Out: " + rezervacija.getCheckOutDate());
                }
                return label;
            }
        });

        // Add components to the dialog
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Select Reservation:"), gbc);

        gbc.gridx = 1;
        dialog.add(reservationComboBox, gbc);

        // Buttons for Check-In, Check-Out, and Cancel
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton checkInButton = new JButton("Check-In");
        JButton checkOutButton = new JButton("Check-Out");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(checkInButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, gbc);

        // Action listeners for the buttons
        checkInButton.addActionListener(e -> {
            try {
                Rezervacija selectedReservation = (Rezervacija) reservationComboBox.getSelectedItem();
                recepcioner.checkIn(selectedReservation);
                showSuccessDialog("Guest checked in successfully!");
                dialog.dispose();
                showReservationList(); // Refresh the reservation list
            } catch (Exception ex) {
                showErrorDialog("Error during check-in: " + ex.getMessage());
            }
        });

        checkOutButton.addActionListener(e -> {
            try {
                Rezervacija selectedReservation = (Rezervacija) reservationComboBox.getSelectedItem();
                recepcioner.checkOut(selectedReservation);
                showSuccessDialog("Guest checked out successfully!");
                dialog.dispose();
                showReservationList(); // Refresh the reservation list
            } catch (Exception ex) {
                showErrorDialog("Error during check-out: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RacunHotela hotelAccount = new RacunHotela(500000.0);
            CSVHandler csvHandler = new CSVHandler();
            TimeBoundPriceList timeBoundPriceList = csvHandler.loadTimeBoundPriceListsFromCSV();


            List<Employee> employees = csvHandler.loadEmployeesFromCSV(timeBoundPriceList);
            Recepcioner recepcioner = null;
            for (Employee employee : employees) {
                if (employee instanceof Recepcioner && employee.getUsername().equals("recepcioner1")) {
                    recepcioner = (Recepcioner) employee;
                    break;
                }
            }

            if (recepcioner != null) {
                RecepcionerPanel panel = new RecepcionerPanel(recepcioner, hotelAccount);
                JFrame frame = new JFrame("Recepcioner Panel");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(panel);
                frame.setSize(1400, 800);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } else {
                System.out.println("Recepcioner with username 'recepcioner1' not found.");
            }
        });
    }
}