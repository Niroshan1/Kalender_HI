/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

import Server.ServerStub;
import Server.Verbindung;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author timtim
 */
public class VerbindungstestsThread extends Thread{
    
    private final LinkedList<Verbindung> connectionList;
    private final Verbindung verbindung;
    
    public VerbindungstestsThread(LinkedList<Verbindung> connectionList, Verbindung verbindung){
        this.connectionList = connectionList;
        this.verbindung = verbindung;
    }    
    
    @Override 
    public void run(){
        int counter = 3;
        boolean serverUp = true;
        
        while(serverUp){
            try {
                Thread.sleep(3000);
                System.out.println("Teste " + this.verbindung.getIP() + " | Counter = " + counter);
                counter--;
                //TODO: starte Thread mit Parameter counter                
                
                if(counter == 0){
                    this.connectionList.remove(this.verbindung);
                    //
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(VerbindungstestsThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}
