package se.kth.id1212.sockets.server.controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import se.kth.id1212.sockets.server.DTO.GameState;
import se.kth.id1212.sockets.server.model.Hangman;
/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class Controller {
   private final Hangman hangman = new Hangman();
   
   public void guessLetter(char letter) {
       hangman.guessLetter(letter);
    }
   
   public void startGame() {
       hangman.startGame();
   }
   
   public void solveWord(char[] word) {
       hangman.solveWord(word);
   }
   
   public GameState getGameState() {
       return hangman.getGameState();
   }
}
