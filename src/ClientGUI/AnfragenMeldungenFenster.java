/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientGUI;

import Server.ClientStub;
import Utilities.Anfrage;
import Utilities.BenutzerException;
import Utilities.Meldung;

import java.awt.*;
import Utilities.TerminException;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author med
 */
public class AnfragenMeldungenFenster extends javax.swing.JFrame {
    
    private final ClientStub stub;
    private final int sitzungsID;
    private final Meldung meldung;
    Hauptfenster hauptfenster;
    
    /**
     * Create new event
     * @param stub
     * @param sitzungsID
     * @param meldung
     * @param hauptfenster
     */
    public AnfragenMeldungenFenster(ClientStub stub, int sitzungsID, Meldung meldung, Hauptfenster hauptfenster) {
        this.stub = stub;
        this.sitzungsID = sitzungsID;
        this.meldung = meldung;
        this.hauptfenster = hauptfenster;
        
        initComponents();
        
        setColor();

        meldungsText.setText(meldung.getText());
        //Wenn es eine Meldung ist, welche auf keine Antwort wartet, annehmen/ablehnen unsichtbar machen
        if(!(meldung instanceof Anfrage) || meldung.gelesen){
            buttonAnnehmen.setVisible(false);
            buttonAblehnen.setVisible(false);
            jPanel5.setVisible(false);
            jLabel7.setText("Nachricht:");
        }
        else{
            buttonDelete.setVisible(false);
            jPanel7.setVisible(false);
        }
        setTitle("");
    }
    
    private void setColor() throws RemoteException, BenutzerException{
        
        Color[] color = stub.getColor(sitzungsID);
        Color color1 = color[0];
        Color color2 = color[1];
        Color color3 = color[2];
        Color color4 = color[3];

        
        //Light
        jPanel3.setBackground(color1);
        jPanel5.setBackground(color1);
        jPanel6.setBackground(color1);
        jPanel7.setBackground(color1);
        
        
        //Middle
        jPanel2.setBackground(color2);
        
        //Font 
        jLabel7.setForeground(color4);
        meldungsText.setForeground(color4);
        buttonAblehnen.setForeground(color4);
        buttonAnnehmen.setForeground(color4);
        buttonDelete.setForeground(color4);
        
    }
    
    /**
     * methode zum schliessen von vorherigem fenster
     */
    public void fensterClose(){
        WindowEvent winClosingEvent = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(winClosingEvent);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        meldungsText = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        buttonAnnehmen = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        buttonAblehnen = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        buttonDelete = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Benachrichtigung Event");
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(29, 30, 66));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(46, 49, 117));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        meldungsText.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        meldungsText.setForeground(new java.awt.Color(240, 240, 240));
        jPanel3.add(meldungsText, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 51, 460, 160));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("Meldung:");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 484, 220));

        jPanel5.setBackground(new java.awt.Color(46, 49, 117));
        jPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel5MouseClicked(evt);
            }
        });

        buttonAnnehmen.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        buttonAnnehmen.setForeground(new java.awt.Color(240, 240, 240));
        buttonAnnehmen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        buttonAnnehmen.setText("Annehmen");
        buttonAnnehmen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonAnnehmenMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonAnnehmen, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonAnnehmen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 240, 110, 30));

        jPanel6.setBackground(new java.awt.Color(46, 49, 117));
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
        });

        buttonAblehnen.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        buttonAblehnen.setForeground(new java.awt.Color(240, 240, 240));
        buttonAblehnen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        buttonAblehnen.setText("Ablehnen");
        buttonAblehnen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonAblehnenMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonAblehnen, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonAblehnen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 240, 110, -1));

        jPanel7.setBackground(new java.awt.Color(46, 49, 117));
        jPanel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel7MouseClicked(evt);
            }
        });

        buttonDelete.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        buttonDelete.setForeground(new java.awt.Color(240, 240, 240));
        buttonDelete.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        buttonDelete.setText("Löschen");
        buttonDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonDeleteMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonDelete, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonDelete, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 240, 110, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void buttonAnnehmenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonAnnehmenMouseClicked
        try {
            Anfrage anfrage = (Anfrage) meldung;
              
            stub.terminAnnehmen(anfrage.getTermin().getID(), sitzungsID);
            JOptionPane.showMessageDialog(null, "Einladung wurde angennomen");

            hauptfenster.fillMeldList();
            hauptfenster.displayDate();
            dispose();     
        } catch (TerminException | SQLException | RemoteException | BenutzerException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "buttonAnnehmenMouseClicked", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_buttonAnnehmenMouseClicked

    private void jPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jPanel5MouseClicked

    private void buttonAblehnenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonAblehnenMouseClicked
        try {
            Anfrage anfrage = (Anfrage) meldung;
              
            stub.terminAblehnen(anfrage.getTermin().getID(), sitzungsID);
            JOptionPane.showMessageDialog(null, "Einladung wurde abgelehnt");

            hauptfenster.fillMeldList();
            hauptfenster.displayDate();
            dispose();     
        } catch (TerminException | SQLException | RemoteException | BenutzerException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "buttonAblehnenMouseClicked", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_buttonAblehnenMouseClicked

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jPanel6MouseClicked

    private void buttonDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonDeleteMouseClicked
        try {
            stub.deleteMeldung(meldung.meldungsID , sitzungsID);
            hauptfenster.fillMeldList();          
            JOptionPane.showMessageDialog(null, "Deine Benachrichtigung wurde gelöscht");
            
            hauptfenster.fillMeldList();
            hauptfenster.displayDate();
            dispose();
        } catch (RemoteException | BenutzerException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "buttonDeleteMouseClicked", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_buttonDeleteMouseClicked

    private void jPanel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel7MouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jPanel7MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel buttonAblehnen;
    private javax.swing.JLabel buttonAnnehmen;
    private javax.swing.JLabel buttonDelete;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel meldungsText;
    // End of variables declaration//GEN-END:variables
}
