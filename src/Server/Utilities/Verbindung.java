/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Utilities;

import Server.ServerStub;

/**
 *
 * @author timtim
 */
public class Verbindung {
    
    // Variablen Deklaration und Initialisierung
    private final ServerStub stub;
    private final String ip;
    private final String id;
    
    /**
     * Konstruktur
     * @param stub
     * @param ip
     * @param id
     */
    public Verbindung(ServerStub stub, String ip, String id){
        this.stub = stub;
        this.ip = ip;
        this.id = id;
    }
    
    /**
     * Gibt den ServerStub (also Serveranbidung) zurueck
     * @return 
     */
    public ServerStub getServerStub(){
        return this.stub;
    }
    
    /**
     * Gibt die IP der Verbindung zurueck
     * @return 
     */
    public String getIP(){
        return this.ip;
    }
    
    /**
     * Gibt die ID der Verbindung zurueck
     * @return 
     */
    public String getID(){
        return this.id;
    }
    
    /**
     * Laesst eigene Server-IP mit anderer Server-IP vergleichen 
     * @param ip
     * @return 
     */
    public boolean equals(String ip){
        return this.ip.equals(ip);
    }
    
    
}
