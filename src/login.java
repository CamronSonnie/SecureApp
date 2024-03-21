import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class login extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private Connection connection;

    public login() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/camronsonnie/Desktop/Eclipse/Assignment2-SecureApp/src/secure_app.db");
            createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setTitle("Login Page");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1)); // Adjusted layout for login page

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        JPanel usernamePanel = new JPanel(new FlowLayout());
        JPanel passwordPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        usernameField.setColumns(15);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        passwordField.setColumns(15);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        add(usernamePanel);
        add(passwordPanel);
        add(buttonPanel);

        loginButton.addActionListener(this);
        registerButton.addActionListener(this);
    }

    private void createTableIfNotExists() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "email TEXT NOT NULL UNIQUE," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL)");

            // Add columns for name and email if they don't already exist
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, "users", "name");
            if (!resultSet.next()) {
                statement.executeUpdate("ALTER TABLE users ADD COLUMN name TEXT NOT NULL DEFAULT ''");
            }

            resultSet = metaData.getColumns(null, null, "users", "email");
            if (!resultSet.next()) {
                statement.executeUpdate("ALTER TABLE users ADD COLUMN email TEXT NOT NULL DEFAULT ''");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            // Login functionality
            try { 
                String usernametext = usernameField.getText();
                String passwordtext = SecurityManager.hashPassword(String.valueOf(passwordField.getPassword()));
                String rsuser = null;
                String rspass = null;
                PreparedStatement statement = connection.prepareStatement(
                   "SELECT * FROM users WHERE username = ? AND password = ?"); 
                statement.setString(1, usernametext);
                statement.setString(2, passwordtext);
                ResultSet resultSet = statement.executeQuery();
                
                while(resultSet.next()) {
                    rsuser = resultSet.getString("username");
                    rspass = resultSet.getString("password");
                }
                
                if ((usernametext.equals(rsuser)) && (passwordtext.equals(rspass))) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    String name = retrieveName(usernametext);
                    dispose(); // Close the login page
                    openQuizSelectionPage(name); // Open the quiz selection page
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == registerButton) {
            // Open registration page
            new RegisterPage(connection);
        }
    }

    private String retrieveName(String username) {
        String name = null;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT name FROM users WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                name = resultSet.getString("name");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return name;
    }

    private void openQuizSelectionPage(String name) {
        new QuizSelectionPage(name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            login loginApp = new login();
            loginApp.setVisible(true);
        });
    }
}
