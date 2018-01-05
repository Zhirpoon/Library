/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.websocketschatrooms.client.startup;

import se.kth.id1212.websocketschatrooms.client.view.ClientInterpreter;

/**
 *
 * @author Johan
 */
public class Startup {
    public static void main(String[] args) throws Exception {
        ClientInterpreter clientInterpreter = new ClientInterpreter();
        clientInterpreter.startInterpretation();
    }        
}
