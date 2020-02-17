package gui;

import java.awt.GridLayout;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Clock extends JPanel implements Runnable{
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("HH:mm:ss");
    private Digit[] digits = new Digit[8];
    private String city;
    
    public Clock() {
        this.setLayout(new GridLayout(1, 8));
        
        for(int i = 1; i<=8; i++) {
            Digit dg = new Digit((i%3==0?-1:0));
            this.add(dg);
            digits[i-1] = dg;
        }
    }
    
    @Override
    public void run() {
        while(!Thread.interrupted()) {
            LocalTime time = LocalTime.now();
            if(!city.equals("L")) {
                Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(city));
                time = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
            }
            String timeStr = time.format(DTF);
            
            for(int i = 0; i<8; i++) {
                if(timeStr.toCharArray()[i] == ':')
                    digits[i].setValue(-1);
                else
                    digits[i].setValue(Integer.parseInt(""+timeStr.toCharArray()[i]));
            }
            
            try {
                Thread.sleep(250);
            }catch(InterruptedException e) {
                break;
            }
        }
        
        for(int i = 1; i<=8; i++) {
            if(i%3==0)
                digits[i-1].setValue(-1);
            else
                digits[i-1].setValue(0);
        }
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
     public static void main(String[] args) throws InterruptedException {      
        JFrame uhr = new JFrame("Beispiel JFrame");     
        uhr.setSize(400,200);
        uhr.setLocationRelativeTo(null); 
        uhr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Clock cl = new Clock();
        
        uhr.getContentPane().add(cl);

        uhr.setVisible(true);           
    }
    
}