package se.kth.id1212.sockets.client.view;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
class ThreadSafeStdOut {
    synchronized void print(String output) {
        System.out.print(output);
    }
    
    synchronized void println(String output) {
        System.out.println(output);
    }
}
