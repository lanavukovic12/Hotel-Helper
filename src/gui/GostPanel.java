package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import com.toedter.calendar.JDateChooser;

import csv.CSVHandler;
import enumeratori.Amenities;
import enumeratori.StanjeRezervacije;
import enumeratori.StanjeSobe;
import enumeratori.TipSobe;
import uloge.Gost;
import usluge.Cenovnik;
import usluge.RacunHotela;
import usluge.Rezervacija;
import usluge.Soba;
import interfejsi.GlumiGosta;

public class GostPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JLabel balanceLabel;
    private GlumiGosta gost;

    private List<Soba> availableRooms;
    private Cenovnik cenovnik;
    private CSVHandler csvHandler;
    private List<Rezervacija> ukupneRezervacije;
    private RacunHotela hotelAccount;

    public GostPanel(GlumiGosta gost,
    				 List<Soba> availableRooms,
    				 Cenovnik cenovnik,
    				 List<Rezervacija> ukupneRezervacije,
    				 RacunHotela hotelAccount
    				 ) 
    {
        this.csvHandler = new CSVHandler();
        this.gost = gost;
        this.availableRooms = availableRooms;
        this.cenovnik = cenovnik;
        this.ukupneRezervacije = ukupneRezervacije;
        this.hotelAccount = hotelAccount;

        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        // Add the welcome message label
        JLabel welcomeLabel = new JLabel("Dear Guest, " + gost.getIme() + ", welcome!");
        topPanel.add(welcomeLabel);  // Add this line

        JButton myReservationsButton = new JButton("Moje Rezervacije");
        JButton makeReservationButton = new JButton("Napravi Rezervaciju");
        JButton addMoneyButton = new JButton("Popuni Licni Racun");
        balanceLabel = new JLabel("Tekuce Stanje na Racunu: " + gost.getRacun().getBalance());

        topPanel.add(myReservationsButton);
        topPanel.add(makeReservationButton);
        topPanel.add(addMoneyButton);
        topPanel.add(balanceLabel);

        // Panels for CardLayout
        JPanel myReservationsPanel = createMyReservationsPanel();
        JPanel makeReservationPanel = createMakeReservationPanel();

        cardPanel.add(myReservationsPanel, "Moje Rezervacije");
        cardPanel.add(makeReservationPanel, "Napravi Rezervaciju");

        // Adding action listeners to buttons
        myReservationsButton.addActionListener(e -> cardLayout.show(cardPanel, "Moje Rezervacije"));
        makeReservationButton.addActionListener(e -> cardLayout.show(cardPanel, "Napravi Rezervaciju"));
        addMoneyButton.addActionListener(e -> showAddMoneyDialog());

        add(topPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
    }
    
    private JPanel createMyReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Left side with filters
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JDateChooser startDateChooser = new JDateChooser();
        JDateChooser endDateChooser = new JDateChooser();
        
        JComboBox<Object> roomTypeComboBox = new JComboBox<>();
        roomTypeComboBox.addItem("Show All"); // Add "Show All" option first

	     // Loop through the TipSobe enum and add each value to the combo box
	    for (TipSobe tipSobe : TipSobe.values()) {
	        roomTypeComboBox.addItem(tipSobe);
	    }
	    
        JComboBox<Object> statusComboBox = new JComboBox<>();
        statusComboBox.addItem("Show All"); // Add "Show All" option first

	     // Loop through the TipSobe enum and add each value to the combo box
	     for (StanjeRezervacije stanje : StanjeRezervacije.values()) {
	         statusComboBox.addItem(stanje);
	     }
        
        JCheckBox filterByPriceCheckBox = new JCheckBox("Filter by Price?");
        JButton filterButton = new JButton("Filter");
        JButton resetButton = new JButton("Reset Filters");

        filterPanel.add(new JLabel("Check-In Date:"));
        filterPanel.add(startDateChooser);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(new JLabel("Check-Out Date:"));
        filterPanel.add(endDateChooser);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(new JLabel("Tip Sobe:"));
        filterPanel.add(roomTypeComboBox);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(new JLabel("Stanje Rezervacije:"));
        filterPanel.add(statusComboBox);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(filterByPriceCheckBox);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(new JLabel("Amenities:"));
        List<JCheckBox> amenitiesCheckBoxes = addAmenitiesCheckboxes(filterPanel);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(filterButton);
        filterPanel.add(resetButton); // Add the reset button to the filter panel

        // Right side with reservation list and total money spent
        JPanel reservationsPanel = new JPanel();
        reservationsPanel.setLayout(new BoxLayout(reservationsPanel, BoxLayout.Y_AXIS));
        reservationsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel reservationLabel = new JLabel("Lista Rezervacija:");
        reservationsPanel.add(reservationLabel);

        updateReservationsPanel(reservationsPanel, gost.getLicneRezervacije());

        JScrollPane reservationsScrollPane = new JScrollPane(reservationsPanel);
        reservationsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        reservationsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JLabel totalMoneySpentLabel = new JLabel("Total Money Spent on Reservations: " + gost.calculateTotalOwnSpendings() + " din.");
        reservationsPanel.add(Box.createVerticalGlue());
        reservationsPanel.add(totalMoneySpentLabel);

        panel.add(filterPanel, BorderLayout.WEST);
        panel.add(reservationsScrollPane, BorderLayout.CENTER);

        // Filtering logic
        filterButton.addActionListener(e -> {
            LocalDate startDate = startDateChooser.getDate() != null ? startDateChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null;
            LocalDate endDate = endDateChooser.getDate() != null ? endDateChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null;
            Object selectedRoomType = roomTypeComboBox.getSelectedItem();
            Object selectedStatus = statusComboBox.getSelectedItem();
            
            TipSobe roomType = selectedRoomType instanceof TipSobe ? (TipSobe) selectedRoomType : null;
            StanjeRezervacije status = selectedStatus instanceof StanjeRezervacije ? (StanjeRezervacije) selectedStatus : null;

            boolean sortByPrice = filterByPriceCheckBox.isSelected();

            List<Amenities> selectedAmenities = new ArrayList<>();
            for (JCheckBox checkBox : amenitiesCheckBoxes) {
                if (checkBox.isSelected()) {
                    selectedAmenities.add(csvHandler.findAmenitiesByOpis(checkBox.getText()));
                }
            }

            List<Rezervacija> filteredReservations = gost.filterAndSortPersonalReservations(
                    startDate, endDate, selectedAmenities, roomType, status, sortByPrice
            );
            updateReservationsPanel(reservationsPanel, filteredReservations);
            totalMoneySpentLabel.setText("Total Money Spent on Reservations: " + gost.calculateTotalOwnSpendings() + " din.");
        });

        // Reset logic
        resetButton.addActionListener(e -> {
            startDateChooser.setDate(null);
            endDateChooser.setDate(null);
            roomTypeComboBox.setSelectedItem("Show All");
            statusComboBox.setSelectedItem("Show All");
            filterByPriceCheckBox.setSelected(false);

            updateReservationsPanel(reservationsPanel, gost.getLicneRezervacije());
            totalMoneySpentLabel.setText("Total Money Spent on Reservations: " + gost.calculateTotalOwnSpendings() + " din.");
        });

        return panel;
    }

    private JPanel createMakeReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Left side with reservation filters
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<TipSobe> roomTypeComboBox = new JComboBox<>(TipSobe.values());
        JComboBox<StanjeSobe> statusComboBox = new JComboBox<>(StanjeSobe.values());
        JTextField maxPriceField = new JTextField(10);
        JButton searchButton = new JButton("Pretraga");

        filterPanel.add(new JLabel("Tip Sobe:"));
        filterPanel.add(roomTypeComboBox);
        filterPanel.add(Box.createVerticalStrut(10)); // Spacing
        filterPanel.add(new JLabel("Stanje Sobe:"));
        filterPanel.add(statusComboBox);
        filterPanel.add(Box.createVerticalStrut(10)); // Spacing
        filterPanel.add(new JLabel("Maksimalna Cena Sobe:"));
        filterPanel.add(maxPriceField);
        filterPanel.add(Box.createVerticalStrut(10)); // Spacing
        filterPanel.add(searchButton);

        // Right side with available rooms
        JPanel availableRoomsPanel = new JPanel();
        availableRoomsPanel.setLayout(new BoxLayout(availableRoomsPanel, BoxLayout.Y_AXIS));
        availableRoomsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel availableRoomsLabel = new JLabel("Dostupne Sobe:");
        availableRoomsPanel.add(availableRoomsLabel);

        // Initially load all rooms
        updateAvailableRoomsPanel(availableRoomsPanel, availableRooms, null, null);

        // Add a JScrollPane to the availableRoomsPanel
        JScrollPane roomsScrollPane = new JScrollPane(availableRoomsPanel);
        roomsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        roomsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        searchButton.addActionListener(e -> {
            TipSobe selectedRoomType = (TipSobe) roomTypeComboBox.getSelectedItem();
            StanjeSobe selectedStatus = (StanjeSobe) statusComboBox.getSelectedItem();
            Double maxPrice = maxPriceField.getText().isEmpty() ? null : Double.parseDouble(maxPriceField.getText());

            List<Soba> filteredRooms = gost.filterRooms(availableRooms, selectedStatus, selectedRoomType, maxPrice, cenovnik);
            updateAvailableRoomsPanel(availableRoomsPanel, filteredRooms, null, null);
        });

        panel.add(filterPanel, BorderLayout.WEST);
        panel.add(roomsScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private List<JCheckBox> addAmenitiesCheckboxes(JPanel panel) {
        List<JCheckBox> checkboxes = new ArrayList<>();
        for (Amenities amenity : Amenities.values()) {
            JCheckBox checkBox = new JCheckBox(amenity.getOpis());
            panel.add(checkBox);
            checkboxes.add(checkBox);
        }
        return checkboxes;
    }

    // Modify updateReservationsPanel to add a button to pay for PENDING reservations
    private void updateReservationsPanel(JPanel reservationsPanel, List<Rezervacija> reservations) {
        reservationsPanel.removeAll();
        reservationsPanel.setLayout(new BoxLayout(reservationsPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignment

        for (Rezervacija reservation : reservations) {
            JPanel reservationEntry = new JPanel();
            reservationEntry.setLayout(new BoxLayout(reservationEntry, BoxLayout.X_AXIS)); // Use BoxLayout for horizontal alignment
            reservationEntry.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // Reduce padding

            // Updated label to include total price
            JLabel reservationLabel = new JLabel("Reservation: " + reservation.getSoba().getTipSobe() 
                                                  + ", " + reservation.getCheckInDate() 
                                                  + " do " + reservation.getCheckOutDate() 
                                                  + ", Status: " + reservation.getStanjeRezervacije() 
                                                  + ", Price: " + reservation.getTotalPrice() + " din.");
            reservationLabel.setPreferredSize(new Dimension(550, 20)); // Adjust the size for a compact look

            reservationEntry.add(reservationLabel);

            // Add a "Pay for Reservation" button if the reservation is in PENDING status
            if (reservation.getStanjeRezervacije() == StanjeRezervacije.PENDING) {
                JButton payButton = new JButton("Pay for Reservation");
                payButton.setPreferredSize(new Dimension(150, 20)); // Adjust the size for a compact look
                payButton.addActionListener(e -> showPayReservationDialog(reservation));
                reservationEntry.add(Box.createHorizontalStrut(10)); // Add small spacing between label and button
                reservationEntry.add(payButton);
            }

            reservationsPanel.add(reservationEntry);
        }

        reservationsPanel.revalidate();
        reservationsPanel.repaint();
    }

    private void updateAvailableRoomsPanel(JPanel availableRoomsPanel, List<Soba> rooms, LocalDate checkIn, LocalDate checkOut) {
        availableRoomsPanel.removeAll();
        availableRoomsPanel.setLayout(new BoxLayout(availableRoomsPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical stacking
        availableRoomsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Optional padding around the panel

        for (Soba room : rooms) {
            JPanel roomEntry = new JPanel();
            roomEntry.setLayout(new BoxLayout(roomEntry, BoxLayout.X_AXIS)); // Horizontal layout for room details
            roomEntry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Set max height for each room entry
            roomEntry.setAlignmentX(Component.LEFT_ALIGNMENT); // Align room entry to the left

            if (room.getStanjeSobe() == StanjeSobe.AVAILABLE) {
                JLabel roomLabel = new JLabel("Soba " + room.getBrojSobe() + " (" + room.getTipSobe() + ") - " + cenovnik.getRoomPrice(room.getTipSobe()) + " din.");
                roomEntry.add(roomLabel);

                JButton chooseRoomButton = new JButton("Odaberi Sobu");
                chooseRoomButton.addActionListener(e -> showMakeReservationDialog(room.getBrojSobe(), room.getTipSobe().toString(), cenovnik.getRoomPrice(room.getTipSobe())));
                roomEntry.add(Box.createHorizontalStrut(10)); // Add space between label and button
                roomEntry.add(chooseRoomButton);
            } else {
                JLabel roomLabel = new JLabel("Soba " + room.getBrojSobe() + " (" + room.getTipSobe() + ") - Nije dostupna");
                roomEntry.add(roomLabel);
            }

            availableRoomsPanel.add(roomEntry);
        }

        availableRoomsPanel.revalidate();
        availableRoomsPanel.repaint();
    }

    private JPanel createRoomEntry(int roomNumber, String roomType, double price, LocalDate checkIn, LocalDate checkOut) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS)); // Use BoxLayout for horizontal alignment
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // Reduce padding

        JLabel roomLabel = new JLabel("Soba " + roomNumber + " (" + roomType + ") - " + price + " din.");
        roomLabel.setPreferredSize(new Dimension(300, 20)); // Adjust the size for a compact look

        JButton chooseRoomButton = new JButton("Odaberi Sobu");
        chooseRoomButton.setPreferredSize(new Dimension(120, 20)); // Adjust the size for a compact look
        chooseRoomButton.addActionListener(e -> showMakeReservationDialog(roomNumber, roomType, price));

        panel.add(roomLabel);
        panel.add(Box.createHorizontalStrut(10)); // Add small spacing between label and button
        panel.add(chooseRoomButton);

        return panel;
    }

    // Modify showMakeReservationDialog to only create the reservation without paying for it
    private void showMakeReservationDialog(int roomNumber, String roomType, double basePrice) {
    	// Get the parent JFrame, cast it to a JFrame, and use it as the first parameter
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        // If parentFrame is null, fallback to a new JFrame
        JDialog dialog = new JDialog(parentFrame != null ? parentFrame : new JFrame(), "Napravi Rezervaciju", true);

        dialog.setResizable(true);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JDateChooser checkInChooser = new JDateChooser();
        JDateChooser checkOutChooser = new JDateChooser();
        panel.add(new JLabel("Check-In Date:"));
        panel.add(checkInChooser);
        panel.add(Box.createVerticalStrut(10)); // Spacing
        panel.add(new JLabel("Check-Out Date:"));
        panel.add(checkOutChooser);

        JLabel roomDetailsLabel = new JLabel("Room " + roomNumber + " (" + roomType + ")");
        List<JCheckBox> amenityCheckboxes = addAmenitiesCheckboxes(panel);
        JLabel totalPriceLabel = new JLabel("Total Price: " + basePrice + " din.");
        JButton createReservationButton = new JButton("Napravi Rezervaciju");
        JButton cancelButton = new JButton("Cancel");

        panel.add(roomDetailsLabel);
        panel.add(Box.createVerticalStrut(10)); // Spacing
        panel.add(new JLabel("Odaberi Amenities:"));
        amenityCheckboxes.forEach(panel::add);
        panel.add(Box.createVerticalStrut(10)); // Spacing
        panel.add(totalPriceLabel);
        panel.add(Box.createVerticalStrut(10)); // Spacing

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(createReservationButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        dialog.add(panel);

        // Base price calculation based on selected dates
        ActionListener dateChangeListener = e -> {
            if (checkInChooser.getDate() != null && checkOutChooser.getDate() != null) {
                LocalDate checkInDate = checkInChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalDate checkOutDate = checkOutChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                long daysBetween = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
                double baseRoomPrice = daysBetween * basePrice;
                double totalPrice = baseRoomPrice;

                for (JCheckBox cb : amenityCheckboxes) {
                    if (cb.isSelected()) {
                        totalPrice += cenovnik.getAmenityPrice(csvHandler.findAmenitiesByOpis(cb.getText())) * daysBetween;
                    }
                }
                totalPriceLabel.setText("Total Price: " + totalPrice + " din.");
            }
        };

        checkInChooser.getDateEditor().addPropertyChangeListener(e -> dateChangeListener.actionPerformed(null));
        checkOutChooser.getDateEditor().addPropertyChangeListener(e -> dateChangeListener.actionPerformed(null));
        amenityCheckboxes.forEach(cb -> cb.addActionListener(e -> dateChangeListener.actionPerformed(null)));

        createReservationButton.addActionListener(e -> {
            try {
                if (checkInChooser.getDate() == null || checkOutChooser.getDate() == null) {
                    throw new IllegalArgumentException("Check-In and Check-Out dates must be selected.");
                }

                LocalDate checkInDate = checkInChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalDate checkOutDate = checkOutChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

                List<Amenities> selectedAmenities = new ArrayList<>();
                for (JCheckBox checkBox : amenityCheckboxes) {
                    if (checkBox.isSelected()) {
                        selectedAmenities.add(csvHandler.findAmenitiesByOpis(checkBox.getText()));
                    }
                }

                Rezervacija reservation = gost.napraviRezervacijuSebi(
                        new Soba(roomNumber, csvHandler.findTipSobeByOpis(roomType), StanjeSobe.AVAILABLE),
                        checkInDate, checkOutDate, selectedAmenities, cenovnik, ukupneRezervacije);

                csvHandler.writeRezervacijaToCSV(reservation);

                JOptionPane.showMessageDialog(this, "Successfully created a new reservation with a PENDING status. Please proceed to the 'My Reservations' panel to pay for the new reservation.", "Reservation Created", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

                balanceLabel.setText("Current Balance: " + gost.getRacun().getBalance());

                // Rebuild the entire My Reservations panel
                cardPanel.remove(0); // Remove the current My Reservations panel
                JPanel myReservationsPanel = createMyReservationsPanel(); // Recreate the My Reservations panel
                cardPanel.add(myReservationsPanel, "Moje Rezervacije", 0); // Add it back to the card layout

                cardLayout.show(cardPanel, "Moje Rezervacije"); // Show the updated panel

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack(); // Automatically sizes the dialog to fit all components
        dialog.setVisible(true);
    }

    private void updateTotalPrice(JDateChooser checkInChooser, JDateChooser checkOutChooser, List<JCheckBox> amenityCheckboxes, double basePrice, JLabel totalPriceLabel) {
        if (checkInChooser.getDate() != null && checkOutChooser.getDate() != null) {
            LocalDate checkInDate = checkInChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate checkOutDate = checkOutChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            long daysStayed = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

            double totalPrice = basePrice * daysStayed;

            for (JCheckBox checkBox : amenityCheckboxes) {
                if (checkBox.isSelected()) {
                    for (Amenities amenity : Amenities.values()) {
                        if (amenity.getOpis().equals(checkBox.getText())) {
                            totalPrice += cenovnik.getAmenityPrice(amenity) * daysStayed;
                            break;
                        }
                    }
                }
            }

            totalPriceLabel.setText("Total Price: " + totalPrice + " din.");
        }
    }

    private void showAddMoneyDialog() {
        // Get the parent JFrame, cast it to a JFrame, and use it as the first parameter
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        // If parentFrame is null, fallback to a new JFrame
        JDialog dialog = new JDialog(parentFrame != null ? parentFrame : new JFrame(), "Popuni Licni Racun", true);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField amountField = new JTextField(10);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(10)); // Add spacing between fields

        // Create a new panel for the buttons with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton addButton = new JButton("Popuni");
        JButton cancelButton = new JButton("Cancel");

        addButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                gost.getRacun().deposit(amount);
                balanceLabel.setText("Trenutno Stanje na Racunu: " + gost.getRacun().getBalance());
                dialog.dispose();
                showSuccessDialog();

                // Save changes to CSV
                csvHandler.updateGostInCSV(gost);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        // Add buttons to the buttonPanel
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Add the buttonPanel to the main panel
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.pack(); // Automatically sizes the dialog to fit all components
        dialog.setVisible(true);
    }
    
    // New method to handle payment process in a separate dialog
    private void showPayReservationDialog(Rezervacija reservation) {
    	// Get the parent JFrame, cast it to a JFrame, and use it as the first parameter
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        // If parentFrame is null, fallback to a new JFrame
        JDialog dialog = new JDialog(parentFrame != null ? parentFrame : new JFrame(), "Pay for Reservation", true);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Room: " + reservation.getSoba().getBrojSobe()));
        panel.add(Box.createVerticalStrut(10)); // Add spacing between elements
        panel.add(new JLabel("Check-In Date: " + reservation.getCheckInDate()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Check-Out Date: " + reservation.getCheckOutDate()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Total Price: " + reservation.getTotalPrice() + " din."));
        panel.add(Box.createVerticalStrut(20)); // Add more spacing before buttons

        // Create a new panel for the buttons with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton payButton = new JButton("Pay for Reservation");
        JButton cancelButton = new JButton("Cancel");

        payButton.addActionListener(e -> {
            try {
                gost.uplatiRezervaciju(reservation, hotelAccount);
                reservation.setStanjeRezervacije(StanjeRezervacije.PAYED_FOR);
                csvHandler.updateRezervacijaInCSV(reservation);

                JOptionPane.showMessageDialog(this, "Reservation successfully paid for!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

                balanceLabel.setText("Current Balance: " + gost.getRacun().getBalance());

                // Rebuild the entire My Reservations panel
                cardPanel.remove(0); // Remove the current My Reservations panel
                JPanel myReservationsPanel = createMyReservationsPanel(); // Recreate the My Reservations panel
                cardPanel.add(myReservationsPanel, "Moje Rezervacije", 0); // Add it back to the card layout

                cardLayout.show(cardPanel, "Moje Rezervacije"); // Show the updated panel

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        // Add buttons to the buttonPanel
        buttonPanel.add(payButton);
        buttonPanel.add(cancelButton);

        // Add the buttonPanel to the main panel
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.pack(); // Automatically size the dialog to fit all components
        dialog.setVisible(true);
    }
    
    
    private void showSuccessDialog() {
        JOptionPane.showMessageDialog(this, "Uspesno ste popunili Licni Racun!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        CSVHandler csvHandlerMain = new CSVHandler();
        List<GlumiGosta> goste = csvHandlerMain.loadGosteFromCSV(); // Load guests from CSV
        List<Soba> sobe = csvHandlerMain.loadSobeFromCSV(); // Load rooms from CSV
        Cenovnik cenovnik = csvHandlerMain.loadTimeBoundPriceListsFromCSV().getPriceList(LocalDate.now());
        List<Rezervacija> ukupneRezervacije = csvHandlerMain.loadRezervacijeFromCSV(goste, sobe, cenovnik);
        RacunHotela hotelAccount = new RacunHotela(50000.0);

        if (goste.isEmpty()) {
            System.out.println("No guests found in CSV.");
            return;
        }

        GlumiGosta gost = goste.get(0); // Use the first guest for this example
        

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("GostPanel Standalone");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 800);
            GostPanel gostPanel = new GostPanel(gost, sobe, cenovnik, ukupneRezervacije, hotelAccount);
            frame.add(gostPanel);
            frame.setVisible(true);
        });
    }
}