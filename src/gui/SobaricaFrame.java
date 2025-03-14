package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import csv.CSVHandler;
import interfejsi.OsobljeZaCiscenje;
import uloge.Sobarica;
import usluge.Soba;

public class SobaricaFrame extends JFrame {
    private Sobarica sobarica;
    private JPanel roomPanel;
    private JLabel cleanedRoomsCounterLabel;
    private CSVHandler csvHandler;

    public SobaricaFrame(String username) {
        csvHandler = new CSVHandler();
        this.sobarica = loadSobaricaByUsername(username);

        setTitle("Sobarica GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        roomPanel = new JPanel();
        roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));

        // Load and display the list of rooms to clean
        loadRoomList();

        JScrollPane scrollPane = new JScrollPane(roomPanel);
        add(scrollPane, BorderLayout.CENTER);

        cleanedRoomsCounterLabel = new JLabel("Total Rooms Cleaned: " + sobarica.getTotalCleanedRoomsCounter());
        add(cleanedRoomsCounterLabel, BorderLayout.SOUTH);
    }

    private Sobarica loadSobaricaByUsername(String username) {
        List<OsobljeZaCiscenje> cleaningStaff = csvHandler.loadOsobljeZaCiscenjeFromCSV(
            csvHandler.loadSobeFromCSV(), csvHandler.loadTimeBoundPriceListsFromCSV()
        );
        for (OsobljeZaCiscenje staff : cleaningStaff) {
            if (staff instanceof Sobarica && ((Sobarica) staff).getUsername().equals(username)) {
                return (Sobarica) staff;
            }
        }
        throw new IllegalArgumentException("No Sobarica found with username: " + username);
    }

    private void loadRoomList() {
        roomPanel.removeAll(); // Clear previous content
        List<Soba> sobeObaveze = sobarica.getSobeObaveze();

        for (Soba soba : sobeObaveze) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel roomLabel = new JLabel("Room " + soba.getBrojSobe() + " (" + soba.getTipSobe() + ")");
            JButton cleanButton = new JButton("Clean the room");

            cleanButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sobarica.zavrsiSredjivanjeSobe(soba);
                    cleanedRoomsCounterLabel.setText("Total Rooms Cleaned: " + sobarica.getTotalCleanedRoomsCounter());
                    loadRoomList(); // Refresh the list after cleaning a room
                    roomPanel.revalidate();
                    roomPanel.repaint();
                }
            });

            panel.add(roomLabel);
            panel.add(cleanButton);
            roomPanel.add(panel);
        }

        if (sobeObaveze.isEmpty()) {
            JLabel noRoomsLabel = new JLabel("No rooms assigned for cleaning.");
            roomPanel.add(noRoomsLabel);
        }

        roomPanel.revalidate();
        roomPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SobaricaFrame frame = new SobaricaFrame("sobarica2");
            frame.setVisible(true);
        });
    }
}