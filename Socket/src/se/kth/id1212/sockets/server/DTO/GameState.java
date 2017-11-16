package se.kth.id1212.sockets.server.DTO;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class GameState {
    private final int score;
    private final String unknownWord;
    private final int tries;
    private final int lettersLeft;
    
    public GameState(String unknownWord, int tries, int score, int lettersLeft) {
        this.score = score;
        this.unknownWord = unknownWord;
        this.tries = tries;
        this.lettersLeft = lettersLeft;
    }
    
    @Override
    public String toString() {
        if(tries == 0) {
            return "You failed to guess the word! Total score: " + score + " To play again type start";
        } else if (lettersLeft == 0) {
            return "You won! You guessed the word: " + unknownWord + " Your total score is: " + score + " To play again type start";
        } else
        return "The word so far is: " + unknownWord + " Tries left: " + tries + " Total score: " + score;
    }

    public int getScore() {
        return score;
    }

    public String getUnknownWord() {
        return unknownWord;
    }

    public int getTries() {
        return tries;
    }
}
