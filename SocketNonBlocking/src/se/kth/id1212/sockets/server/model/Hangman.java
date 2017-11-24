package se.kth.id1212.sockets.server.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import se.kth.id1212.sockets.server.DTO.GameState;
/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public final class Hangman {
    private int tries;
    private char[] unknownWord;
    private char[] word;
    private int score;
    private int lettersLeft;
    private final String INCREASE_SCORE = "ADD";
    private final String DECREASE_SCORE = "SUB";
    
    public Hangman() {
        score = 0;
        startGame();
    }
    
    public void startGame() {
        word = setWord();
        setTries();
        initUnknownWord();
        lettersLeft = tries;
    }
    
    private char[] setWord() {
        try {
            Scanner scanner = new Scanner(new File("words.txt"));
            List<String> temps = new ArrayList<>();
            String tempWord;
            while(scanner.hasNext()) {
                tempWord = scanner.next();
                temps.add(tempWord);
            }
            String[] tempsArray = temps.toArray(new String[0]);
            int n = (int)(Math.random() * tempsArray.length + 1);
            tempWord = tempsArray[n];
            tempWord = tempWord.toLowerCase();
            return tempWord.toCharArray();
        } catch (FileNotFoundException fnfe) {
            System.err.println("File was not found in the directory.");
            return null;
        }
    }
    
    private void setTries() {
        tries = word.length;
    }
    
    private void initUnknownWord() {
        unknownWord = new char[word.length];
        for(int i =0;i<unknownWord.length;i++) {
            unknownWord[i] = '_';
        }
    }
    
    private boolean ableToGuess() {
        return lettersLeft != 0 && tries != 0;
    }
    
    public void solveWord(char[] guessedWord) {
        if(ableToGuess()) {
            if(Arrays.equals(guessedWord, word)) {
                lettersLeft = 0;
                unknownWord = word;
                updateStatus();
            } else {
                reduceTries();
                updateStatus();
            }
        }
    }
    
    public void guessLetter(char letter) {
        if(!ableToGuess()) return;
        boolean hit = false;
        for(int i=0;i<word.length;i++) {
            if(word[i] == letter) {
                hit = true;
                reduceLettersLeft();
                unknownWord[i] = letter;
            }
        }
        if(!hit) {
            reduceTries();
        }
        updateStatus();
    }
    
    private void updateStatus() {
        if(tries == 0) {
            changeScore(DECREASE_SCORE);
        } else if (lettersLeft == 0) {
            changeScore(INCREASE_SCORE);
        }
    }
    
    private void reduceLettersLeft() {
        lettersLeft--;
    }
    
    private void reduceTries() {
        tries--;
    }
    
    private void changeScore(String operation) {
        if(operation.equals(INCREASE_SCORE)) {
            score++;
        } else if (operation.equals(DECREASE_SCORE)){
            score--;
        }
    }
    
    public GameState getGameState() {
        return new GameState(String.copyValueOf(unknownWord), tries, score, lettersLeft);
    }
    
    
    
    
}
