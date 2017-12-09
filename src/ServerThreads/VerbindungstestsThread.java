/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThreads;

import Server.ServerDaten;
import Server.ServerStubImpl;
import Server.Verbindung;
import java.rmi.RemoteException;
import java.util.LinkedList;
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
                System.out.println("onlineserverliste:");
                for(String bla : this.serverDaten.onlineServerList){
                    System.out.println(bla);
                }
                
                System.out.println("Teste " + this.verbindung.getIP() + " | Counter = " + counter.getValue());
                counter.decrement();
                
                //starte Thread der Server anpingt               
                new PingThread(this.verbindung.getServerStub(), counter).start();
                
                if(counter.getValue() == 0){
                    System.out.println("--->> " + this.verbindung.getIP() + " kann nicht mehr erreicht werden");
                    this.serverDaten.connectionList.remove(this.verbindung);
                    this.serverDaten.onlineServerList.remove(this.verbindung);
                    
                    for(Verbindung connection : this.serverDaten.connectionList){
                        new Thread(() -> {
                            try {
                                
                                connection.getServerStub().entferneServerAusSystem(this.verbindung.getIP(), this.serverDaten.ownIP);
                            }catch (RemoteException ex) {
                                System.out.println("HIHIHHIIHHIHHIHIHHIHHIIHIH");
                                Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }).start();
                    }
                    serverUp = false;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(VerbindungstestsThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}
