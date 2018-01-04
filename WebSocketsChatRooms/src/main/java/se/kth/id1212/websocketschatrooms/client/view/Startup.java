/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.websocketschatrooms.client.view;

import java.net.URI;
import java.util.Scanner;
import javax.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;
import se.kth.id1212.websocketschatrooms.client.net.ClientEndpoint;
import static se.kth.id1212.websocketschatrooms.util.JsonUtil.formatMessage;

/**
 *
 * @author Johan
 */
public class Startup {
   
    public static String SERVER = "ws://localhost:8025/ws/chat";

    public static void main(String[] args) throws Exception {
        ClientManager client = ClientManager.createClient();
        String message;

        // connect to server
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Tiny Chat!");
        System.out.println("What's your name?");
        String user = scanner.nextLine();
        System.out.println("Which room would you like to join?");
        String room = scanner.nextLine();
        SERVER = SERVER + "/" + room;
        Session session = client.connectToServer(ClientEndpoint.class, new URI(SERVER));
        System.out.println("You are logged in as: " + user);
        resetServerAddress();
        // repeatedly read a message and send it to the server (until quit)
        do {
            message = scanner.nextLine();
            session.getBasicRemote().sendText(formatMessage(message, user));
            if("leave".equalsIgnoreCase(message)) {
                System.out.println("What's your name?");
                user = scanner.nextLine();
                System.out.println("Which room would you like to join?");
                room = scanner.nextLine();
                SERVER = SERVER + "/" + room;
                session = client.connectToServer(ClientEndpoint.class, new URI(SERVER));
                System.out.println("You are logged in as: " + user); 
            }
        } while (!message.equalsIgnoreCase("quit"));
    }
    
    private static void resetServerAddress() {
        SERVER = "ws://localhost:8025/ws/chat";
    }
}
