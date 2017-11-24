package se.kth.id1212.sockets.client.controller;


import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import se.kth.id1212.sockets.client.net.OutputHandler;
import se.kth.id1212.sockets.client.net.ServerConnection;
/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class Controller {
    private final ServerConnection serverConnection = new ServerConnection();
    
    public void connect(String host, int port, OutputHandler outputHandler) {
        serverConnection.connect(host, port, outputHandler);
    }
    
    public void disconnect() throws IOException {
        serverConnection.quit();
    }
    
    public void guessLetter(char letter) {
        serverConnection.guessLetter(letter);
    }
    
    public void solveWord(String word) {
        serverConnection.solveWord(word);
    }
    
    public void startGame() {
        serverConnection.startGame();
    }
}
