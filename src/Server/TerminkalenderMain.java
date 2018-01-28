/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Server.Utilities.DatenbankException;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * test
 *
 * @author nader
 */
public class TerminkalenderMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  {
        
        if (args.length == 2) {
            System.out.println("LOG * ");

            try {                    
                Server server;
                server = new Server(args);
                server.start();
            } catch (ClassNotFoundException | SQLException | NoSuchAlgorithmException | AlreadyBoundException | IOException | DatenbankException ex) {
                Logger.getLogger(TerminkalenderMain.class.getName()).log(Level.SEVERE, null, ex);
            }              
        } else {
            System.out.println("Eingabeparameter: <Eigene IP> <Parent IP>");
        }

    }

}
