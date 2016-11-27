/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eventremainder;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.smslib.GatewayException;
import org.smslib.TimeoutException;
import toModem.SmsGateway;
import toSql.toSql;

/**
 *
 * @author Komando
 */
public class sendMessage implements Runnable{
    SmsGateway sms;
    toSql sql;
    ResultSet idKontak;
    String pesan;
    progressSms progrees;
    javax.swing.JButton kirim;
    String idEvent;
    public sendMessage(SmsGateway s,toSql sq,ResultSet i,String p, progressSms pr,javax.swing.JButton bt){
        sms =s;
        sql =sq;
        idKontak=i;
        pesan =p;
        progrees=pr;
        kirim=bt;
    }

    @Override
    public void run() {
        int point = (int)Math.round((double)progrees.getMaxValueProgressBar()/(double)sql.getRowCount(idKontak));
        int progres=0;
        try {
            while(idKontak.next()){
                String pesanX = pesan;
                
                String no;
                pesanX = pesanX.replaceAll("%nama%", idKontak.getString("nama_wali"));
                pesanX = pesanX.replaceAll("%acara%", idKontak.getString("nama_event"));
                pesanX = pesanX.replaceAll("%tgl%", getDateModel(idKontak.getDate("tgl_event"),"dd-MMMM-yyyy"));
                pesanX = pesanX.replaceAll("%jam%", idKontak.getString("jam_event"));
                pesanX = pesanX.replaceAll("%tempat%", idKontak.getString("tempat"));
                pesanX = pesanX.replaceAll("%keterangan%", idKontak.getString("keterangan"));
                if(!(no=idKontak.getString("noTelp1")).equals(""))
                    sms.kirimSMS(no, pesanX);
                if(!(no=idKontak.getString("noTelp2")).equals(""))
                    sms.kirimSMS(no, pesanX);
                progres+=point;
                progrees.setValueProgressBar(progres);
            }
        } catch (SQLException ex) {
        }
        if(progres!=progrees.getMaxValueProgressBar()){
            JOptionPane.showMessageDialog(null,"Tidak semua pesan terkirim.", "Pengiriman Gagal", JOptionPane.ERROR_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null,"Semua pesan berhasil dikirim", "Pengiriman berhasil", JOptionPane.INFORMATION_MESSAGE);
        }
        progrees.setValueProgressBar(0);
        progrees.removeFromList();
        setTimerEventNext();
    }
    public String getDateModel (Date d,String model){
        SimpleDateFormat date = new SimpleDateFormat(model);
        return date.format(d);
    }
    private void setTimerEventNext(){
        Timer timer = new Timer();
        try {
            if(sql.isExists("SELECT TIMEDIFF(time(tgl_pengingat),CURRENT_TIME()) waktu FROM pesan WHERE date(tgl_pengingat)=CURRENT_DATE() AND time(tgl_pengingat)>CURRENT_TIME() and status_terkirim=0 ORDER BY tgl_pengingat ASC")){
                ResultSet rs = sql.getTable("SELECT TIMEDIFF(time(tgl_pengingat),CURRENT_TIME()) waktu, time(tgl_pengingat) jam FROM pesan WHERE date(tgl_pengingat)=CURRENT_DATE() AND time(tgl_pengingat)>CURRENT_TIME() and status_terkirim=0 ORDER BY tgl_pengingat ASC");rs.next();
                String pukul[] = rs.getString("waktu").split(":");
                rs = sql.getTable("SELECT * FROM pesan where date(tgl_pengingat)=CURRENT_DATE() AND time(tgl_pengingat)='"+rs.getString("jam")+"' ORDER BY tgl_pengingat");
                Vector isi = new Vector();
                while (rs.next()) {
                    Vector row = new Vector();
                    row.add(rs.getString("id_event"));
                    row.add(rs.getString("pesan"));
                    isi.add(row);
                }
                long second = (Integer.parseInt(pukul[0]) * 3600) + (Integer.parseInt(pukul[1]) * 60) + Integer.parseInt(pukul[2]);
                timer.schedule(new Timers(isi, timer, sms, sql,progrees,kirim), second * 1000);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
