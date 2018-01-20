/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Utilities.DatenbankException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.portable.RemarshalException;

/**
 * test
 * @author nader
 */
public class TerminkalenderMain {

    /**
     * @param args the command line arguments
     * @throws org.omg.CORBA.portable.RemarshalException
     */
    public static void main(String[] args) throws RemarshalException{        
        try {  
            if(args.length == 2){
                Server server = new Server(args);
                server.start();   
            }
            else{
                System.out.println("Eingabeparameter: <Eigene IP> <Parent IP>");
            }
              
            
        } catch (RemoteException | AlreadyBoundException | NotBoundException | UnknownHostException | DatenbankException | SQLException ex) {
            Logger.getLogger(TerminkalenderMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException | NoSuchAlgorithmException | IOException ex) {
            Logger.getLogger(TerminkalenderMain.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    }      
    
}
