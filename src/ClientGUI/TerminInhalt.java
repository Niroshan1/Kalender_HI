package ClientGUI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Server.ClientStub;
import Utilities.BenutzerException;
import Utilities.Datum;
import Utilities.Teilnehmer;
import Utilities.TerminException;
import Utilities.Zeit;
import java.awt.Color;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author niroshan
 */
public class TerminInhalt extends javax.swing.JFrame {

    private final ClientStub stub;
    private final int sitzungsID;
    private int terminID;
    DefaultListModel teilnehmerListeModel = new DefaultListModel();

    /**
     * Creates new form CalenderInhalt
     *
     * @param terminID
     * @param stub
     * @param sitzungsID
     * @throws java.rmi.RemoteException
     * @throws Utilities.BenutzerException
     * @throws Utilities.TerminException
     */
    public TerminInhalt(int terminID, ClientStub stub, int sitzungsID) throws RemoteException, BenutzerException, TerminException {
        initComponents();

        this.terminID = terminID;
        this.stub = stub;
        this.sitzungsID = sitzungsID;
        datum.setText(stub.getTermin(terminID, sitzungsID).getDatum().toString());
        titel.setText(stub.getTermin(terminID, sitzungsID).getTitel());
        startZeit.setText(stub.getTermin(terminID, sitzungsID).getBeginn().toString() + " Uhr");
        endZeit.setText(stub.getTermin(terminID, sitzungsID).getEnde().toString() + " Uhr");
        ort.setText(stub.getTermin(terminID, sitzungsID).getOrt());
        terminersteller.setText(stub.getTermin(terminID, sitzungsID).getOwner());
        teilnehmerliste.setModel(teilnehmerListeModel);
        notiz.setText(stub.getTermin(terminID, sitzungsID).getNotiz());
        fillTeilnehmerlist();

    }

    private void fillTeilnehmerlist() throws RemoteException, BenutzerException, TerminException {
        teilnehmerListeModel.clear();
        for (Teilnehmer teilnehmer : stub.getTermin(terminID, sitzungsID).getTeilnehmerliste()) {
            if (teilnehmer.checkIstTeilnehmer()) {
                teilnehmerListeModel.addElement(teilnehmer.getUsername() + " (nimmt Teil)");
            } else {
                teilnehmerListeModel.addElement(teilnehmer.getUsername() + " (offen)");
            }

        }
    }

    private TerminInhalt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //.setModel(legen1);
    public void zeigeTerminInhalt() throws RemoteException, BenutzerException, TerminException {
        boolean teilnehmerBoolean = false;
        DefaultListModel legen1 = new DefaultListModel();
        teilnehmerliste.setModel(legen1);

        for (Teilnehmer tn : stub.getTermin(terminID, sitzungsID).getTeilnehmerliste()) {
            if (tn.getUsername().equals(stub.getUsername(sitzungsID))) {
                teilnehmerBoolean = tn.checkIstTeilnehmer();
            }
        }

        titel.setText(stub.getTermin(terminID, sitzungsID).getTitel());
        JOptionPane.showMessageDialog(null, stub.getTermin(terminID, sitzungsID).getTitel(), "InfoBox: titel", JOptionPane.INFORMATION_MESSAGE);
        datum.setText(stub.getTermin(terminID, sitzungsID).getDatum().toString());

        startZeit.setText(stub.getTermin(terminID, sitzungsID).getBeginn().toString());
        endZeit.setText(stub.getTermin(terminID, sitzungsID).getEnde().toString());

        if (stub.getTermin(terminID, sitzungsID).getNotiz().length() > 20) {
            notiz.setText(stub.getTermin(terminID, sitzungsID).getNotiz().substring(0, 20) + "...(5)");
        }

        if (stub.getTermin(terminID, sitzungsID).getEditierbar()) {
            //TODO SETBUTTON
            System.out.println("Bearbeitungsrecht: Jeder");
        } else {
            notiz.setText(stub.getTermin(terminID, sitzungsID).getNotiz() + "(5)");
        }
        ort.setText(stub.getTermin(terminID, sitzungsID).getOrt());

        terminersteller.setText(stub.getTermin(terminID, sitzungsID).getOwner());
        //System.out.println("Termin löschen");
        //System.out.println("zurück(0)");
        //System.out.print("Eingabe: ");
        notiz.setText(stub.getTermin(terminID, sitzungsID).getNotiz());

        for (Teilnehmer teilnehmer : stub.getTermin(terminID, sitzungsID).getTeilnehmerliste()) {
            //getteilnehmer.setText(teilnehmer.getUsername());

            if (teilnehmer.checkIstTeilnehmer()) {
                legen1.addElement(teilnehmer.getUsername() + " (nimmt Teil)");
            } else {
                legen1.addElement(teilnehmer.getUsername() + " (noch offen)");
            }
        }
        if (!teilnehmerBoolean) {
            bearbeitenLabel.setVisible(false);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        datum = new javax.swing.JLabel();
        start = new javax.swing.JLabel();
        startZeit = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        endZeit = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ort = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        terminersteller = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        titel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        teilnehmerliste = new javax.swing.JList<>();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        notiz = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        bearbeitenLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(29, 30, 66));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("Datum");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 83, 28));

        datum.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        datum.setForeground(new java.awt.Color(240, 240, 240));
        datum.setText("datum");
        jPanel1.add(datum, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 80, 171, 28));

        start.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        start.setForeground(new java.awt.Color(240, 240, 240));
        start.setText("Start Zeit");
        jPanel1.add(start, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 83, 35));

        startZeit.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        startZeit.setForeground(new java.awt.Color(240, 240, 240));
        startZeit.setText("startZeit");
        jPanel1.add(startZeit, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 120, 90, 28));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("End Zeit");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 81, 27));

        endZeit.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        endZeit.setForeground(new java.awt.Color(240, 240, 240));
        endZeit.setText("endZeit");
        jPanel1.add(endZeit, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 160, 144, 28));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("Ort");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 90, 26));

        ort.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        ort.setForeground(new java.awt.Color(240, 240, 240));
        ort.setText("Ort");
        jPanel1.add(ort, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, 124, 26));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("Terminersteller");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, -1, 30));

        terminersteller.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        terminersteller.setForeground(new java.awt.Color(240, 240, 240));
        terminersteller.setText("ersteller");
        jPanel1.add(terminersteller, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 240, 132, 27));

        jPanel2.setBackground(new java.awt.Color(46, 49, 117));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        titel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        titel.setForeground(new java.awt.Color(240, 240, 240));
        titel.setText("Titel");
        jPanel2.add(titel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 360, 43));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 470, 60));

        jPanel3.setBackground(new java.awt.Color(46, 49, 117));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        teilnehmerliste.setBackground(new java.awt.Color(29, 30, 66));
        teilnehmerliste.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        teilnehmerliste.setForeground(new java.awt.Color(240, 240, 240));
        teilnehmerliste.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(teilnehmerliste);

        jPanel3.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 200, 180));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("Teilnehmer");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 70, 220, 230));

        jPanel4.setBackground(new java.awt.Color(46, 49, 117));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("Notizen");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 122, 37));

        notiz.setEditable(false);
        notiz.setBackground(new java.awt.Color(29, 30, 66));
        notiz.setColumns(20);
        notiz.setForeground(new java.awt.Color(240, 240, 240));
        notiz.setRows(5);
        jScrollPane2.setViewportView(notiz);

        jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 450, 100));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 470, 140));

        jPanel5.setBackground(new java.awt.Color(46, 49, 117));
        jPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel5MouseClicked(evt);
            }
        });

        bearbeitenLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        bearbeitenLabel.setForeground(new java.awt.Color(240, 240, 240));
        bearbeitenLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bearbeitenLabel.setText("bearbeiten");
        bearbeitenLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bearbeitenLabelMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bearbeitenLabelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bearbeitenLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(bearbeitenLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 470, 130, 30));

        jPanel6.setBackground(new java.awt.Color(46, 49, 117));
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("abbrechen");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 470, -1, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 490, 520));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void bearbeitenLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bearbeitenLabelMouseClicked
        // TODO add your handling code here:
        bearbeitenLabel.setForeground(Color.gray);
        try {
            // TODO add your handling code here:
            TerminInhaltBearbeiten tIB = new TerminInhaltBearbeiten(terminID, stub, sitzungsID);
            this.dispose();
            tIB.setVisible(true);
            tIB.bearbeiteTerminInhalt();
        } catch (RemoteException | SQLException | BenutzerException | TerminException | Datum.DatumException | Zeit.ZeitException ex) {
            Logger.getLogger(TerminInhalt.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }//GEN-LAST:event_bearbeitenLabelMouseClicked

    private void jPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseClicked

    }//GEN-LAST:event_jPanel5MouseClicked

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel6MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jLabel8MouseClicked

    private void bearbeitenLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bearbeitenLabelMousePressed
        // TODO add your handling code here:
        bearbeitenLabel.setForeground(Color.white);
    }//GEN-LAST:event_bearbeitenLabelMousePressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TerminInhalt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new TerminInhalt().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bearbeitenLabel;
    private javax.swing.JLabel datum;
    private javax.swing.JLabel endZeit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea notiz;
    private javax.swing.JLabel ort;
    private javax.swing.JLabel start;
    private javax.swing.JLabel startZeit;
    private javax.swing.JList<String> teilnehmerliste;
    private javax.swing.JLabel terminersteller;
    private javax.swing.JLabel titel;
    // End of variables declaration//GEN-END:variables
}
