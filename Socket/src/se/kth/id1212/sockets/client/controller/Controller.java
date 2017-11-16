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
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect(host, port, outputHandler);
            } catch(IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        }).thenRun(() -> outputHandler.handleMsg("Connected to: " + host + ":" + port));
    }
    
    public void disconnect() throws IOException {
        serverConnection.disconnect();
    }
    
    public void guessLetter(char letter) {
        CompletableFuture.runAsync(() -> serverConnection.guessLetter(letter));
    }
    
    public void solveWord(String word) {
        CompletableFuture.runAsync(() -> serverConnection.solveWord(word));
    }
    
    public void startGame() {
        CompletableFuture.runAsync(()-> serverConnection.startGame());
    }
}
