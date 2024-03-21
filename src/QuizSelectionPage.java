import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class QuizSelectionPage extends JFrame implements ActionListener {
    private String name;
    private JComboBox<String> numQuestionsCombo;

    public QuizSelectionPage(String userName) {
        this.name = userName;

        setTitle("Quiz Selection");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1));

        JLabel greetingLabel = new JLabel("Hello, " + name + "!");
        greetingLabel.setFont(new Font("Arial", Font.BOLD, 20)); 
        JLabel descriptionLabel = new JLabel("<html>From entertainment and science to<br>history and sports, these questions are<br>designed to test your general knowledge<br>and keep you on your toes!</html>");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel selectLabel = new JLabel("Select the number of quiz questions for the common knowledge quiz:");
        selectLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        numQuestionsCombo = new JComboBox<>(new String[]{"5", "10", "15"});
        JButton startButton = new JButton("Start Quiz");

        JPanel greetingPanel = new JPanel(new FlowLayout());
        JPanel descriptionPanel = new JPanel(new FlowLayout());
        JPanel selectPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        greetingPanel.add(greetingLabel);
        descriptionPanel.add(descriptionLabel); // Adding JLabel with HTML formatting to the panel
        selectPanel.add(selectLabel);
        selectPanel.add(numQuestionsCombo);
        buttonPanel.add(startButton);

        add(greetingPanel);
        add(descriptionPanel); // Adding the JLabel panel to the frame
        add(selectPanel);
        add(buttonPanel);

        startButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Start Quiz")) {
            String selectedNumQuestions = (String) numQuestionsCombo.getSelectedItem();
            if (selectedNumQuestions != null) {
                int numberOfQuestions = Integer.parseInt(selectedNumQuestions);
                // Close the current window
                dispose();
                // Open the quiz window
                openQuizPage(numberOfQuestions);
            }
        }
    }

    private void openQuizPage(int numberOfQuestions) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:/Users/camronsonnie/Desktop/Eclipse/Assignment2-SecureApp/src/secure_app.db");
            SwingUtilities.invokeLater(() -> new Quiz(connection, numberOfQuestions));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
