/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

/**
 *
 * @author timtim
 */
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBHandler {
    boolean abfrage;
    private static Connection con;
    private static boolean hasData;
    ResultSet rs;
    private BenutzerListe benutzerliste;
    
    public DBHandler(){
        abfrage = true;
        hasData = false;
        con = null;
    }   
    
    public void getConnection() throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {       
        if(con == null){
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:Kalender.db");
            initialise();  
        }       
    }
    
    private void initialise() throws SQLException, NoSuchAlgorithmException{
        if(!hasData){
            hasData = true;
            Statement state1 = con.createStatement();
            Statement state2 = con.createStatement();
            Statement state3 = con.createStatement();
            Statement state4 = con.createStatement();
            Statement state5 = con.createStatement();
            Statement state6 = con.createStatement();
            Statement state7 = con.createStatement();
            
            ResultSet res1 = state1.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='benutzer'");
            ResultSet res2 = state2.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='termine'");
            ResultSet res3 = state3.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='terminkalender'");
            ResultSet res4 = state4.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='kontaktliste'");
            ResultSet res5 = state5.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='meldungen'");
            ResultSet res6 = state6.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='anfragen'");
            ResultSet res7 = state7.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='benutzerliste'");
            
            if(!res1.next()){
                System.out.println("Building the User table with prepopulated values.");
                Statement stateBenutzer = con.createStatement();
                stateBenutzer.execute("CREATE TABLE benutzer(userID integer,"
                        + "username varchar(60),"
                        + "email varchar(100),"
                        + "name varchar(60),"
                        + "lastname varchar(60),"
                        + "password varchar(60),"
                        + "meldungsCounter integer,"
                        + "terminCounter integer,"
                        + "PRIMARY KEY (username))");
            }
            
            if(!res2.next()){
                System.out.println("Building the Termin table with prepopulated values.");
                Statement stateTermine = con.createStatement();
                stateTermine.execute("CREATE TABLE termine(terminID integer,"
                        + "titel varchar(60),"
                        + "day intger,"
                        + "month integer,"
                        + "year integer,"
                        + "from_hours integer,"
                        + "from_minutes integer,"
                        + "to_hours integer,"
                        + "to_minutes integer,"
                        + "note varchar(100),"
                        + "location varchar(60),"
                        + "ownerID integer,"
                        + "editEveryone integer,"
                        + "foreign key(ownerID) references benutzer(userID),"
                        + "primary key(terminID))");
            }
            
            if(!res3.next()){
                System.out.println("Building the UserTermin table with prepopulated values.");
                Statement stateKalender = con.createStatement();
                stateKalender.execute("CREATE TABLE terminkalender(userID integer,"
                        + "terminID integer,"
                        + "nimmtTeil integer,"
                        + "foreign key(userID) references benutzer(userID),"
                        + "foreign key(terminID) references termine(terminID),"
                        + "primary key(userID, terminID))");
            }
            
            if(!res4.next()){
                System.out.println("Building the Kontakliste table with prepopulated values.");
                Statement stateKontaktliste = con.createStatement();
                stateKontaktliste.execute("CREATE TABLE kontaktliste(userID integer,"
                        + "kontaktID integer,"
                        + "foreign key(userID) references benutzer(userID),"
                        + "foreign key(kontaktID) references benutzer(userID),"
                        + "primary key(userID, kontaktID))");
            }
            
            if(!res5.next()){
                System.out.println("Building the Meldung table with prepopulated values.");
                Statement stateMeldungen = con.createStatement();
                stateMeldungen.execute("CREATE TABLE meldungen(meldungsID integer,"
                        + "userID integer,"
                        + "text varchar(60),"
                        + "gelesen integer,"
                        + "anfrage integer,"
                        + "foreign key(userID) references benutzer(userID),"
                        + "primary key(meldungsID))");
            }
            
            if(!res6.next()){
                System.out.println("Building the MeldungAnfrage table with prepopulated values.");
                Statement stateAnfragen = con.createStatement();
                stateAnfragen.execute("CREATE TABLE anfragen(meldungsID integer,"
                        + "terminID integer,"
                        + "absenderID integer,"
                        + "foreign key(terminID) references termine(terminID),"
                        + "foreign key(absenderID) references benutzer(userID),"
                        + "primary key(meldungsID))");
            }
            
            if(!res7.next()){
                System.out.println("Building the Benutzerliste table with prepopulated values.");
                Statement stateAnfragen = con.createStatement();
                stateAnfragen.execute("CREATE TABLE benutzerliste(benutzerlisteID integer,"
                        + "userCounter integer,"
                        + "primary key(benutzerlisteID))");
                
                PreparedStatement prepuser = con.prepareStatement("INSERT INTO benutzerliste values(?,?);");        
                prepuser.setInt(1, 1);
                prepuser.setInt(2, 1);
                prepuser.execute(); 
            }
        }
    }
    
    public void addUser(String username, String passwort, String email, int meldungsCounter, int userID, int terminCounter) throws SQLException{
        PreparedStatement prepuser = con.prepareStatement("INSERT INTO benutzer values(?,?,?,?,?,?,?,?);");        
        prepuser.setInt(1, userID);
        prepuser.setString(2, username);
        prepuser.setString(3, email);
        prepuser.setString(4, "");
        prepuser.setString(5, "");
        prepuser.setString(6, passwort);
        prepuser.setInt(7, meldungsCounter);
        prepuser.setInt(8, terminCounter);
        prepuser.execute(); 
        
        Statement state = con.createStatement();   
        ResultSet res = state.executeQuery("SELECT * FROM benutzerliste " +
                "WHERE benutzerlisteID = " + 1); 
        
        if(res.next()){           
            int userCounter = res.getInt("userCounter");
            userCounter++;

            PreparedStatement prepIncUserCounter = con.prepareStatement("UPDATE benutzerliste SET userCounter = ? WHERE benutzerlisteID = ?");
            prepIncUserCounter.setInt(1, userCounter);
            prepIncUserCounter.setInt(2, 1);
            prepIncUserCounter.execute(); 
        }
    }
    
    public void resetPassword(String username, String passwort) throws SQLException{
        PreparedStatement prepResetPW = con.prepareStatement("UPDATE benutzer SET password = ? WHERE username = ?");
        prepResetPW.setString(1, passwort);
        prepResetPW.setString(2, username);
        prepResetPW.execute();      
    }
    
    public void addKontakt(int userID, int kontaktID) throws SQLException{
        PreparedStatement prepAddKontakt = con.prepareStatement("INSERT INTO kontaktliste values(?,?);");
        prepAddKontakt.setInt(1, userID);
        prepAddKontakt.setInt(2, kontaktID);
        prepAddKontakt.execute();   
    }
    
    public void removeKontakt(int userID, int kontaktID) throws SQLException{
        PreparedStatement prepRemoveKontakt = con.prepareStatement("DELETE FROM kontaktliste WHERE userID = ? AND kontaktID = ?;");
        
        prepRemoveKontakt.setInt(1, userID);
        prepRemoveKontakt.setInt(2, kontaktID);
        prepRemoveKontakt.execute(); 
    }
    
    public void changePasswort(String neuesPW, int userID) throws SQLException{
        PreparedStatement prepChangePasswort = con.prepareStatement("UPDATE benutzer SET password = ? WHERE userID = ?");
        prepChangePasswort.setString(1, neuesPW);
        prepChangePasswort.setInt(2, userID);
        prepChangePasswort.execute(); 
    }
    
    public void changeVorname(String neuerVorname, int userID) throws SQLException{
        PreparedStatement prepChangeVorname = con.prepareStatement("UPDATE benutzer SET name = ? WHERE userID = ?");
        prepChangeVorname.setString(1, neuerVorname);
        prepChangeVorname.setInt(2, userID);
        prepChangeVorname.execute(); 
    }
    
    public void changeNachname(String neuerNachname, int userID) throws SQLException{
        PreparedStatement prepChangeNachname = con.prepareStatement("UPDATE benutzer SET lastname = ? WHERE userID = ?");
        prepChangeNachname.setString(1, neuerNachname);
        prepChangeNachname.setInt(2, userID);
        prepChangeNachname.execute(); 
    }
    
    public void changeEmail(String neueEmail, int userID) throws SQLException{
        PreparedStatement prepChangeEmail = con.prepareStatement("UPDATE benutzer SET email = ? WHERE userID = ?");
        prepChangeEmail.setString(1, neueEmail);
        prepChangeEmail.setInt(2, userID);
        prepChangeEmail.execute(); 
    }
    
    public void addnewTermin(Datum datum, Zeit beginn, Zeit ende, String titel, int terminID, int userID, int terminCounter) throws SQLException{
        PreparedStatement prepAddNewTermin = con.prepareStatement("INSERT INTO termine values(?,?,?,?,?,?,?,?,?,?,?,?,?);");
        prepAddNewTermin.setInt(1, terminID);
        prepAddNewTermin.setString(2, titel);
        prepAddNewTermin.setInt(3, datum.getTag());
        prepAddNewTermin.setInt(4, datum.getMonat());
        prepAddNewTermin.setInt(5, datum.getJahr());
        prepAddNewTermin.setInt(6, beginn.getStunde());
        prepAddNewTermin.setInt(7, beginn.getMinute());
        prepAddNewTermin.setInt(8, ende.getStunde());
        prepAddNewTermin.setInt(9, ende.getMinute());
        prepAddNewTermin.setString(10, "");
        prepAddNewTermin.setString(11, "");
        prepAddNewTermin.setInt(12, userID);
        prepAddNewTermin.setInt(13, 1);
        prepAddNewTermin.execute();
        
        PreparedStatement prepIncCounter = con.prepareStatement("UPDATE benutzer SET terminCounter = ? WHERE userID = ?;");
        prepIncCounter.setInt(1, terminCounter);
        prepIncCounter.setInt(2, userID);
        prepIncCounter.execute();
        
        addTermin(terminID, userID, 1);
    }
    
    public void addTermin(int terminID, int userID, int nimmtTeil) throws SQLException{
        PreparedStatement prepAddTermin = con.prepareStatement("INSERT INTO terminkalender values(?,?,?);");
        prepAddTermin.setInt(1, userID);
        prepAddTermin.setInt(2, terminID);
        prepAddTermin.setInt(3, nimmtTeil);
        prepAddTermin.execute();
    }
    
    public void removeTermin(int terminID, int userID) throws SQLException{
        PreparedStatement prepRemoveTermin = con.prepareStatement("DELETE FROM terminkalender WHERE userID = ? AND terminID = ?;");      
        prepRemoveTermin.setInt(1, userID);
        prepRemoveTermin.setInt(2, terminID);
        prepRemoveTermin.execute(); 
    }
    
    public void deleteTermin(int terminID) throws SQLException{
        PreparedStatement prepDeleteTermin = con.prepareStatement("DELETE FROM terminkalender WHERE terminID = ?;");      
        prepDeleteTermin.setInt(1, terminID);
        prepDeleteTermin.execute(); 
        
        PreparedStatement deleteTermin = con.prepareStatement("DELETE FROM termine WHERE terminID = ?;");      
        deleteTermin.setInt(1, terminID);
        deleteTermin.execute(); 
    }
    
    public void changeEditierrechte(boolean editierbar, int terminID) throws SQLException{
        PreparedStatement prepChangeEditierrechte = con.prepareStatement("UPDATE termine SET editEveryone = ? WHERE terminID = ?");
        if(editierbar){
            prepChangeEditierrechte.setInt(1, 1);
        }
        else{
            prepChangeEditierrechte.setInt(1, 0);
        }
        prepChangeEditierrechte.setInt(2, terminID);
        prepChangeEditierrechte.execute();
    }
            
    public void changeTerminort(int terminID, String neuerOrt) throws SQLException{
        PreparedStatement prepChangeTerminort = con.prepareStatement("UPDATE termine SET location = ? WHERE terminID = ?");
        prepChangeTerminort.setString(1, neuerOrt);
        prepChangeTerminort.setInt(2, terminID);
        prepChangeTerminort.execute();
    }
    
    public void changeTermintitel(int terminID, String neuerTitel) throws SQLException{
        PreparedStatement prepChangeTermintitel = con.prepareStatement("UPDATE termine SET titel = ? WHERE terminID = ?");
        prepChangeTermintitel.setString(1, neuerTitel);
        prepChangeTermintitel.setInt(2, terminID);
        prepChangeTermintitel.execute();
    }
    
    public void changeTerminnotiz(int terminID, String neueNotiz) throws  SQLException{
        PreparedStatement prepChangeTerminnotiz = con.prepareStatement("UPDATE termine SET note = ? WHERE terminID = ?");
        prepChangeTerminnotiz.setString(1, neueNotiz);
        prepChangeTerminnotiz.setInt(2, terminID);
        prepChangeTerminnotiz.execute();
    }
    
    public void changeTerminende(int terminID, Zeit neuesEnde) throws  SQLException{
        PreparedStatement prepChangeTerminende = con.prepareStatement("UPDATE termine SET to_hours = ?, to_minutes = ? WHERE terminID = ?");
        prepChangeTerminende.setInt(1, neuesEnde.getStunde());
        prepChangeTerminende.setInt(2, neuesEnde.getMinute());
        prepChangeTerminende.setInt(3, terminID);
        prepChangeTerminende.execute(); 
    }
    
    public void changeTerminbeginn(int terminID, Zeit neuerBeginn) throws SQLException{
        PreparedStatement prepChangeTerminbeginn = con.prepareStatement("UPDATE termine SET from_hours = ?, from_minutes = ? WHERE terminID = ?");
        prepChangeTerminbeginn.setInt(1, neuerBeginn.getStunde());
        prepChangeTerminbeginn.setInt(2, neuerBeginn.getMinute());
        prepChangeTerminbeginn.setInt(3, terminID);
        prepChangeTerminbeginn.execute(); 
    }
    
    public void changeTermindatum(int terminID, Datum neuesDatum) throws SQLException{
        PreparedStatement prepChangeTermin = con.prepareStatement("UPDATE termine SET day = ?, month = ?, year = ? WHERE terminID = ?");
        prepChangeTermin.setInt(1, neuesDatum.getTag());
        prepChangeTermin.setInt(2, neuesDatum.getMonat());
        prepChangeTermin.setInt(3, neuesDatum.getJahr());
        prepChangeTermin.setInt(4, terminID);
        prepChangeTermin.execute(); 
    }
    
    public void nimmtTeil(int terminID, int userID) throws SQLException{
        PreparedStatement prepNimmtTeil = con.prepareStatement("UPDATE terminkalender SET nimmtTeil = ? WHERE terminID = ? AND userID = ?");
        prepNimmtTeil.setInt(1, 1);
        prepNimmtTeil.setInt(2, terminID);
        prepNimmtTeil.setInt(3, userID);
        prepNimmtTeil.execute();
    }
    
    public void addMeldung(int meldungsID, int userID, String text, Boolean istAnfrage) throws SQLException{
        PreparedStatement prepAddMeldung = con.prepareStatement("INSERT INTO meldungen values(?,?,?,?,?);");
        prepAddMeldung.setInt(1, meldungsID);
        prepAddMeldung.setInt(2, userID);
        prepAddMeldung.setString(3, text);
        prepAddMeldung.setInt(4, 0);
        if(istAnfrage){
            prepAddMeldung.setInt(5, 1);
        }
        else{
            prepAddMeldung.setInt(5, 0);
        }
        prepAddMeldung.execute();
        
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * From benutzer " +
                    "Where userID = " + userID);
        
        resSet.next();
        PreparedStatement prepIncMeldungsCounter = con.prepareStatement("UPDATE benutzer SET meldungsCounter = ? WHERE userID = ?");
        prepIncMeldungsCounter.setInt(1, resSet.getInt("meldungsCounter") + 1);
        prepIncMeldungsCounter.setInt(2, userID);
        prepIncMeldungsCounter.execute();
    }  
    
    public void addAnfrage(int meldungsID, int userID, int terminID, int absenderID, String text) throws SQLException{
        addMeldung(meldungsID, userID, text, true);
        
        PreparedStatement prepAddAnfrage = con.prepareStatement("INSERT INTO anfragen values(?,?,?);");
        prepAddAnfrage.setInt(1, meldungsID);
        prepAddAnfrage.setInt(2, terminID);
        prepAddAnfrage.setInt(3, absenderID);
        prepAddAnfrage.execute();
    }
    
    public void deleteMeldung(int meldungsID) throws SQLException{
        PreparedStatement deleteAnfrage, deleteMeldung;
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * From meldungen " +
                    "Where meldungsID = " + meldungsID);
        
        resSet.next();
        if(resSet.getInt("anfrage") == 1){
            deleteAnfrage = con.prepareStatement("DELETE FROM anfragen WHERE meldungsID = ?");
            deleteAnfrage.setInt(1, meldungsID);
            deleteAnfrage.execute();
        }
        
        deleteMeldung = con.prepareStatement("DELETE FROM meldungen WHERE meldungsID = ?");
        deleteMeldung.setInt(1, meldungsID);
        deleteMeldung.execute();
    }
    
    public void setMeldungenGelesen(int meldungsID) throws SQLException{
        PreparedStatement prepNimmtTeil = con.prepareStatement("UPDATE meldungen SET gelesen = ? WHERE meldungsID = ?");
        prepNimmtTeil.setInt(1, 1);
        prepNimmtTeil.setInt(2, meldungsID);
        prepNimmtTeil.execute();
    }
    
    // ****************************** GETTER ****************************** //
    
    public int getUserCounter() throws SQLException, DatenbankException{
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("Select * FROM benutzerliste " +
                "Where benutzerlisteID = 1");
        if(res.next()){
            return res.getInt("userCounter");
        }
        throw new DatenbankException("kein user Counter!!");
    }
    
    private ResultSet getUserDetails(int userID) throws SQLException{
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("Select * FROM benutzer " +
                "Where userID = " + userID);
        return res;
    }   
    
    private LinkedList<Termin> getTermine(int userID) throws SQLException {
        LinkedList<Termin> terminkalender = new LinkedList<>();   
        Statement state = con.createStatement();
        String teilnehmerUsername;
        boolean edit, stopLoop;
        ResultSet teilnehmerliste, termine = null;
        
        
        try {
            //Grunddaten jedes Termins des Users holen
            termine = state.executeQuery("SELECT * FROM termine " +
                    "JOIN terminkalender ON termine.terminID = terminkalender.terminID " +
                    "WHERE terminkalender.userID = " + userID);
        } catch (SQLException ex) {
            System.out.println("benutzer ohne termine geladen");  
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //hier werden die Terminliste erstellt
        while(termine != null && termine.next()){
            //gibt es einen User auf dem Servern, der an dem Termin teilnimmt? 
            //wenn ja: füge referenz hinzu
            teilnehmerliste = getTeilnehmerSet(termine.getInt("terminID"));
            stopLoop = false;
            while(teilnehmerliste.next() && !stopLoop){
                teilnehmerUsername = getUsernameByUserID(teilnehmerliste.getInt("userID"));
                if(benutzerliste.existiertBenutzer(teilnehmerUsername)){
                    try {
                        terminkalender.add(benutzerliste.getBenutzer(teilnehmerUsername).getTerminkalender().getTerminByID(termine.getInt("terminID")));
                        stopLoop = true;
                    } catch (BenutzerException | TerminException ex) {
                        Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }    
            //wenn nein: erstelle neuen Termin
            if(!stopLoop){
                edit = termine.getInt("editEveryone") == 1;
                try {
                    //Erst die einfachen Datentypen und Klassen hinzufügen
                    terminkalender.add(new Termin(
                            new Datum(termine.getInt("day"), termine.getInt("month"), termine.getInt("year")),
                            new Zeit(termine.getInt("from_hours"), termine.getInt("from_minutes")),
                            new Zeit(termine.getInt("to_hours"), termine.getInt("to_minutes")),
                            termine.getString("titel"),
                            termine.getInt("terminID"),
                            getUsernameByUserID(termine.getInt("ownerID")),
                            termine.getString("location"),
                            termine.getString("note"),
                            edit));
                    //und nun die Teilnehmerliste
                    teilnehmerliste = getTeilnehmerSet(termine.getInt("terminID"));
                    while(teilnehmerliste.next()){
                        if(termine.getInt("ownerID") != teilnehmerliste.getInt("userID")){
                            teilnehmerUsername = getUsernameByUserID(teilnehmerliste.getInt("userID"));
                            terminkalender.getLast().addTeilnehmer(teilnehmerUsername);
                            //falls der Teilnehmer zugesagt hat, muss das noch gesetzt werden
                            if(testUserNimmtTeil(teilnehmerliste.getInt("userID"), termine.getInt("terminID"))){
                                terminkalender.getLast().changeTeilnehmerNimmtTeil(teilnehmerUsername);
                            }
                        }
                    }
                } catch (TerminException | Datum.DatumException | Zeit.ZeitException ex) {
                    Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return terminkalender;
    }
      
    private LinkedList<String> getKontaktliste(int userID) throws SQLException, DatenbankException{
        LinkedList<String> kontaktliste = new LinkedList<>();
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * FROM kontaktliste " +
                "Where userID = " + userID);
        
        while(resSet.next()){
            kontaktliste.add(getUsernameByUserID(resSet.getInt("kontaktID")));
        } 
        
        return kontaktliste;
    }
    
    private LinkedList<Meldungen> getMeldungen(int userID, Benutzer benutzer) throws SQLException{
        LinkedList<Meldungen> meldungen = new LinkedList<>();
        
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * FROM meldungen " +
                "Where userID = " + userID);
        
        while(resSet.next()){
            if(resSet.getInt("anfrage") == 1){
                meldungen.add(getAnfrage(resSet.getString("text"), resSet.getInt("meldungsID"), benutzer));
            }
            else{
                meldungen.add(new Meldungen(resSet.getString("text"), resSet.getInt("meldungsID")));                    
            } 
            if(resSet.getInt("gelesen") == 1){
                    meldungen.getLast().meldungGelesen();
            }  
        }
        return meldungen;
    }
    
    private Anfrage getAnfrage(String text, int meldungsID, Benutzer benutzer) throws SQLException{
        Anfrage anfrage; 
        Termin termin;
        
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * FROM anfragen " +
                "Where meldungsID = " + meldungsID);
        
        resSet.next();
        try {
            termin = benutzer.getTerminkalender().getTerminByID(resSet.getInt("terminID"));
            anfrage = new Anfrage(text, termin, getUsernameByUserID(resSet.getInt("absenderID")), meldungsID);
            return anfrage;
        } catch (TerminException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public Benutzer getBenutzer(int userID) throws SQLException, DatenbankException{
        Benutzer benutzer;
        LinkedList<Termin> termine;
        LinkedList<String> kontakte;
        LinkedList<Meldungen> meldungen;
        
        ResultSet user = getUserDetails(userID);
        
        if(user.next()){
            //erstmal alle einfachen Datentypen 
            benutzer = new Benutzer(
                    user.getString("username"), 
                    user.getString("password"), 
                    user.getString("email"), 
                    user.getInt("userID"), 
                    user.getString("name"), 
                    user.getString("lastname"), 
                    user.getInt("meldungsCounter"),
                    user.getInt("terminCounter")
            );
            //jetzt die Listen
            //1. Termine
            termine = getTermine(userID);
            for(Termin termin : termine){
                benutzer.addTermin(termin);
            }
            //2. Meldungen
            meldungen = getMeldungen(userID, benutzer);
            benutzer.setMeldungen(meldungen);
            //3. Kontakte
            kontakte = getKontaktliste(userID);
            benutzer.setKontaktliste(kontakte);
           
            return benutzer;
        }
        
        throw new DatenbankException("user " + userID + " nicht in Datenbank vorhanden!");
    }

    public Benutzer getBenutzer(String username, BenutzerListe benutzerliste) throws SQLException, DatenbankException{
        this.benutzerliste = benutzerliste;
        int userID;
        Statement state = con.createStatement();
        
        ResultSet res = state.executeQuery("SELECT * FROM benutzer " +
                "WHERE username = \"" + username + "\"");       
        
        if(res.next()){
            userID = res.getInt("userID");
            return getBenutzer(userID);
        }
        
        throw new DatenbankException(username + " nicht in Datenbank vorhanden!");
    }
    
    // ****************************** Hilfsmethoden ****************************** //
    
    private String getUsernameByUserID(int userID) throws SQLException{
        String username;
        Statement state = con.createStatement();
        
        ResultSet res = state.executeQuery("SELECT * FROM benutzer " +
                "WHERE userID = " + userID); 
        
        if(res.next()){
            username = res.getString("username");
            return username;
        }
        return null; 
    }
    
    private ResultSet getTeilnehmerSet(int terminID) throws SQLException{
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * From terminkalender " +
                    "Where terminID = " + terminID);
        
        return resSet;
    }

    private boolean testUserNimmtTeil(int userID, int terminID) throws SQLException {
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * From terminkalender " +
                    "Where terminID = " + terminID + " AND userID = " + userID);
        
        resSet.next();        
        return resSet.getInt("nimmtTeil") == 1;
    }
}
