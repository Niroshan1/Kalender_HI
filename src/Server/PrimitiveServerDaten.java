/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

/**
 * Diese Klasse erzeugt die benötigten Informationen für den Server
 * 
 */
public class PrimitiveServerDaten {
    
    public final String ownIP;
    public int requestCounter;
    public int sitzungscounter;  
    public String serverID;  
    public int childCounter;
    
    /**
     * 
     * @param ownIP
     * @param serverID 
     */    
    public PrimitiveServerDaten(String ownIP, String serverID){
        this.requestCounter = 0;  
        this.sitzungscounter = 1;
        this.ownIP = ownIP;
        this.serverID = serverID;
        this.childCounter = 0;
    }
    
    /**
     * Methode getNewChildId 
     * 
     * @return id
     */
    public String getNewChildId(){
        String id = serverID + childCounter;
        childCounter++;
        return id;
    }
}
