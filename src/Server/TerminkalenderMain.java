/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Utilities.DatenbankException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
 *
 * @author nader
 */
public class TerminkalenderMain {

    /**
     * @param args the command line arguments
     * @throws org.omg.CORBA.portable.RemarshalException
     */
    public static void main(String[] args) throws RemarshalException, FileNotFoundException, IOException, RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException, SQLException, DatenbankException, ClassNotFoundException, NoSuchAlgorithmException {
        File file = new File("serverIP.txt");
        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            if (file.exists()) {
                System.out.println("LOG * ");
                System.out.println("LOG * Datei wird ausgelesen!");
                br = new BufferedReader(new FileReader(new File("serverIP.txt")));
                String line = null;
                while ((line = br.readLine()) != null) {
                    // Ganze Zeile:
                    // System.out.println(line);               
                    String[] parts = line.split(" ");
                    System.out.println("LOG * LOG * OwnIP: " + parts[0]);
                    System.out.println("ParentIP: " + parts[1]);
                    
                }
                Server server = new Server(args);
                server.start();
            } else if (args.length == 2) {
                System.out.println("LOG * ");
                System.out.println("LOG * Datei wird neu angelegt!");
                bw = new BufferedWriter(new FileWriter("serverIP.txt"));
                bw.write(args[0] + " " + args[1]);

                Server server = new Server(args);
                server.start();
            } else {
                System.out.println("Eingabeparameter: <Eigene IP> <Parent IP>");
            }
        } catch (RemoteException | AlreadyBoundException | NotBoundException | UnknownHostException | DatenbankException | SQLException ex) {
            Logger.getLogger(TerminkalenderMain.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (bw != null) {
                bw.flush();
                bw.close();
            }
        }

    }

}
