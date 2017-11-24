package se.kth.id1212.sockets.client.view;

import java.util.Scanner;
import se.kth.id1212.sockets.client.controller.Controller;
import se.kth.id1212.sockets.client.net.OutputHandler;
/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class NonBlockingInterpreter implements Runnable {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private boolean receivingCmds = false;
    private Controller controller;
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    
    public void start() {
        if(receivingCmds) {
            return;
        }
        receivingCmds = true;
        controller = new Controller();
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        while(receivingCmds) {
            try {
                CmdLine cmdLine =  new CmdLine(readNextLine());
                switch(cmdLine.getCmd()) {
                    case QUIT:
                        receivingCmds = false;
                        controller.disconnect();
                        break;
                    case START:
                        controller.startGame();
                        break;
                    case SOLVE:
                        controller.solveWord(cmdLine.getParameter(0));
                        break;
                    case CONNECT:
                        controller.connect(cmdLine.getParameter(0), Integer.parseInt(cmdLine.getParameter(1)), new ConsoleOutput());
                        break;
                    case GUESS:
                        char[] chrArray = cmdLine.getParameter(0).toCharArray();
                        if(chrArray.length != 1) {
                            break;
                        }
                        controller.guessLetter(chrArray[0]);
                        break;
                    default: 
                } 
            } catch(Exception e) {
                outMgr.println("Operation failed!");
            }
        }
    }
    
    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }
    
    private class ConsoleOutput implements OutputHandler {
        @Override
        public void handleMsg(String msg) {
            outMgr.println((String) msg);
            outMgr.print(PROMPT);
        }
    }
}
