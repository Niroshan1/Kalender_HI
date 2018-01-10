/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Utilities.DBHandler;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 *
 * @author timtim
 */
public class ServerDaten {
    
    public Verbindung parent; 
    public Verbindung leftchild;
    public Verbindung rightchild;
    public DBHandler datenbank;
    public final String ownIP;
    
    public ServerDaten(String ownIP) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{    
        parent = null;
        leftchild = null;
        rightchild = null;
        this.ownIP = ownIP;
        datenbank = null;      
    }
    
    public void ladeDatenbank() throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{
        datenbank = new DBHandler(); 
        datenbank.getConnection(); 
    }

    void ladeDatenbankFromParent() {
        //TODO: lade DB von Parent (mit Stub-Methode)
    }
    
    

 
}
