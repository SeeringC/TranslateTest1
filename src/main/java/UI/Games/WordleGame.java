package UI.Games;

import AlertDisplay.CustomAlert;
import AlertDisplay.LoseWordleGameAlert;
import AlertDisplay.WinWordleGameAlert;
import Manager.UIManager;
import UI.UILayer;
import com.example.translatetest1.MyDictionary;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WordleGame implements UILayer {

    private static final int WORD_LENGTH = 5;
    private static final int GUESS_TURN = 6;
    private List<String> answerWord = new ArrayList<>();
    private List<String> tempAnswerWord = new ArrayList<>();
    private TextField[][] textFieldsArr = new TextField[GUESS_TURN][WORD_LENGTH];
    private ArrayList<String> wordsList = new ArrayList<>();
    private CustomAlert customAlert;

    @FXML
    private Pane currentPane;
    @FXML
    private GridPane wordleGridPane;

    @Override
    public void onInit() {
        loadWords();
        assignRandomWordToAnswerWord();
        loadGame();
    }

    @Override
    public void onClose() {

    }


    @FXML
    public void switchToGame(ActionEvent event) throws IOException {
        UIManager.getIns(UIManager.class).openScene(currentPane, "Game.fxml");
    }

    private void loadWords() {
        Enumeration<String> keys = MyDictionary.getIns(MyDictionary.class).enToViDic.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if(key.length() == WORD_LENGTH) {
                wordsList.add(key);
            }
        }
    }

    private void assignRandomWordToAnswerWord() {
        String randomWord = getRandomWord();
        System.out.println("The word is: " + randomWord);
        answerWord = Arrays.asList(randomWord.split(""));
        tempAnswerWord.addAll(answerWord);
    }

    public void loadGame() {
        for (int row = 0; row < GUESS_TURN; row++) {
            for (int col = 0; col < WORD_LENGTH; col++) {
                TextField textField = createTextField();
                textFieldsArr[row][col] = textField;
                wordleGridPane.add(textField, col, row);
            }
        }
        moveToNextTextField();
    }

    private TextField createTextField() {
        TextField textField = new TextField();
        setStyleToTextField(textField);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 1) {
                textField.setText(newValue.substring(0, 1));
            }
        });
        return textField;
    }

    private void setStyleToTextField(TextField textField) {
         textField.setStyle("    -fx-background-color: #1f0606;\n" +
                            "   -fx-border-color: #05dff7;\n" +
                            "    -fx-border-width: 1;\n" +
                            "    -fx-text-fill: #05dff7;\n" +
                            "    -fx-pref-width: 167;\n" +
                            "    -fx-pref-height: 108;" +
                            "    -fx-alignment: center;" +
                            "    -fx-font-size: 33;" +
                            "    -fx-font-family: Oxanium;");
    }
    public void moveToNextTextField() {
        for (int row = 0; row < GUESS_TURN; row++) {
            for (int col = 0; col < WORD_LENGTH; col++) {
                handleKeyPress(row, col);
                handleTextChange(row, col);
            }
        }
    }

    private void handleKeyPress(int row, int col) {
        textFieldsArr[row][col].setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE && col != 0 && textFieldsArr[row][col].getText().isEmpty()) {
                textFieldsArr[row][col - 1].requestFocus();
            }
        });
    }

    private void handleTextChange(int row, int col) {
        textFieldsArr[row][col].textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 1 && oldValue.length() < 1) {
                if (col != WORD_LENGTH - 1) {
                    textFieldsArr[row][col + 1].requestFocus();
                } else if (row != GUESS_TURN - 1) {
                    textFieldsArr[row + 1][0].requestFocus();
                }
                if (col == WORD_LENGTH - 1) {
                    showResult(row);
                }
            }
        });
    }

    private String getRandomWord() {
        Random random = new Random();
        int index = random.nextInt(wordsList.size());
        return wordsList.get(index);
    }

    public void showResult(int row) {
        int correctCharGuessed = 0;
        for (int col = 0; col < WORD_LENGTH; col++) {
            correctCharGuessed += checkGuess(row, col);
        }

        if (correctCharGuessed == WORD_LENGTH) {
            displayWinAlert();
        }
        if (row == GUESS_TURN - 1) {
            displayLoseAlert();
        }
        tempAnswerWord.addAll(answerWord);
    }

    private int checkGuess(int row, int col) {
        String guess = textFieldsArr[row][col].getText();
        String answer = answerWord.get(col);
        if (Objects.equals(guess, answer)) {
            setStyleToCorrectTextField(textFieldsArr[row][col]);
            tempAnswerWord.remove(guess);
            return 1;
        } else if (answerWord.contains(guess)) {
            setStyleToPartlyCorrectTextField(textFieldsArr[row][col]);
            tempAnswerWord.remove(guess);
        } else {
            setStyleToIncorrectTextField(textFieldsArr[row][col]);
            tempAnswerWord.remove(guess);
        }
        return 0;
    }

    private void setStyleToCorrectTextField(TextField textField) {
        textField.setStyle("    -fx-background-color: #0e5e09;\n" +
                "   -fx-border-color: #05dff7;\n" +
                "    -fx-border-width: 1;\n" +
                "    -fx-text-fill: #05dff7;\n" +
                "    -fx-pref-width: 167;\n" +
                "    -fx-pref-height: 108;" +
                "    -fx-alignment: center;" +
                "    -fx-font-size: 33;" +
                "    -fx-font-family: Oxanium;");
    }

    private void setStyleToPartlyCorrectTextField(TextField textField) {
        textField.setStyle("    -fx-background-color: #a5a80c;\n" +
                "   -fx-border-color: #05dff7;\n" +
                "    -fx-border-width: 1;\n" +
                "    -fx-text-fill: #05dff7;\n" +
                "    -fx-pref-width: 167;\n" +
                "    -fx-pref-height: 108;" +
                "    -fx-alignment: center;" +
                "    -fx-font-size: 33;" +
                "    -fx-font-family: Oxanium;");
    }

    private void setStyleToIncorrectTextField(TextField textField) {
        textField.setStyle("    -fx-background-color: #870909;\n" +
                "   -fx-border-color: #05dff7;\n" +
                "    -fx-border-width: 1;\n" +
                "    -fx-text-fill: #05dff7;\n" +
                "    -fx-pref-width: 167;\n" +
                "    -fx-pref-height: 108;" +
                "    -fx-alignment: center;" +
                "    -fx-font-size: 33;" +
                "    -fx-font-family: Oxanium;");
    }

    public void setCustomAlert(CustomAlert customAlert) {
        this.customAlert = customAlert;
    }
    private void displayWinAlert() {
        setCustomAlert(new WinWordleGameAlert());
        customAlert.displayAlert(getAnswerString());
    }

    private void displayLoseAlert() {
        setCustomAlert(new LoseWordleGameAlert());
        customAlert.displayAlert(getAnswerString());
    }

    private String getAnswerString() {
        return answerWord.stream().collect(Collectors.joining());
    }

    @FXML
    private void replayGame(ActionEvent event) {
        assignRandomWordToAnswerWord();
        loadGame();
    }

    @FXML
    private void backToGames (KeyEvent event) throws IOException {
        System.out.println("key press");
        if (event.getCode() == KeyCode.ESCAPE) {
            UIManager.getIns(UIManager.class).openScene(currentPane, "UI.Game.fxml");
        }
    }
}

