package gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import csv.CSVHandler;
import uloge.Administrator;
import uloge.Gost;
import uloge.Recepcioner;
import uloge.Sobarica;
import uloge.Osoba;

public class LoginDialog extends JDialog {

    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);
        setSize(400, 250); // Adjusted size
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());

        // Username label constraints
        JLabel usernameLabel = new JLabel("Username");
        GridBagConstraints usernameLabelConstraints = new GridBagConstraints();
        usernameLabelConstraints.insets = new Insets(10, 10, 10, 10);
        usernameLabelConstraints.gridx = 0;
        usernameLabelConstraints.gridy = 0;
        usernameLabelConstraints.anchor = GridBagConstraints.EAST;
        panel.add(usernameLabel, usernameLabelConstraints);

        // Username field constraints
        JTextField usernameField = new JTextField(20);
        GridBagConstraints usernameFieldConstraints = new GridBagConstraints();
        usernameFieldConstraints.insets = new Insets(10, 10, 10, 10);
        usernameFieldConstraints.gridx = 1;
        usernameFieldConstraints.gridy = 0;
        usernameFieldConstraints.anchor = GridBagConstraints.WEST;
        usernameFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(usernameField, usernameFieldConstraints);

        // Password label constraints
        JLabel passwordLabel = new JLabel("Password");
        GridBagConstraints passwordLabelConstraints = new GridBagConstraints();
        passwordLabelConstraints.insets = new Insets(10, 10, 10, 10);
        passwordLabelConstraints.gridx = 0;
        passwordLabelConstraints.gridy = 1;
        passwordLabelConstraints.anchor = GridBagConstraints.EAST;
        panel.add(passwordLabel, passwordLabelConstraints);

        // Password field constraints
        JPasswordField passwordField = new JPasswordField(20);
        GridBagConstraints passwordFieldConstraints = new GridBagConstraints();
        passwordFieldConstraints.insets = new Insets(10, 10, 10, 10);
        passwordFieldConstraints.gridx = 1;
        passwordFieldConstraints.gridy = 1;
        passwordFieldConstraints.anchor = GridBagConstraints.WEST;
        passwordFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(passwordField, passwordFieldConstraints);

        // Login button constraints
        JButton loginButton = new JButton("Login");
        GridBagConstraints loginButtonConstraints = new GridBagConstraints();
        loginButtonConstraints.insets = new Insets(10, 10, 10, 10);
        loginButtonConstraints.gridx = 0;
        loginButtonConstraints.gridy = 2;
        loginButtonConstraints.gridwidth = 2;
        loginButtonConstraints.anchor = GridBagConstraints.CENTER;
        loginButtonConstraints.fill = GridBagConstraints.NONE;
        panel.add(loginButton, loginButtonConstraints);

        this.add(panel);

        // Action for the login button
        loginButton.addActionListener(e -> {
            CSVHandler csvHandler = new CSVHandler();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Load the list of users (Osoba)
            List<Osoba> osobe = csvHandler.loadOsobeFromCSV();

            // Authenticate user
            Osoba authenticatedUser = null;
            for (Osoba user : osobe) {
                if (user.authenticate(username, password)) {
                    authenticatedUser = user;
                    break;
                }
            }

            if (authenticatedUser != null) {
                JOptionPane.showMessageDialog(this, "Login successful!");

                // Redirect based on role
                if (authenticatedUser instanceof Administrator) {
                    //new AdministratorFrame(authenticatedUser.getUsername()).setVisible(true);
                } else if (authenticatedUser instanceof Recepcioner) {
                    //new RecepcionerPanel().setVisible(true);
                } else if (authenticatedUser instanceof Sobarica) {
                    new SobaricaFrame(authenticatedUser.getUsername()).setVisible(true);
                } else if (authenticatedUser instanceof Gost) {
                    //new GostFrame(1000.0).setVisible(true);
                }

                dispose(); // Close the login dialog
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials, please try again.");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog dialog = new LoginDialog(null);
            dialog.setVisible(true);
        });
    }
}