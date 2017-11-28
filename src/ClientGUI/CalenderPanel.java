/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientGUI;

//import statements
import Terminkalender.BenutzerException;
import Terminkalender.Datum;
import Terminkalender.LauncherInterface;
import Terminkalender.Termin;
import Terminkalender.TerminException;
import Terminkalender.Zeit;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

//create class
class CalenderPanel {

    private final LauncherInterface stub;
    private final int sitzungsID;

    //define variables
    LocalDate ld = LocalDate.now();
    int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
    //int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    //int month = ld.getMonthValue();
    int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

    //create object of JLabel with alignment
    JLabel l = new JLabel("", JLabel.CENTER);
    //define variable
    String day = "";
    //declaration
    JPanel d;
    //create object of JButton
    JButton[] button = new JButton[49];

    public CalenderPanel(LauncherInterface stub, int sitzungsID)//create constructor 
    {
        this.stub = stub;
        this.sitzungsID = sitzungsID;

        //create object
        d = new JPanel();
        //set modal true
        //d.setModal(true);
        //define string
        String[] header = {"Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};
        //create JPanel object and set layout
        JPanel p1 = new JPanel(new GridLayout(7, 7));
        //set size
        p1.setPreferredSize(new Dimension(600, 500));
        //for loop condition
        for (int x = 0; x < button.length; x++) {
            //define variable
            final int selection = x;
            //create object of JButton
            button[x] = new JButton();
            //set focus painted false
            button[x].setFocusPainted(false);
            //set background colour
            button[x].setBackground(Color.LIGHT_GRAY);
            //if loop condition
            if (x > 6) //add action listener
            {
                button[x].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        day = button[selection].getActionCommand();
                        //call dispose() method
                        //d.dispose();
                    }
                });
            }
            if (x < 7)//if loop condition 
            {
                button[x].setText(header[x]);
                //set fore ground colour
                button[x].setForeground(Color.red);
            }
            p1.add(button[x]);//add button
        }
        //create JPanel object with grid layout
        JPanel p2 = new JPanel(new GridLayout(1, 3));

        //create object of button for previous month
        JButton previous = new JButton("<< Previous");
        //add action command
        previous.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                //decrement month by 1
                month--;
                //call method
                displayDate();
            }
        });
        p2.add(previous);//add button
        p2.add(l);//add label
        //create object of button for next month
        JButton next = new JButton("Next >>");
        //add action command
        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                //increment month by 1
                month++;
                //call method
                displayDate();
            }
        });
        p2.add(next);// add next button
        //set border alignment
        d.add(p1, BorderLayout.CENTER);
        d.add(p2, BorderLayout.NORTH);
        //d.pack();
        //set location
        //d.setLocationRelativeTo();
        //call method
        displayDate();

        //d.setLocationRelativeTo(null);
        
        //set visible true         
        d.setVisible(true);
    }

    public void displayDate() {
        for (int x = 7; x < button.length; x++)//for loop
        {
            button[x].setText("");//set text
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.YYYY");
        //create object of SimpleDateFormat 
        java.util.Calendar cal = java.util.Calendar.getInstance();
        //create object of java.util.Calendar 
        cal.set(year, month, 1); //set year, month and date
        //define variables
        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        ////JOptionPane.showMessageDialog(null, month, "InfoBox: month", JOptionPane.INFORMATION_MESSAGE);
        ////JOptionPane.showMessageDialog(null, year, "InfoBox: year", JOptionPane.INFORMATION_MESSAGE);
        //int dayOfWeek = ld.get(ld.getDayOfWeek());

        //int daysInMonth = ld.lengthOfMonth();
        ////JOptionPane.showMessageDialog(null, dayOfWeek, "InfoBox: dayOfWeek", JOptionPane.INFORMATION_MESSAGE);
        ////JOptionPane.showMessageDialog(null, daysInMonth, "InfoBox: daysInMonth", JOptionPane.INFORMATION_MESSAGE);
        String twoLines = "Two\nLines";
        StringBuilder sb = new StringBuilder();
        StringBuilder cl = new StringBuilder();

        //sb.append( "a" );
        //String s= sb.toString() ;
        LinkedList<Termin> dieserMonat;

        //wegen java kalender fehler um 1 hoch zahelen
        int monat = month + 1;

        try {
            dieserMonat = stub.getTermineInMonat(monat, year, sitzungsID);

            int i = 1;
            //String zusammen = " ";

            //condition
            for (int x = 6 + dayOfWeek, day = 1; day <= daysInMonth; x++, day++) //set text
            {

                for (Termin termin : dieserMonat) {
                    //JOptionPane.showMessageDialog(null, month, "InfoBox: month in forschlife", JOptionPane.INFORMATION_MESSAGE);
                    //JOptionPane.showMessageDialog(null, year, "year in forschleife", JOptionPane.INFORMATION_MESSAGE);
                    //JOptionPane.showMessageDialog(null, i, "InfoBox: for in i", JOptionPane.INFORMATION_MESSAGE);

                    cl.append(day);
                    cl.append(".");
                    cl.append(monat);
                    cl.append(".");
                    cl.append(year);

                    String calenderDate = cl.toString();
                    //JOptionPane.showMessageDialog(null, calenderDate, "InfoBox: stub1", JOptionPane.INFORMATION_MESSAGE);

                    String tuiDate = termin.getDatum().toString();
                    //JOptionPane.showMessageDialog(null, tuiDate, "InfoBox: 2 Datum", JOptionPane.INFORMATION_MESSAGE);

                    if (calenderDate.equals(tuiDate)) {
                        String titel = termin.getTitel();
                        String[] parts = titel.split(" ");
                        String part1 = parts[0]; // 004
                        String cutString = part1;

                        int length = part1.length();

                        if (length > 10) {
                            cutString = part1.substring(0, 8) + "...";
                        }

                        //JOptionPane.showMessageDialog(null, day, "InfoBox:", JOptionPane.INFORMATION_MESSAGE);
                        //sb.append("\n");
                        //sb.append(termin.getBeginn().toString());
                        //sb.append(" ");
                        sb.append(cutString);
                        sb.append("\n");

                        //JOptionPane.showMessageDialog(null, sb.toString(), "InfoBox: 3", JOptionPane.INFORMATION_MESSAGE);
                    }
                    cl.setLength(0);
                    i++;
                }

                String getSt = sb.toString();
                String twooLines = day + "\n" + getSt;

                /**
                 * if (){ //set fore ground colour
                 * button[x].setForeground(Color.red); }
                 */
                //String twooLines = "Two\nLines";
                button[x].setText("<html>" + twooLines.replaceAll("\\n", "<br>") + "</html>");
                sb.setLength(0);
            }

            l.setText(sdf.format(cal.getTime()));

            //set title
            //d.setTitle("Date Picker");
        } catch (RemoteException | TerminException | BenutzerException ex) {
            Logger.getLogger(CalenderPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String setPickedDate() {
        //if condition
        if (day.equals("")) {
            return day;
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month, Integer.parseInt(day));
        return sdf.format(cal.getTime());
    }
}
