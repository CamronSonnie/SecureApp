import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterPage extends JFrame implements ActionListener {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton backButton; // New button for going back to the login page
    private Connection connection;

    public RegisterPage(Connection connection) {
        this.connection = connection;

        setTitle("Register Page");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 1)); // Adjusted layout for register page

        nameField = new JTextField();
        emailField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        registerButton = new JButton("Register");
        backButton = new JButton("Go back to Login"); // Initialize the new button

        JLabel nameLabel = new JLabel("Name:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        JPanel namePanel = new JPanel(new FlowLayout());
        JPanel emailPanel = new JPanel(new FlowLayout());
        JPanel usernamePanel = new JPanel(new FlowLayout());
        JPanel passwordPanel = new JPanel(new FlowLayout());
        JPanel registerPanel = new JPanel(new FlowLayout());
        JPanel backPanel = new JPanel(new FlowLayout()); // New panel for the back button

        namePanel.add(nameLabel);
        namePanel.add(nameField);
        nameField.setColumns(15);
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        emailField.setColumns(15);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        usernameField.setColumns(15);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        passwordField.setColumns(15);
        registerPanel.add(registerButton);
        backPanel.add(backButton); // Add the back button to its panel

        add(namePanel);
        add(emailPanel);
        add(usernamePanel);
        add(passwordPanel);
        add(registerPanel);
        add(backPanel); // Add the back button panel to the frame

        registerButton.addActionListener(this);
        backButton.addActionListener(this); // Register action listener for the back button

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            // Register new user
            String name = nameField.getText();
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = SecurityManager.hashPassword(String.valueOf(passwordField.getPassword()));
            if (!name.isEmpty() && !email.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                registerUser(name, email, username, password);
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                dispose(); // Close the registration page
            } else {
                JOptionPane.showMessageDialog(this, "Name, email, username, and password cannot be empty.");
            }
        } else if (e.getSource() == backButton) {
            // Go back to the login page
            dispose(); // Close the registration page
            new login().setVisible(true); // Open the login page
        }
    }

    private void registerUser(String name, String email, String username, String password) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (name, email, username, password) VALUES (?, ?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, username);
            statement.setString(4, password);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
