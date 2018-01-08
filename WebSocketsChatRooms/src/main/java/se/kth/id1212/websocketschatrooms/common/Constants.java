package se.kth.id1212.websocketschatrooms.common;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class Constants {
    public static final String SERVERADDRESS = "ws://localhost:8025/ws/chat";
    public static final String HELP = "#help";
    public static final String WELCOME_MESSAGE = "Welcome to the chat program! \n"
                + "To get a list of command type \"" + HELP + "\"";
    public static final String CHANGE = "#change";
    public static final String QUIT = "#quit";
    public static final String CONNECT = "#connect";
    public static final String LIST_ROOMS = "#list_rooms";
    public static final String CONNECTION_CLOSED = "The connection has been closed.";
    public static final String HELP_MENU = "\"" + HELP + "\" shows all the commands.\n" +
            "\"" + CONNECT + "\" connects to the server.\n" +
            "\"" + CHANGE + "\" changes the chat room and username you are connected to and with.\n" +
            "\"" + QUIT + "\" quits the program.\n" +
            "\"" + LIST_ROOMS + "\" lists all the rooms on the server when you're connected.";
            
}
