package ClientGUI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Terminkalender.BenutzerException;
import Terminkalender.LauncherInterface;
import Terminkalender.Teilnehmer;
import Terminkalender.TerminException;
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
public class TerminInhaltBearbeiten extends javax.swing.JFrame {

    private final LauncherInterface stub;
    private final int sitzungsID;
    private int terminID;
    DefaultListModel teilnehmerListeModel = new DefaultListModel();

    private Integer tag1S;
    private Integer minute1S;
    private Integer minute2S;
    private Integer stunde1S;
    private Integer stunde2S;
    private Integer monat1S;
    private Integer jahr1S;

    /**
     * Creates new form CalenderInhalt
     *
     * @param terminID
     * @param stub
     * @param sitzungsID
     * @throws java.rmi.RemoteException
     * @throws Terminkalender.BenutzerException
     * @throws Terminkalender.TerminException
     */
    public TerminInhaltBearbeiten(int terminID, LauncherInterface stub, int sitzungsID) throws RemoteException, BenutzerException, TerminException {
        initComponents();

        this.terminID = terminID;
        this.stub = stub;
        this.sitzungsID = sitzungsID;
        //datum.setText(stub.getTermin(terminID, sitzungsID).getDatum().toString());
        titel.setText(stub.getTermin(terminID, sitzungsID).getTitel());
        //startZeit.setText(stub.getTermin(terminID, sitzungsID).getBeginn().toString() + " Uhr");
        //endZeit.setText(stub.getTermin(terminID, sitzungsID).getEnde().toString() + " Uhr");
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

    private TerminInhaltBearbeiten() {
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
        //datum.setText(stub.getTermin(terminID, sitzungsID).getDatum().toString());

        //startZeit.setText(stub.getTermin(terminID, sitzungsID).getBeginn().toString());
        //endZeit.setText(stub.getTermin(terminID, sitzungsID).getEnde().toString());
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

    public void bearbeiteTerminInhalt() throws RemoteException, BenutzerException, TerminException, SQLException, Datum.DatumException, Zeit.ZeitException {
        boolean teilnehmerBOOLEAN = false;
        DefaultListModel legen1 = new DefaultListModel();
        teilnehmerliste.setModel(legen1);

        for (Teilnehmer tn : stub.getTermin(terminID, sitzungsID).getTeilnehmerliste()) {
            if (tn.getUsername().equals(stub.getUsername(sitzungsID))) {
                teilnehmerBOOLEAN = tn.checkIstTeilnehmer();
            }
        }
        tag1.setSelectedItem(stub.getTermin(terminID, sitzungsID).getDatum().toString().substring(0, 1));
        monat1.setSelectedItem(stub.getTermin(terminID, sitzungsID).getDatum().toString().substring(3, 4));
        jahr1.setSelectedItem(stub.getTermin(terminID, sitzungsID).getDatum().toString().substring(6, 10));

        //tag2S = Integer.valueOf((String)tag2.getSelectedItem());
        String beginnTime = stub.getTermin(terminID, sitzungsID).getBeginn().toString();
        String[] beginnTimePart = beginnTime.split(":");

        stunde1.setSelectedItem(beginnTimePart[0]);
        minute1.setSelectedItem(beginnTimePart[1]);
        //JOptionPane.showMessageDialog(null, beginnTimePart[1], "Kontakt entfernen - Termin Kalender", JOptionPane.ERROR_MESSAGE);

        String endTime = stub.getTermin(terminID, sitzungsID).getEnde().toString();
        String[] endTimePart = endTime.split(":");
        stunde2.setSelectedItem(endTimePart[0]);
        minute2.setSelectedItem(endTimePart[1]);

        //JOptionPane.showMessageDialog(null, stub.getTermin(terminID, sitzungsID).getEnde().toString().substring(0, 1), "Kontakt entfernen - Termin Kalender", JOptionPane.ERROR_MESSAGE);
        //monat2S = Integer.valueOf((String)monat2.getSelectedItem());
        titel.setText(stub.getTermin(terminID, sitzungsID).getTitel());

        //JOptionPane.showMessageDialog(null, stub.getTermin(terminID, sitzungsID).getTitel(), "InfoBox: titel", JOptionPane.INFORMATION_MESSAGE);
        //datum.setText(stub.getTermin(terminID, sitzungsID).getDatum().toString());
        //startZeit.setText(stub.getTermin(terminID, sitzungsID).getBeginn().toString());
        //endZeit.setText(stub.getTermin(terminID, sitzungsID).getEnde().toString());
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
        if (!teilnehmerBOOLEAN) {
            //bearbeiten.setVisible(false);
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
        start = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        terminersteller = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        titel = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        addTeilnehmerTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        teilnehmerliste = new javax.swing.JList<>();
        jLabel4 = new javax.swing.JLabel();
        addTeilnahmeLabel = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        notiz = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        bearbeitenLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        tag1 = new javax.swing.JComboBox<>();
        monat1 = new javax.swing.JComboBox<>();
        jahr1 = new javax.swing.JComboBox<>();
        stunde1 = new javax.swing.JComboBox<>();
        minute1 = new javax.swing.JComboBox<>();
        stunde2 = new javax.swing.JComboBox<>();
        minute2 = new javax.swing.JComboBox<>();
        ort = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(29, 30, 66));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("Datum");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 83, 28));

        start.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        start.setForeground(new java.awt.Color(240, 240, 240));
        start.setText("Start Zeit");
        jPanel1.add(start, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 83, 35));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("End Zeit");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 81, 27));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("Ort");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 90, 26));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("Terminersteller");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, -1, 30));

        terminersteller.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        terminersteller.setForeground(new java.awt.Color(240, 240, 240));
        terminersteller.setText("ersteller");
        jPanel1.add(terminersteller, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 240, 100, 27));

        jPanel2.setBackground(new java.awt.Color(46, 49, 117));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        titel.setBackground(new java.awt.Color(46, 49, 117));
        titel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        titel.setForeground(new java.awt.Color(240, 240, 240));
        titel.setText("titel");
        titel.setBorder(null);
        jPanel2.add(titel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 420, 44));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 470, 60));

        jPanel3.setBackground(new java.awt.Color(46, 49, 117));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addTeilnehmerTextField.setBackground(new java.awt.Color(29, 30, 66));
        addTeilnehmerTextField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        addTeilnehmerTextField.setForeground(new java.awt.Color(240, 240, 240));
        addTeilnehmerTextField.setToolTipText("");
        addTeilnehmerTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTeilnehmerTextFieldActionPerformed(evt);
            }
        });
        jPanel3.add(addTeilnehmerTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 150, 30));

        teilnehmerliste.setBackground(new java.awt.Color(29, 30, 66));
        teilnehmerliste.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        teilnehmerliste.setForeground(new java.awt.Color(240, 240, 240));
        teilnehmerliste.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(teilnehmerliste);

        jPanel3.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 200, 130));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("Teilnehmer");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        addTeilnahmeLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        addTeilnahmeLabel.setForeground(new java.awt.Color(240, 240, 240));
        addTeilnahmeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        addTeilnahmeLabel.setText("+");
        addTeilnahmeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addTeilnahmeLabelMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                addTeilnahmeLabelMousePressed(evt);
            }
        });
        jPanel3.add(addTeilnahmeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 40, 30));

        jPanel7.setBackground(new java.awt.Color(29, 30, 66));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 40, 30));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 70, 220, 230));

        jPanel4.setBackground(new java.awt.Color(46, 49, 117));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("Notizen");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 122, 37));

        notiz.setBackground(new java.awt.Color(29, 30, 66));
        notiz.setColumns(20);
        notiz.setForeground(new java.awt.Color(240, 240, 240));
        notiz.setRows(5);
        jScrollPane2.setViewportView(notiz);

        jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 450, 90));

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
        bearbeitenLabel.setText("speichern");
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

        tag1.setBackground(new java.awt.Color(46, 49, 117));
        tag1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tag1.setForeground(new java.awt.Color(240, 240, 240));
        tag1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        tag1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tag1ActionPerformed(evt);
            }
        });
        jPanel1.add(tag1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, -1, -1));

        monat1.setBackground(new java.awt.Color(46, 49, 117));
        monat1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        monat1.setForeground(new java.awt.Color(240, 240, 240));
        monat1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", " " }));
        jPanel1.add(monat1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 90, -1, -1));

        jahr1.setBackground(new java.awt.Color(46, 49, 117));
        jahr1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jahr1.setForeground(new java.awt.Color(240, 240, 240));
        jahr1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1900", "1901", "1902", "1903", "1904", "1905", "1906", "1907", "1908", "1909", "1910", "1911", "1912", "1913", "1914", "1915", "1916", "1917", "1918", "1919", "1920", "1921", "1922", "1923", "1924", "1925", "1926", "1927", "1928", "1929", "1930", "1931", "1932", "1933", "1934", "1935", "1936", "1937", "1938", "1939", "1940", "1941", "1942", "1943", "1944", "1945", "1946", "1947", "1948", "1949", "1950", "1951", "1952", "1953", "1954", "1955", "1956", "1957", "1958", "1959", "1960", "1961", "1962", "1963", "1964", "1965", "1966", "1967", "1968", "1969", "1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977", "1978", "1979", "1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989", "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030", "2031", "2032", "2033", "2034", "2035", "2036", "2037", "2038", "2039", "2040", "2041", "2042", "2043", "2044", "2045", "2046", "2047", "2048", "2049", "2050", "2051", "2052", "2053", "2054", "2055", "2056", "2057", "2058", "2059", "2060", "2061", "2062", "2063", "2064", "2065", "2066", "2067", "2068", "2069", "2070", "2071", "2072", "2073", "2074", "2075", "2076", "2077", "2078", "2079", "2080", "2081", "2082", "2083", "2084", "2085", "2086", "2087", "2088", "2089", "2090", "2091", "2092", "2093", "2094", "2095", "2096", "2097", "2098", "2099", "2100" }));
        jPanel1.add(jahr1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 90, -1, -1));

        stunde1.setBackground(new java.awt.Color(46, 49, 117));
        stunde1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        stunde1.setForeground(new java.awt.Color(240, 240, 240));
        stunde1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", " " }));
        jPanel1.add(stunde1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 130, -1, -1));

        minute1.setBackground(new java.awt.Color(46, 49, 117));
        minute1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        minute1.setForeground(new java.awt.Color(240, 240, 240));
        minute1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60" }));
        jPanel1.add(minute1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 130, -1, -1));

        stunde2.setBackground(new java.awt.Color(46, 49, 117));
        stunde2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        stunde2.setForeground(new java.awt.Color(240, 240, 240));
        stunde2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", " " }));
        jPanel1.add(stunde2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, -1, -1));

        minute2.setBackground(new java.awt.Color(46, 49, 117));
        minute2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        minute2.setForeground(new java.awt.Color(240, 240, 240));
        minute2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60" }));
        minute2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minute2ActionPerformed(evt);
            }
        });
        jPanel1.add(minute2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 170, -1, -1));

        ort.setBackground(new java.awt.Color(29, 30, 66));
        ort.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        ort.setForeground(new java.awt.Color(240, 240, 240));
        ort.setText("jTextField1");
        ort.setBorder(null);
        ort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ortActionPerformed(evt);
            }
        });
        jPanel1.add(ort, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 210, 140, -1));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 230, 140, 10));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 490, 520));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void addTeilnehmerTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTeilnehmerTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addTeilnehmerTextFieldActionPerformed

    private void bearbeitenLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bearbeitenLabelMouseClicked
        try {
            tag1S = Integer.valueOf((String) tag1.getSelectedItem());
            //tag2S = Integer.valueOf((String)tag2.getSelectedItem());
            minute1S = Integer.valueOf((String) minute1.getSelectedItem());
            minute2S = Integer.valueOf((String) minute2.getSelectedItem());
            stunde1S = Integer.valueOf((String) stunde1.getSelectedItem());
            stunde2S = Integer.valueOf((String) stunde2.getSelectedItem());
            monat1S = Integer.valueOf((String) monat1.getSelectedItem());
            //monat2S = Integer.valueOf((String)monat2.getSelectedItem());
            jahr1S = Integer.valueOf((String) jahr1.getSelectedItem());

            stub.changeTermintitel(terminID, titel.getText(), sitzungsID);
            stub.changeTermindatum(terminID, new Datum(tag1S, monat1S, jahr1S), sitzungsID);
            stub.changeTerminbeginn(terminID, new Zeit(stunde1S, minute1S), sitzungsID);
            stub.changeTerminbeginn(terminID, new Zeit(stunde2S, minute2S), sitzungsID);
            stub.changeTerminort(terminID, ort.getText(), sitzungsID);
            stub.changeTerminnotiz(terminID, notiz.getText(), sitzungsID);

            TerminInhalt startagain = new TerminInhalt(terminID, stub, sitzungsID);
            startagain.setVisible(true);

        } catch (BenutzerException | RemoteException | TerminException | SQLException | Datum.DatumException | Zeit.ZeitException ex) {
            Logger.getLogger(TerminInhaltBearbeiten.class.getName()).log(Level.SEVERE, null, ex);
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

    private void addTeilnahmeLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addTeilnahmeLabelMouseClicked
        // TODO add your handling code here:
        addTeilnahmeLabel.setForeground(Color.gray);

        String username = addTeilnehmerTextField.getText();
        if (username.length() > 0) {
            try {
                stub.addTerminteilnehmer(terminID, username, sitzungsID);
                fillTeilnehmerlist();
                addTeilnehmerTextField.setText("");

            } catch (RemoteException | BenutzerException | SQLException | TerminException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Teilnehmer hinzufügen - Terminansicht", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_addTeilnahmeLabelMouseClicked

    private void addTeilnahmeLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addTeilnahmeLabelMousePressed
        // TODO add your handling code here:
        addTeilnahmeLabel.setForeground(Color.white);
    }//GEN-LAST:event_addTeilnahmeLabelMousePressed

    private void bearbeitenLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bearbeitenLabelMousePressed
        // TODO add your handling code here:
        bearbeitenLabel.setForeground(Color.white);
    }//GEN-LAST:event_bearbeitenLabelMousePressed

    private void tag1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tag1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tag1ActionPerformed

    private void minute2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minute2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minute2ActionPerformed

    private void ortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ortActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TerminInhaltBearbeiten.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TerminInhaltBearbeiten.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TerminInhaltBearbeiten.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TerminInhaltBearbeiten.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TerminInhaltBearbeiten().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addTeilnahmeLabel;
    private javax.swing.JTextField addTeilnehmerTextField;
    private javax.swing.JLabel bearbeitenLabel;
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
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JComboBox<String> jahr1;
    private javax.swing.JComboBox<String> minute1;
    private javax.swing.JComboBox<String> minute2;
    private javax.swing.JComboBox<String> monat1;
    private javax.swing.JTextArea notiz;
    private javax.swing.JTextField ort;
    private javax.swing.JLabel start;
    private javax.swing.JComboBox<String> stunde1;
    private javax.swing.JComboBox<String> stunde2;
    private javax.swing.JComboBox<String> tag1;
    private javax.swing.JList<String> teilnehmerliste;
    private javax.swing.JLabel terminersteller;
    private javax.swing.JTextField titel;
    // End of variables declaration//GEN-END:variables
}
