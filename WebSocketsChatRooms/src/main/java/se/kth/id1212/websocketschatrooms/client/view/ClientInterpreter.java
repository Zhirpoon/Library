package se.kth.id1212.websocketschatrooms.client.view;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.kth.id1212.websocketschatrooms.common.Constants;
import se.kth.id1212.websocketschatrooms.client.controller.ClientController;
import se.kth.id1212.websocketschatrooms.client.net.OutputHandler;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class ClientInterpreter {
    private ClientController controller;
    private boolean connected = false;
    private boolean serverIsActive = true;
    public void startInterpretation() {
        String message;
        Scanner scanner = new Scanner(System.in);
        System.out.println(Constants.WELCOME_MESSAGE);
        controller = new ClientController(new ConsoleOutput());
        do {
            message = scanner.nextLine();
            switch(message.trim()) {
                case Constants.HELP:
                    System.out.println(Constants.HELP_MENU);
                    break;
                case Constants.CONNECT:
                    if(connected) {
                       System.out.println("You can only connect once, to connect to another room use the command: \"" + Constants.CHANGE + "\"");
                       break;
                    }
                    connectToServer(scanner);
                    break;
                case Constants.CHANGE:
                    if(connected) {
                        leaveChatRoom();
                        connectToServer(scanner);
                        break;
                    }
                    System.out.println("You are not connected, please use the command: \"" + Constants.CONNECT +"\" to connect.");
                    break;
                default:
                    sendMessageToServer(message);
                    break;
            }
        } while (!message.trim().equalsIgnoreCase(Constants.QUIT) && serverIsActive);
        quitProgram();
    }
    
    private void checkIfServerIsDown() throws Exception{
        if(!serverIsActive) {
            throw new Exception("The server is down!");
        }
    }
    
    private void quitProgram() {
        try {
            controller.leaveChatRoom();
            System.out.println("Program has stopped successfully!");
        } catch (IOException ex) {
            System.err.println("Sending the quit-message to the server failed, shutting down anyway.");
            System.err.println(ex.getMessage());
        }
        
    }
    
    private void leaveChatRoom() {
            try {
                checkIfServerIsDown();
                controller.leaveChatRoom();
                System.out.println("You have now left the chat room.");
            } catch (Exception  ex) {
                System.err.println("Leaving the chat room failed.");
                System.err.println(ex.getMessage());
                checkIfConnectionIsClosed(ex);
            }
    }
    
    private void connectToServer(Scanner scanner) {
        try {
            checkIfServerIsDown();
            System.out.println("What username do you want to have?");
            String user = scanner.nextLine();
            System.out.println("Which chat room would you like to join?");
            String room = scanner.nextLine();
            isTextValid(user);
            if(controller.connectToServer(user, room)) {
                connected = true;
            }
        } catch (Exception ex) {
            System.out.println("Something went wrong when connecting to the server");
            System.err.println(ex.getMessage());
            checkIfConnectionIsClosed(ex);
        }
        

    }
    
    private void sendMessageToServer(String message) {
        try {
            checkIfServerIsDown();
            isTextValid(message);
            controller.sendMessageToServer(message);
        } catch (Exception ex) {
            System.err.println("Something went wrong when sending a message to the server!");
            System.err.println(ex.getMessage());
            checkIfConnectionIsClosed(ex);
        }
    }
    
    private void checkIfConnectionIsClosed(Exception ex) {
        if(ex.getMessage().equalsIgnoreCase(Constants.CONNECTION_CLOSED)) {
            System.out.println("Shutting down program!");
            serverIsActive = false;
        }
    }
    
    private void isTextValid(String text) throws Exception {
        if(text.trim().isEmpty()) {
            throw new Exception("cannot be empty!");
        }
    }
    
    private class ConsoleOutput implements OutputHandler {
        @Override
        public void handleMsg(String msg) {
            System.out.println(msg);
        }
    }
}
