/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Utilities.DatenbankException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nader
 */
public class TerminkalenderMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        
        try {
            
            Server server = new Server();
            server.start(args);
            
        } catch (RemoteException | AlreadyBoundException | NotBoundException | UnknownHostException | DatenbankException | SQLException ex) {
            Logger.getLogger(TerminkalenderMain.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    }      
    
}
