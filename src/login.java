import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class login extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private Connection connection;
    

    public login() {
    	try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	try {
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/camronsonnie/Desktop/Eclipse/Assignment2-SecureApp/src/secure_app.db");
            createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setTitle("Login Page");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        JPanel usernamePanel = new JPanel(new FlowLayout());
        JPanel passwordPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        usernameField.setColumns(15);
        passwordField.setColumns(15);

        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        buttonPanel.add(loginButton);

        add(usernamePanel);
        add(passwordPanel);
        add(buttonPanel);

        loginButton.addActionListener(this);
    }
    
    private void createTableIfNotExists() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL)");
        }
    }
    

	@Override 
    public void actionPerformed(ActionEvent e) {
        // Query the database for the entered username and password
        try { 
        	String usernametext = usernameField.getText();
            String passwordtext = String.valueOf(passwordField.getPassword());
            String rsuser = null;
            String rspass = null;
        	PreparedStatement statement = connection.prepareStatement(
               "SELECT * FROM users WHERE username = ? AND password = ?"); 
            statement.setString(1, usernametext);
            statement.setString(2, passwordtext);
            ResultSet resultSet = statement.executeQuery();
            System.out.println(statement);
            
            
            while(resultSet.next()) {
            	rsuser = resultSet.getString("username");
            	System.out.println(rsuser);
            	rspass = resultSet.getString("password");
            	System.out.println(rspass);
            }
            if ((usernametext.equals(rsuser)) && (passwordtext.equals(rspass))) {
            	JOptionPane.showMessageDialog(this, "Login successful!");
            } else {
            	System.out.println(rsuser);
            	System.out.println(rspass);
            	JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
            
            // If a matching user is found, login successful
//            if (resultSet.next()) {
//            	resultSet.getString(username);
//            	System.out.println(resultSet.getString(username));
//                JOptionPane.showMessageDialog(this, "Login successful!");
//            } else {
//            	System.out.println(String.valueOf(resultSet));
//            	System.out.println(resultSet.getString(username));
//                JOptionPane.showMessageDialog(this, "Invalid username or password.");
//            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
	

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            login loginApp = new login();
            loginApp.setVisible(true);
        });
        

    }
}
