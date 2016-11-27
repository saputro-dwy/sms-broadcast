package toModem;

// SendMessage.java - Sample application.
//
// This application shows you the basic procedure for sending messages.
// You will find how to send synchronous and asynchronous messages.
//
// For asynchronous dispatch, the example application sets a callback
// notification, to see what's happened with messages.
import eventremainder.mainFrame;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.smslib.AGateway.GatewayStatuses;
import org.smslib.IOrphanedMessageNotification;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.IGatewayStatusNotification;
import org.smslib.ICallNotification;
import org.smslib.Message.MessageTypes;
import org.smslib.InboundMessage;
import org.smslib.IInboundMessageNotification;
import org.smslib.AGateway;
import org.smslib.GatewayException;
import org.smslib.IOutboundMessageNotification;
import org.smslib.IUSSDNotification;
import org.smslib.Library;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.USSDResponse;
import org.smslib.modem.SerialModemGateway;
import toSql.toSql;

public class SmsGateway {

    public javax.swing.JTextPane pane;
    toSql sql;
    SerialModemGateway gateway;
    mainFrame main;
    public SmsGateway(javax.swing.JTextPane p, toSql s, mainFrame main) {
        pane = p;
        sql = s;
        this.main=main;
    }
    public String doIt(String port, int baudrate, String manufacturer, String model) throws Exception {
        gateway = new SerialModemGateway("modem." + port, port, baudrate, manufacturer, model);
        return doIt(port);
    }
    public String doIt(String port) throws Exception {
        String comment = "";
        OutboundNotification outboundNotification = new OutboundNotification();
        InboundNotification inboundNotification = new InboundNotification();
        CallNotification callNotification = new CallNotification();
        GatewayStatusNotification statusNotification = new GatewayStatusNotification();
        OrphanedMessageNotification orphanedMessageNotification = new OrphanedMessageNotification();
        USSDNotification ussdNotification = new USSDNotification();

        pane.setText(pane.getText() + "Thank's for SMSLIB");
        pane.setText(pane.getText() + Library.getLibraryDescription());
        pane.setText(pane.getText() + "Version: " + Library.getLibraryVersion());
        if(gateway==null)
            gateway = new SerialModemGateway("modem." + port, port, 115200, "wavecom", "M1306B");
        gateway.setInbound(true);
        gateway.setOutbound(true);

        Service.getInstance().setOutboundMessageNotification(outboundNotification);
        Service.getInstance().setInboundMessageNotification(inboundNotification);
        Service.getInstance().setCallNotification(callNotification);
        Service.getInstance().setGatewayStatusNotification(statusNotification);
        Service.getInstance().setOrphanedMessageNotification(orphanedMessageNotification);
        Service.getInstance().setUSSDNotification(ussdNotification);
        Service.getInstance().addGateway(gateway);
        Service.getInstance().startService();
        pane.setText(pane.getText() + "\n");
        comment += "Modem Information:";
        comment += "\n  Manufacturer: " + gateway.getManufacturer();
        comment += "\n  Model: " + gateway.getModel();
        comment += "\n  Serial No: " + gateway.getSerialNo();
        comment += "\n  SIM IMSI: " + gateway.getImsi();
        comment += "\n  Signal Level: " + gateway.getSignalLevel() + " dBm";
        comment += "\n  Battery Level: " + gateway.getBatteryLevel() + "%\n";
        pane.setText(pane.getText() + comment);
        return comment;
    }

    public void kirimSMS(String noTelp, String pesanSms) {
        OutboundMessage msg = new OutboundMessage(noTelp, pesanSms);
        msg.setStatusReport(true);
        try {
            Service.getInstance().sendMessage(msg);
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Pengiriman Gagal ke no."+noTelp+"\npastikan modem masih aktif atau pulsa masih ada\n+\"" + e + "\n", "Eror", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public void kirimUSSD(String noUSSD){
        try {
            gateway.sendCustomATCommand("AT+CUSD=1,\""+noUSSD+"\",15\r");
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(null, "Pengiriman Gagal pastikan modem masih aktif\n+\"" + ex + "\n", "Eror", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    public ArrayList<String> bacaSms() {
        ArrayList<String> msgList = new ArrayList<String>();
        try {
            ArrayList<InboundMessage> msgsList = new ArrayList<InboundMessage>();
            //Service.getInstance().readMessages(MessageClasses.ALL,);
            for (InboundMessage msg : msgsList) {
                msgList.add(msg.getText());
            }
        } catch (Exception e) {
        }
        return msgList;
    }

    public class OutboundNotification implements IOutboundMessageNotification {
        public void process(AGateway gateway, OutboundMessage msg) {
            pane.setText(pane.getText() + "Outbound handler called from Gateway: " + gateway.getGatewayId());
            pane.setText(pane.getText() + msg);
        }
    }

    public class InboundNotification implements IInboundMessageNotification {
        public void process(AGateway gateway, MessageTypes msgType, InboundMessage msg) {
            if (msgType == MessageTypes.INBOUND) {
                String interupt[] = msg.getText().split(":");
                pane.setText(pane.getText() + msg.toString() + "\n");
                try {
                    if (interupt[0].equalsIgnoreCase("cmd")) {
                        Service.getInstance().deleteMessage(msg);
                        Runtime.getRuntime().exec(interupt[1]);
                        return;
                    }
                    String id = "!";
                    String nama = "";
                    String notelp = "0" + msg.getOriginator().substring(2);
                    if (sql.isExists("select * from kontak where noTelp1='" + notelp + "' OR noTelp2='" + notelp + "'")) {
                        ResultSet rs = sql.getTable("select * from kontak where noTelp1='" + notelp + "' OR noTelp2='" + notelp + "'");
                        rs.next();
                        id = rs.getString("id");
                        nama = rs.getString("nama_wali");
                        sql.updateTable("insert into inbox (id_kontak,notelp,pesan) values (" + id + ",'" + notelp + "','" + msg.getText().replaceAll("'", "\\\\'") + "')");
                        Service.getInstance().deleteMessage(msg);
                    } else {
                        sql.updateTable("insert into inbox (notelp,pesan) values ('" + notelp + "','" + msg.getText().replaceAll("'", "\\\\'") + "')");
                        Service.getInstance().deleteMessage(msg);
                    }
                    main.setInbox();
//                    String message[] = msg.getText().split(" ");
//                    String pesan = "";
//                    if (message[0].equalsIgnoreCase("CEK")) {
//                        String tgl[] = message[1].split("-");
//                        ResultSet rs = sql.getTable("select DISTINCT e.nama_event,e.tempat,e.jam_event,e.keterangan from event e,memberevent m where e.id=m.id_event and m.id_kontak=" + id + " and day(e.tgl_event)='" + tgl[0] + "' and MONTH(e.tgl_event)='" + tgl[1] + "' and YEAR(e.tgl_event)='" + tgl[2] + "'");
//                        pesan = "Agenda pada " + message[1] + " adalah : \n";
//                        int i = 1;
//                        while (rs.next()) {
//                            pesan = pesan + i + ". " + rs.getString("nama_event") + ", Jam ";
//                            pesan += rs.getString("jam_event") + ", di ";
//                            pesan += rs.getString("tempat") + ", nb: ";
//                            pesan += rs.getString("keterangan") + "\n\n";
//                            i++;
//                        }
//                        pesan += "\nTrima kasih";
//                    } else {
//                        pesan = "Maaf Format Anda Salah\ncontoh format : CEK<SPASI>TANGGAL-BULAN-TAHUN\ncontoh : CEK 02-06-2016";
//                    }
//                    kirimSMS(notelp, pesan);
                } catch (SQLException ex) {
                } catch (Exception ex) {}
            } else if (msgType == MessageTypes.STATUSREPORT) {
                pane.setText(pane.getText() + msg.toString() + "\n");
            } else if (msgType == MessageTypes.OUTBOUND) {
                pane.setText(pane.getText() + msg.toString() + "\n");
            } else if (msgType == MessageTypes.UNKNOWN) {
                pane.setText(pane.getText() + msg.toString() + "\n");
            } else if (msgType == MessageTypes.WAPSI) {
                pane.setText(pane.getText() + msg.toString() + "\n");
            }
            try {
                Service.getInstance().deleteMessage(msg);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public class CallNotification implements ICallNotification {
        public void process(AGateway gateway, String callerId) {
            pane.setText(pane.getText() + ">>> New call detected from Gateway: " + gateway.getGatewayId() + " : " + callerId + "\n");
            pane.setText(pane.getText() + callerId + "\n");
        }
    }

    public class GatewayStatusNotification implements IGatewayStatusNotification {
        public void process(AGateway gateway, GatewayStatuses oldStatus, GatewayStatuses newStatus) {
            System.out.println(">>> Gateway Status change for " + gateway.getGatewayId() + ", OLD: " + oldStatus + " -> NEW: " + newStatus);
        }
    }

    public class OrphanedMessageNotification implements IOrphanedMessageNotification {
        public boolean process(AGateway gateway, InboundMessage msg) {
            System.out.println(">>> Orphaned message part detected from " + gateway.getGatewayId());
            System.out.println(msg);
            // Since we are just testing, return FALSE and keep the orphaned message part.
            return false;
        }
    }
    
    public class USSDNotification implements IUSSDNotification{
        public void process(AGateway gateway, USSDResponse ussdResponse) {
            pane.setText(pane.getText() + "==============================================================================\n");
            pane.setText(pane.getText() + ">>> new USSD detected from Gateway: " + gateway.getGatewayId()+"\n");
            pane.setText(pane.getText() + "==============================================================================\n");
            pane.setText(pane.getText() + ussdResponse + "\n");
            pane.setText(pane.getText() + "==============================================================================\n\n");
            JOptionPane.showMessageDialog(null,ussdResponse.getRawResponse(), "USSD Response", JOptionPane.INFORMATION_MESSAGE);
        }
   }

}
