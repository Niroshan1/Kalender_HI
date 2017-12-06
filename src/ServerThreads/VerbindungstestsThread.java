/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

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
    private final String ownIP;
    
    public VerbindungstestsThread(LinkedList<Verbindung> connectionList, Verbindung verbindung, String ownIP){
        this.connectionList = connectionList;
        this.verbindung = verbindung;
        this.ownIP = ownIP;
    }    
    
    @Override 
    public void run(){
        Counter counter = new Counter();
        boolean serverUp = true;
        
        while(serverUp){
            try {
                Thread.sleep(3000);
                System.out.println("Teste " + this.verbindung.getIP() + " | Counter = " + counter.getValue());
                counter.decrement();
                
                //starte Thread der Server anpingt               
                new PingThread(this.verbindung.getServerStub(), counter).start();
                
                if(counter.getValue() == 0){
                    System.out.println("--->> " + this.verbindung.getIP() + " kann nicht mehr erreicht werden");
                    this.connectionList.remove(this.verbindung);
                    //TODO: entferne aus onlineServerList
                    for(Verbindung connection : this.connectionList){
                        new FloodingThreadEntferneServerAusSystem(connection.getServerStub(), this.verbindung.getIP(), this.ownIP).start();
                    }
                    serverUp = false;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(VerbindungstestsThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}
