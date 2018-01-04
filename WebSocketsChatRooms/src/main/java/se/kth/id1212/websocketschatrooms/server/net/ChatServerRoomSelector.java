/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.websocketschatrooms.server.net;

/**
 *
 * @author Johan
 */
import java.util.Scanner;
import javax.websocket.DeploymentException;
import se.kth.id1212.websocketschatrooms.server.model.ChatRoom;

public class ChatServerRoomSelector {

    public static void main(String[] args) {

        org.glassfish.tyrus.server.Server server = new org.glassfish.tyrus.server.Server("localhost", 8025, "/ws", ChatRoom.class);

        try {
            server.start();
            System.out.println("Press any key to stop the servers..");
            new Scanner(System.in).nextLine();
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        } finally {
            server.stop();
        }
    }

}
