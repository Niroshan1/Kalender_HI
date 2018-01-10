/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

import Server.ServerDaten;
import Server.Verbindung;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author timtim
 */
public class VerbindungstestsThread extends Thread{
    
    private final ServerDaten serverDaten;
    private final Verbindung verbindung;
    
    public VerbindungstestsThread(ServerDaten serverDaten, Verbindung verbindung){
        this.serverDaten = serverDaten;
        this.verbindung = verbindung;
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
                new PingThread(this.verbindung.getServerStub(), counter, serverDaten).start();
                
                //test ob keine verbindung mehr zu anderem server
                if(counter.getValue() <= 0){
                    
                    //Wenn Verbindung zu Parent war, versuche erneut Verbindung zu diesem aufzubauen                
                    if(this.serverDaten.parent != null && this.serverDaten.parent.getIP().equals(this.verbindung.getIP())){
                        System.out.println("--->> Versuche neue Verbindung zu Parent aufzubauen");
                        this.serverDaten.connectToParent();
                    }
                    else{
                        //TODO: ..
                    }                  
                    
                    //beende Schleife
                    serverUp = false;
                }                
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(VerbindungstestsThread.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }
    }
}
