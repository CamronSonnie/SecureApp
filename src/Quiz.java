import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class Quiz extends JFrame {
    private Connection connection;
    private JLabel questionLabel;
    private JRadioButton[] choiceButtons;
    private JButton submitButton;
    private int currentQuestionIndex;
    private ResultSet questionResultSet;
    private static int numberOfQuestions;
    private int correctAnswers;
    private int incorrectAnswers;
    private int questionsAnswered;

    public Quiz(Connection connection, int numberOfQuestions) {
        this.connection = connection;
        this.numberOfQuestions = numberOfQuestions;
        this.correctAnswers = 0;
        this.incorrectAnswers = 0;
        this.questionsAnswered = 0;

        setTitle("Quiz");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        questionLabel = new JLabel();
        choiceButtons = new JRadioButton[4];
        for (int i = 0; i < 4; i++) {
            choiceButtons[i] = new JRadioButton();
 
        }
        ButtonGroup buttonGroup = new ButtonGroup();
        for (JRadioButton button : choiceButtons) {
            buttonGroup.add(button);
        }

        JPanel questionPanel = new JPanel(new GridLayout(5, 1));
        questionPanel.add(questionLabel);
        for (JRadioButton button : choiceButtons) {
            questionPanel.add(button);
        }

        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitAnswer();
            }
        });

        add(questionPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);

        setVisible(true);
        loadNextQuestion();
    }

    private ArrayList<Integer> questionIds = new ArrayList<>();

    private void loadNextQuestion() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM quiz_questions");
            ArrayList<Integer> availableQuestionIds = new ArrayList<>();
            while (resultSet.next()) {
                int questionId = resultSet.getInt("question_id");
                if (!questionIds.contains(questionId)) {
                    availableQuestionIds.add(questionId);
                }
            }

            if (!availableQuestionIds.isEmpty()) {
                int randomIndex = (int) (Math.random() * availableQuestionIds.size());
                int selectedQuestionId = availableQuestionIds.get(randomIndex);

                resultSet = statement.executeQuery("SELECT * FROM quiz_questions WHERE question_id = " + selectedQuestionId);
                if (resultSet.next()) {
                    questionResultSet = resultSet;
                    currentQuestionIndex = resultSet.getInt("question_id");
                    questionLabel.setText(resultSet.getString("question_text"));
                    choiceButtons[0].setText(resultSet.getString("choice_1"));
                    choiceButtons[1].setText(resultSet.getString("choice_2"));
                    choiceButtons[2].setText(resultSet.getString("choice_3"));
                    choiceButtons[3].setText(resultSet.getString("choice_4"));
                    questionsAnswered++;
                    questionIds.add(selectedQuestionId);

                    if (correctAnswers + incorrectAnswers == numberOfQuestions) {
                        JOptionPane.showMessageDialog(this, "Quiz completed!");
                        showSummary();
                        dispose();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No more questions available.");
                showSummary();
                dispose();
            }

            // Set font for question label
            questionLabel.setFont(new Font("Arial", Font.BOLD, 22));

            // Set font for choice buttons
            Font buttonFont = new Font("Arial", Font.PLAIN, 20);
            for (JRadioButton button : choiceButtons) {
                button.setFont(buttonFont);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private void submitAnswer() {
        try {
            int selectedChoice = -1;
            for (int i = 0; i < choiceButtons.length; i++) {
                if (choiceButtons[i].isSelected()) {
                    selectedChoice = i + 1;
                    break;
                }
            }
            if (selectedChoice == -1) {
                JOptionPane.showMessageDialog(this, "Please select an answer.");
                return;
            }
            int correctChoice = questionResultSet.getInt("correct_choice");
            if (selectedChoice == correctChoice) {
                correctAnswers++;
                JOptionPane.showMessageDialog(this, "Correct!");
            } else {
                incorrectAnswers++;
                JOptionPane.showMessageDialog(this, "Incorrect. The correct answer is: " + questionResultSet.getString("choice_" + correctChoice));
            }
            loadNextQuestion();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showSummary() {
        JOptionPane.showMessageDialog(this,
                "Quiz Summary:\n" +
                        "Number of questions: " + numberOfQuestions + "\n" +
                        "Correct answers: " + correctAnswers + "\n" +
                        "Incorrect answers: " + incorrectAnswers);
    }

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:/Users/camronsonnie/Desktop/Eclipse/Assignment2-SecureApp/src/secure_app.db");
            SwingUtilities.invokeLater(() -> new Quiz(connection, numberOfQuestions));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
