/*
 * koneksiAlat.java
 *
 * Created on 11 Desember 2007, 19:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package toModem;
//paket untuk atur port
import javax.comm.*;

// Utilities Packages
import java.util.Enumeration;
import java.util.Vector;

// Untilities untuk Pattern
import javax.swing.JComboBox;

/**
 *
 * @author Rachmad HW
 */
public class portSearch implements SerialPortEventListener {
    
    static Enumeration daftarPort;
    static CommPortIdentifier portId;
    static String namaPort;
    /** Creates a new instance of koneksiAlat */
    public static void main(String[] args) {
       portSearch.prosesCariPort();
	}
    public static Vector prosesCariPort() {
        int i = 0;
        try{
        daftarPort = CommPortIdentifier.getPortIdentifiers();
        Vector jcombo = new Vector();
        while (daftarPort.hasMoreElements()) {
            portId = (CommPortIdentifier) daftarPort.nextElement();
            namaPort = portId.getName();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL ) {
            // Mendapatkan nama port
                    namaPort = portId.getName();
                    jcombo.add(namaPort);
            // Menambahkan Item pada comboPort
            }
        }
        return jcombo;
        }catch (Exception e){}
        return null;
    } // Akhir methode prosesCariPort
    public void serialEvent(SerialPortEvent serialPortEvent) {
        serialPortEvent.notify();//masih belum tau kegunaanya???
    }
}
