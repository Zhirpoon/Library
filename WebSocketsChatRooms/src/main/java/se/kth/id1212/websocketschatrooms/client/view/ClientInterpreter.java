package se.kth.id1212.websocketschatrooms.client.view;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.kth.id1212.websocketschatrooms.client.controller.ClientController;
import se.kth.id1212.websocketschatrooms.client.net.OutputHandler;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class ClientInterpreter {
    private ClientController controller;
    
    public void startInterpretation() {
        String message;
        boolean notQuitting = true;
        boolean connected = false;
        Scanner scanner = new Scanner(System.in);
        welcomeMessage();
        controller = new ClientController(new ConsoleOutput());
        do {
            message = scanner.nextLine();
            if("#connect".trim().equalsIgnoreCase(message) && !connected) {
                connectToServer(scanner);
                connected = true;
            } else if("#change".trim().equalsIgnoreCase(message) && connected) {
                leaveChatRoom();
                System.out.println("You have now left the chat room.");
                connectToServer(scanner);
                connected = true;
            } else {
                sendMessageToServer(message);
            }
        } while (!message.equalsIgnoreCase("#quit"));
        leaveChatRoom();
    }
    
    private void leaveChatRoom() {
        try {
            controller.leaveChatRoom();
        } catch (IOException ex) {
            System.err.println("Something went wrong when quitting!");
            System.err.println(ex.getMessage());
        }
    }
    
    private void welcomeMessage() {
        System.out.println("Welcome to the chat program! \n"
                + "To connect to a server type \"#connect\" and further instructions will appear." 
                + "To quit the program completely type \"#quit\"");
    }
    
    private void connectToServer(Scanner scanner) {
        System.out.println("What username do you want to have?");
        String user = scanner.nextLine();
        System.out.println("Which chat room would you like to join?");
        String room = scanner.nextLine();
        if(isTextValid(user)) {
            controller.connectToServer(user, room);
        } else {
            System.out.println("Username cannot be empty!");
        }
    }
    
    private void sendMessageToServer(String message) {
        try {
            if(!isTextValid(message)) {
                System.out.println("A message cannot be empty, please send another message!");
                return;
            }
            controller.sendMessageToServer(message);
        } catch (Exception ex) {
            System.err.println("Something went wrong when sending a message to the server!");
            System.err.println(ex.getMessage());
        }
    }
    
    private boolean isTextValid(String text) {
        return !text.trim().isEmpty();
    }
    
    private class ConsoleOutput implements OutputHandler {
        @Override
        public void handleMsg(String msg) {
            System.out.println(msg);
        }
    }
}
