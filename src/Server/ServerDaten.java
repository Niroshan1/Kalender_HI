/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Utilities.DBHandler;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 *
 * @author timtim
 */
public class ServerDaten {
    
    public final LinkedList<Verbindung> connectionList;
    public LinkedList<String> onlineServerList;
    public final DBHandler datenbank;
    public final String ownIP;
    
    public ServerDaten(String ownIP) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{
        this.connectionList = new LinkedList<>();
        this.onlineServerList = new LinkedList<>();
        this.ownIP = ownIP;
        datenbank = new DBHandler(); 
        datenbank.getConnection();        
    }
}
