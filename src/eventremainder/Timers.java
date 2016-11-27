/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventremainder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import toModem.SmsGateway;
import toSql.toSql;
/**
 *
 * @author Komando
 */
public class Timers extends TimerTask{
    Vector isi = new Vector();
    Timer timer;
    SmsGateway sms;
    toSql sql;
    javax.swing.JButton kirim;
    progressSms progressBar;
    public Timers(Vector i,Timer t,SmsGateway s,toSql sq,progressSms pr,javax.swing.JButton bt){
        isi = i;
        timer = t;
        sms = s;
        sql = sq;
        progressBar=pr ;
        kirim=bt;
    }
    @Override
    public void run() {
        for (int i = 0; i < isi.size(); i++) {
            try {
                Vector j = (Vector)isi.get(i);
                sql.updateTable("update pesan set status_terkirim=1 where id_event="+((String)j.get(0)));
                ResultSet rs = sql.getTable("select * from event e, memberevent m, kontak k where e.id=m.id_event and m.id_kontak=k.id and k.id and e.id="+((String)j.get(0)));
                
                progressBar.addToList();
                Thread th = new Thread(new sendMessage(sms, sql, rs,((String)j.get(1)), progressBar, kirim));
                th.start();
            } catch (SQLException ex) {
                Logger.getLogger(Timers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
