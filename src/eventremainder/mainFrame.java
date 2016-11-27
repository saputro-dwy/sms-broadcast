
package eventremainder;
import static eventremainder.addPesertaEvent.getTabelModel;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import static java.awt.SystemTray.getSystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import toModem.*;
import toSql.toSql;
/**
 *
 * @author Komando
 */
public class mainFrame extends javax.swing.JFrame implements Runnable{
    about ab;
    ArrayList<progressSms> progressList;
    public mainFrame() {
        ab = new about();
        str = new start();
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        st.setSize(400,300);
        st.setLocation(dim.width-st.getSize().width, dim.height-st.getSize().height-50);
        st.setVisible(true);
        setConfig();
        
        if(portSearch.prosesCariPort().size()==0){
            JOptionPane.showMessageDialog(null,"Modem tidak tersambung atau driver anda belum terinstall");
            System.exit(0);
        }
        if(portSearch.prosesCariPort().size()>1){
            int x = JOptionPane.showOptionDialog(null,str, "Setting", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        }
        //frame gui modem
        sms = new SmsGateway(st.getTextArea(),sql,this);
        try {
            new Thread(this).start();
        } catch (Exception ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComponents();
        //frame gui event
        setTitle("~SMS Broadcast~");
        progressList = new ArrayList<progressSms>();
        try {
            java.awt.Image img = ImageIO.read(getClass().getResource("/images/heads.PNG"));
            MenuItem item1 = new MenuItem("Exit");
            item1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            MenuItem item2 = new MenuItem("Tamplikan status modem");
            item2.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    st.setVisible(true);
                    st.setExtendedState(mainFrame.NORMAL);
                }
            });
            MenuItem item3 = new MenuItem("Tampilkan Tampilan Utama");
            item3.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mainFrame.this.setVisible(true);
                    mainFrame.this.setExtendedState(mainFrame.NORMAL);
                }
            });
            MenuItem item4 = new MenuItem("about");
            item4.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showOptionDialog(null, ab, "Tentang Kami", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object []{} ,null);
                }
            });
            PopupMenu menu = menu = new PopupMenu();
            menu.add(item3);
            menu.add(item2);
            menu.add(item4);
            menu.add(item1);
            final TrayIcon icon = new TrayIcon(img,"SMS Broadcast",menu);
            setIconImage(img);
            st.setIconImage(img);
            icon.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mainFrame.this.setVisible(true);
                    mainFrame.this.setExtendedState(mainFrame.NORMAL);
                }
            });
            getSystemTray().add(icon);
            addWindowListener(new WindowAdapter() {
                public void windowIconified(WindowEvent e) {
                    mainFrame.this.setVisible(false);
                }
                public void windowClosing(WindowEvent e) {
                    mainFrame.this.setVisible(false);
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AWTException ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
//        timer = new Timer();
//        try {
//            if(sql.isExists("SELECT TIMEDIFF(time(tgl_pengingat),CURRENT_TIME()) waktu FROM pesan WHERE date(tgl_pengingat)=CURRENT_DATE() AND time(tgl_pengingat)>CURRENT_TIME() ORDER BY tgl_pengingat ASC LIMIT 1")){
//                ResultSet rs = sql.getTable("SELECT TIMEDIFF(time(tgl_pengingat),CURRENT_TIME()) waktu FROM pesan WHERE date(tgl_pengingat)=CURRENT_DATE() AND time(tgl_pengingat)>CURRENT_TIME() ORDER BY tgl_pengingat ASC LIMIT 1");
//                rs.next();
//                String pukul[] = rs.getString("waktu").split(":");
//                rs = sql.getTable("SELECT * FROM pesan p,event e WHERE p.id_event=e.id and date(tgl_pengingat)=CURRENT_DATE() AND time(tgl_pengingat)>CURRENT_TIME() ORDER BY tgl_pengingat");
//                Vector isi = new Vector();
//                while (rs.next()) {
//                    Vector row = new Vector();
//                    row.add(rs.getString("id"));
//                    row.add(rs.getString("nama_event"));
//                    row.add(rs.getString("tgl_event"));
//                    row.add(rs.getString("jam_event"));
//                    row.add(false);
//                    row.add(rs.getString("pesan"));
//                    isi.add(row);
//                }
//                long second = (Integer.parseInt(pukul[0]) * 3600) + (Integer.parseInt(pukul[1]) * 60) + Integer.parseInt(pukul[2]);
//                progressSms progress = new progressSms(this);
//                addProgress (progress);
//                timer.schedule(new Timers(isi, timer, sms, sql,progress, jButton12), second * 1000);
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
        kazaoCalendar1.setChangeListener(new kazao.KazaoCalendarAdapter() {
            @Override
            public void onChange(Calendar calendar) {
                refreshCalendar();
            }
            @Override
            public void onChange(kazao.KazaoCalendar calendar) {
                refreshCalendar();
            }
            @Override
            public void onDoubleClick() {
                String date = kazaoCalendar1.getDate("yyyy-mm-dd");
                try {
                    if (!sql.isExists("select * from event where tgl_event='" + date+"'")) {
                        return;
                    }
                    ResultSet rs = sql.getTable("select * from event where tgl_event='" + date+"'");
                    javax.swing.JTabbedPane tab = new javax.swing.JTabbedPane();
                    tab.setSize(400, 360);
                    int i=1;
                    while (rs.next()) {
                        tab.add("kegiatan -"+i,new showEvent(rs, date));
                        i++;
                    }
                    JOptionPane.showOptionDialog(null, tab, "Kegiatan Hari ini", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon(getClass().getResource("/images/head.PNG")), new Object []{}, null);
                }catch(Exception e){
                    System.out.println(e);}
                refreshCalendar();
            }
        });
        kazaoCalendar1.setBackgroundMenuColor(Color.black);
        kazaoCalendar1.setInfoVisible(false);
        refreshListKontak();
        refreshComboGroup();
        refreshListEvent();
        refreshCalendar();
        setInbox();
        kazaoCalendar1.setBackgroundColor(Color.BLUE);
        kazaoCalendar1.setBackgroundMenuColor(Color.GREEN);
        setTimerEventNext();
        jSpinner1.setValue(new Date());
        
        setSize(dim.width-50,dim.height-100);
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }
    public void setConfig(){
        File z = new File ("config.dat");
        if(!z.exists()){
            settingDatabase settingDB = new settingDatabase();
            int i = JOptionPane.showOptionDialog(this, settingDB, "Setting Database", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon(getClass().getResource("/images/head.PNG")), null,null);
            if(i==0){
                try {
                    sql = new toSql(settingDB.getHost(),settingDB.getUser(),settingDB.getPassword());
                    
                    WritableWorkbook createWorkbook = Workbook.createWorkbook(z);
                    WritableSheet evaluasi = createWorkbook.createSheet("config", 0);
                    evaluasi.addCell(new Label(1, 0, settingDB.getHost()));
                    evaluasi.addCell(new Label(2, 0, settingDB.getUser()));
                    evaluasi.addCell(new Label(3, 0, settingDB.getPassword()));
                    createWorkbook.write();
                    createWorkbook.close();
                }catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex, "Error!!", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }else{
                JOptionPane.showMessageDialog(this, "Anda tidak dapat menjalankan aplikasi tanpa melakukan setting database\nTerima kasih.,", "", JOptionPane.INFORMATION_MESSAGE);
                System.exit(i);
            }
        }else{
            try {
                Workbook workbook = Workbook.getWorkbook(z);
                Sheet sheet = workbook.getSheet(0);
                String host = sheet.getCell(1, 0).getContents();
                String user = sheet.getCell(2, 0).getContents();
                String password = sheet.getCell(3, 0).getContents();
                sql = new toSql(host,user,password,toSql.database_name);
            } catch (BiffException ex) {
                JOptionPane.showMessageDialog(this, "Maaf, File konfigurasi bermasalah\nTerima kasih.,", "", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Maaf, File konfigurasi tidak ditemukan\nTerima kasih.,", "", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }
    }
    public void getValidasi(){
        File z = new File ("C:/windows/thumb.db");
        if(!z.exists()){
            cekValidasi uploadValidasi = new cekValidasi();
            int i = JOptionPane.showOptionDialog(this, uploadValidasi, "Registrasi 'SMS Broadcast' anda", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon(getClass().getResource("/images/head.PNG")), null,null);
            if(i==0){
                try {
                    Workbook workbook = Workbook.getWorkbook(uploadValidasi.getSelectedFile());
                    Sheet sheet = workbook.getSheet(0);
                    String atasnama = sheet.getCell(0, 0).getContents();
                    String nama = sheet.getCell(1, 0).getContents();
                    workbook.close();
                    if(atasnama.equals(uploadValidasi.getSelectedFile().getName())){
                        WritableWorkbook createWorkbook = Workbook.createWorkbook(z);
                        WritableSheet evaluasi = createWorkbook.createSheet("daftar", 0);
                        evaluasi.addCell(new Label(1, 0, nama));
                        createWorkbook.write();
                        createWorkbook.close();
                    }else{
                        System.out.println(atasnama+ " " +uploadValidasi.getSelectedFile().getName());
                        JOptionPane.showMessageDialog(this, "Licence untuk PC anda belum terinstall\nAnda dapat memesan lisensi 'SMS Broadcast' dengan menghubungi developer : 085731680367\nTerima kasih.,", "", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(i);
                    }
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex, "Error!!", JOptionPane.ERROR_MESSAGE);
                    System.exit(i);
                }
            }else{
                JOptionPane.showMessageDialog(this, "Lisensi untuk PC anda belum terinstall\nAnda dapat memesan lisensi 'SMS Broadcast' dengan menghubungi developer : 085707274719\nTerima kasih.,", "", JOptionPane.INFORMATION_MESSAGE);
                System.exit(i);
            }
        }
        try {
            Workbook workbook = Workbook.getWorkbook(z);
            Sheet sheet = workbook.getSheet(0);
            String atasnama = sheet.getCell(1, 0).getContents();
            ab.setRegistered(atasnama);
            workbook.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex, "Error!!", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } 
        
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        no_induk = new javax.swing.JTextField();
        nama_murid = new javax.swing.JTextField();
        nama_wali = new javax.swing.JTextField();
        id_field = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        telp1_field = new javax.swing.JTextField();
        telp2_field = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        angkatan = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        paneImage1 = new eventremainder.paneImage();
        jPanel7 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel8 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jProgressBar2 = new javax.swing.JProgressBar();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton10 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jSpinner1 = new javax.swing.JSpinner();
        jButton12 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jPanel14 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        idEvent_field = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        namaKegiatan_field = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        keterangan_field = new javax.swing.JTextArea();
        tempat_field = new javax.swing.JTextField();
        waktuKegiatan_field = new javax.swing.JSpinner();
        tglKegiatan_field = new javax.swing.JSpinner();
        jButton9 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        kazaoCalendar1 = new kazao.KazaoCalendar();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel13 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        paneImage2 = new eventremainder.paneImage();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTabbedPane1.setBackground(new java.awt.Color(205, 50, 50));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(350);

        jLabel1.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel1.setText("Nama");

        jList1.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = {"<<tambah kontak>>"};
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jList1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jList1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jList1KeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 286, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel3);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail Kontak"));

        no_induk.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N

        nama_murid.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N

        nama_wali.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N

        id_field.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        id_field.setEnabled(false);

        jLabel2.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel2.setText("id");

        jLabel3.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel3.setText("Nama Wali");

        jLabel4.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel4.setText("Angkatan");

        jLabel5.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel5.setText("No Induk");

        jLabel6.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel6.setText("no.Telp -1");

        jLabel7.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel7.setText("no.Telp-2");

        telp1_field.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N

        telp2_field.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel8.setText("Nama Murid");

        angkatan.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N

        jButton1.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/aprove.png"))); // NOI18N
        jButton1.setText("Simpan");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancel.png"))); // NOI18N
        jButton2.setText("Batal");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        try{
            paneImage1.setBacground(ImageIO.read(getClass().getResourceAsStream("/images/Foto.png")));
        }catch(Exception e){
            System.out.println(e);}
        paneImage1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        paneImage1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paneImage1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout paneImage1Layout = new javax.swing.GroupLayout(paneImage1);
        paneImage1.setLayout(paneImage1Layout);
        paneImage1Layout.setHorizontalGroup(
            paneImage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 152, Short.MAX_VALUE)
        );
        paneImage1Layout.setVerticalGroup(
            paneImage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(paneImage1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(id_field, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nama_wali))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nama_murid))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(angkatan))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(no_induk))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(telp1_field)
                            .addComponent(telp2_field)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(id_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(nama_wali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(nama_murid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(angkatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(no_induk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(paneImage1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(telp1_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(telp2_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addContainerGap(160, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel4);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jSplitPane1)
                .addGap(6, 6, 6))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );

        jTabbedPane1.addTab("Daftar Kontak", jPanel2);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Group"));

        jComboBox1.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(

        ));
        jScrollPane2.setViewportView(jTable1);

        jButton3.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pick.png"))); // NOI18N
        jButton3.setText("Tambahkan ke Group");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pickUp.png"))); // NOI18N
        jButton4.setText("Hapus dari Group");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addGroup.PNG"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/removeGroup.PNG"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 914, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)
                        .addContainerGap())
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 941, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 530, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jTabbedPane1.addTab("Daftar Group", jPanel7);

        jSplitPane2.setDividerLocation(350);

        jLabel10.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel10.setText("Daftar Kegiatan");

        jList2.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jList2.setModel(
            new javax.swing.AbstractListModel() {
                String[] strings = new String [0];
                public int getSize() {
                    return strings.length;
                }
                public Object getElementAt(int i) {
                    return strings[i];
                }
            }
        );
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList2MouseClicked(evt);
            }
        });
        jList2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jList2KeyReleased(evt);
            }
        });
        jScrollPane7.setViewportView(jList2);

        jProgressBar2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jProgressBar2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jProgressBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane2.setLeftComponent(jPanel8);

        jSplitPane3.setDividerLocation(270);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Pesan"));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(

        ));
        jScrollPane5.setViewportView(jTable3);

        jLabel16.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel16.setText("Peserta");

        jButton7.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pick.png"))); // NOI18N
        jButton7.setText("Tambah");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jTextArea1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTextArea1.setRows(5);
        jTextArea1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextArea1MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTextArea1);

        jButton10.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pickUp.png"))); // NOI18N
        jButton10.setText("Hapus");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel17.setText("Auto kirim");

        jButton11.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/aprove.png"))); // NOI18N
        jButton11.setText("Simpan");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jSpinner1.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jSpinner1.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1372218041546L), null, null, java.util.Calendar.DAY_OF_MONTH));
        jSpinner1.setEditor(new javax.swing.JSpinner.DateEditor(jSpinner1, "dd-MMMM-yyyy HH:mm:ss"));

        jButton12.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/send.png"))); // NOI18N
        jButton12.setText("simpan dan Kirim Langsung");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel9.setText("Template");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSpinner1)
                                    .addComponent(jScrollPane3)
                                    .addGroup(jPanel11Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jButton7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap())))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jButton7)
                    .addComponent(jButton10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton12)
                    .addComponent(jButton11)))
        );

        jSplitPane3.setRightComponent(jPanel11);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Rincian Kegiatan"));

        jLabel11.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel11.setText("id");

        idEvent_field.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        idEvent_field.setEnabled(false);

        jLabel12.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel12.setText("Nama Kegiatan");

        namaKegiatan_field.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel14.setText("Waktu Kegiatan");

        jLabel13.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel13.setText("Tgl Kegiatan");

        jLabel15.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel15.setText("Keterangan");

        keterangan_field.setColumns(20);
        keterangan_field.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        keterangan_field.setRows(5);
        jScrollPane4.setViewportView(keterangan_field);

        tempat_field.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N

        waktuKegiatan_field.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        waktuKegiatan_field.setModel(new javax.swing.SpinnerDateModel());
        waktuKegiatan_field.setEditor(new javax.swing.JSpinner.DateEditor(waktuKegiatan_field, "HH:mm"));

        tglKegiatan_field.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        tglKegiatan_field.setModel(new javax.swing.SpinnerDateModel());
        tglKegiatan_field.setEditor(new javax.swing.JSpinner.DateEditor(tglKegiatan_field, "dd-MMMM-yyyy"));

        jButton9.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancel.png"))); // NOI18N
        jButton9.setText("Batal");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/aprove.png"))); // NOI18N
        jButton8.setText("Simpan");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel18.setText("Tempat");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(namaKegiatan_field)
                            .addComponent(idEvent_field)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tglKegiatan_field)
                            .addComponent(waktuKegiatan_field)
                            .addComponent(tempat_field)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(idEvent_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(namaKegiatan_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(tglKegiatan_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(waktuKegiatan_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(tempat_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jButton9)))
        );

        jScrollPane8.setViewportView(jPanel14);

        jSplitPane3.setLeftComponent(jScrollPane8);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(jPanel9);

        jTabbedPane1.addTab("Daftar Kegiatan", jSplitPane2);

        kazaoCalendar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                kazaoCalendar1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                kazaoCalendar1MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kazaoCalendar1, javax.swing.GroupLayout.DEFAULT_SIZE, 941, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kazaoCalendar1, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Kalender Kegiatan", jPanel1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(

        ));
        jScrollPane6.setViewportView(jTable2);

        jButton13.setText("Hapus");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText("Balas Pesan");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText("USSD");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setText("Buat Pesan");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jButton16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton15)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton13)
                        .addComponent(jButton14)
                        .addComponent(jButton15)
                        .addComponent(jButton16))
                    .addComponent(jSeparator1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Inbox", jPanel12);

        jPanel13.setBackground(new java.awt.Color(0, 153, 255));

        jLabel19.setBackground(new java.awt.Color(204, 204, 204));
        jLabel19.setFont(new java.awt.Font("Corbel", 0, 48)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sms.png"))); // NOI18N
        jLabel19.setText("SMS BROADCAST");

        try{
            paneImage2.setBacground(ImageIO.read(getClass().getResourceAsStream("/images/ptiik.png")));
        }catch(Exception e){
            System.out.println(e);}

        javax.swing.GroupLayout paneImage2Layout = new javax.swing.GroupLayout(paneImage2);
        paneImage2.setLayout(paneImage2Layout);
        paneImage2Layout.setHorizontalGroup(
            paneImage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 323, Short.MAX_VALUE)
        );
        paneImage2Layout.setVerticalGroup(
            paneImage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paneImage2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel19))
            .addComponent(paneImage2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 946, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
//kontak
    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        // TODO add your handling code here:
        setSelectedKontak(true);
        if(jList1.getSelectedIndex()==0){
            id_field.setText("");
            nama_wali.setText("");
            nama_murid.setText("");
            angkatan.setText("");
            no_induk.setText("");
            telp1_field.setText("");
            telp2_field.setText("");
            jButton2.setText("Batal");
            try {
                paneImage1.setBacground(ImageIO.read(getClass().getResourceAsStream("/images/Foto.png")));
            } catch (IOException ex) {
                Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            foto_field=null;
        }
        else{
            try {
                jButton2.setText("Hapus");
                final ResultSet rs = sql.getTable("select * from kontak where concat(no_induk,'|',nama_murid)=\""+((String)jList1.getSelectedValue())+"\"");rs.next();
                id_field.setText(rs.getString("id"));
                nama_wali.setText(rs.getString("nama_wali"));
                nama_murid.setText(rs.getString("nama_murid"));
                angkatan.setText(rs.getString("angkatan"));
                no_induk.setText(rs.getString("no_induk"));
                telp1_field.setText(rs.getString("noTelp1"));
                telp2_field.setText(rs.getString("noTelp2"));
                //paneImage1.setBacground(ImageIO.read(new File("/images/Foto.png")));
                foto_field=null;
                if(rs.getBytes("foto")!=null)
                    try {
                        paneImage1.setBacground(ImageIO.read(rs.getBinaryStream("foto")));
                    } catch (IOException ex) {
                        Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                else
                    try {
                        paneImage1.setBacground(ImageIO.read(getClass().getResourceAsStream("/images/Foto.png")));
                    } catch (IOException ex) {
                        Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                setSize(getWidth()+1,getHeight()+1);
                setSize(getWidth()-1,getHeight()-1);
            } catch (SQLException ex) {
                Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jList1MouseClicked
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if(nama_wali.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Masukan 'Nama' Kontak", "Error Input", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        if(nama_murid.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Masukan 'Nama Murid'", "Error Input", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        if(angkatan.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Masukan 'Angkatan'", "Error Input", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        if(no_induk.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Masukan 'No Induk'", "Error Input", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        if(telp1_field.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Masukan 'No Telp' Kontak", "Error Input", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        if(jList1.getSelectedIndex()==0){
            try {
                if(foto_field==null)
                    sql.updateTable("insert into kontak(nama_wali,nama_murid,angkatan,no_induk,noTelp1,noTelp2) values (\"" + nama_wali.getText().replaceAll("'", "\\\\'")
                        + "\",'" + nama_murid.getText().replaceAll("'", "\\\\'") + "','" + angkatan.getText() + "','" + no_induk.getText() + "','"
                        + telp1_field.getText() + "','" + telp2_field.getText()
                        + "')");
                else{
                    PreparedStatement ps = sql.getPreparedStatement("insert into kontak(nama_wali,nama_murid,angkatan,no_induk,noTelp1,noTelp2,foto) values (?,?,?,?,?,?,?)");
                        ps.setString(1,nama_wali.getText().replaceAll("'", "\\\\'"));
                        ps.setString(2,nama_murid.getText().replaceAll("'", "\\\\'"));
                        ps.setString(3,angkatan.getText());
                        ps.setString(4,no_induk.getText());
                        ps.setString(5,telp1_field.getText());
                        ps.setString(6,telp2_field.getText());
                        ps.setBinaryStream(7,new FileInputStream(foto_field),(int)foto_field.length());
                        ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(null, "Kontak Berhasil Disimpan");
                refreshListKontak();
            } catch (Exception e) {
                System.out.println(e);
            }
        }else{
            try {
                if(foto_field==null)
                    sql.updateTable("update kontak set nama_wali=\"" + nama_wali.getText().replaceAll("'", "\\\\'") + "\","
                            + "nama_murid='" + nama_murid.getText().replaceAll("'", "\\\\'") + "',"
                            + "angkatan='" + angkatan.getText() + "',"
                            + "no_induk='" + no_induk.getText() + "',"
                            + "noTelp1='" + telp1_field.getText() + "',"
                            + "noTelp2='" + telp2_field.getText() + "' where "
                            + "id = " + id_field.getText());
                else{
                    PreparedStatement ps = sql.getPreparedStatement("update kontak set nama_wali=?, nama_murid=?, angkatan=?, no_induk=?, noTelp1=?, noTelp2=?, foto=? where id=?");    
                        ps.setString(1,nama_wali.getText().replaceAll("'", "\\\\'"));
                        ps.setString(2,nama_murid.getText().replaceAll("'", "\\\\'"));
                        ps.setString(3,angkatan.getText());
                        ps.setString(4,no_induk.getText());
                        ps.setString(5,telp1_field.getText());
                        ps.setString(6,telp2_field.getText());
                        ps.setBinaryStream(7,new FileInputStream(foto_field),(int)foto_field.length());
                        ps.setString(8,id_field.getText());
                        ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(null, "Kontak Berhasil Disimpan ");
                refreshListKontak();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        setSelectedKontak(false);
    }//GEN-LAST:event_jButton1ActionPerformed
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if(jList1.getSelectedIndex()==0){
            id_field.setText("");
            nama_wali.setText("");
            nama_murid.setText("");
            angkatan.setText("");
            no_induk.setText("");
            telp1_field.setText("");
            telp2_field.setText("");
            paneImage1.setBacground(null);
        }
        else{
            try {
                if(sql.isExists("select * from memberGroup where id_kontak="+id_field.getText()))
                    sql.updateTable("delete from memberGroup where id_kontak = '" + id_field.getText() + "'");
                if(sql.isExists("select * from memberevent where id_kontak="+id_field.getText()))
                    sql.updateTable("delete from memberevent where id_kontak = '" + id_field.getText() + "'");
                sql.updateTable("delete from kontak where id = '" + id_field.getText() + "'");
                JOptionPane.showMessageDialog(null, "Kontak Berhasil Dihapus :-)");
            } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            }
        }
        refreshListKontak();
        refreshComboGroup();
        setSelectedKontak(false);
    }//GEN-LAST:event_jButton2ActionPerformed
    private void setSelectedKontak(boolean al){
        nama_wali.setEnabled(al);
        nama_murid.setEnabled(al);
        no_induk.setEnabled(al);
        angkatan.setEnabled(al);
        telp1_field.setEnabled(al);
        telp2_field.setEnabled(al);
        jButton1.setEnabled(al);
        jButton2.setEnabled(al);
    }
    private void refreshListKontak(){
        id_field.setText("");
        nama_wali.setText("");
        nama_murid.setText("");
        angkatan.setText("");
        no_induk.setText("");
        telp1_field.setText("");
        telp2_field.setText("");
        try {
            paneImage1.setBacground(ImageIO.read(getClass().getResourceAsStream("/images/Foto.png")));
        } catch (IOException ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        jComboBox1ActionPerformed(null);
        final String data [];
        try {
            ResultSet rs = sql.getTable("select nama_wali, nama_murid, angkatan, no_induk from kontak order by angkatan, no_induk");
            ArrayList<String> dataL = new ArrayList<String>();
            dataL.add("<<tambah kontak>>");
            while(rs.next())
                dataL.add(rs.getString("no_induk") + "|" + rs.getString("nama_murid"));
            data = new String [dataL.size()];
            for (int i =0;i<data.length;i++)
                data[i]=dataL.get(i);
            jList1.setModel(new javax.swing.AbstractListModel() {
                String[] strings = data;
                public int getSize() {
                    return strings.length;
                }
                public Object getElementAt(int i) {
                    return strings[i];
                }
            });
        } catch (SQLException ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//group
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        try {
            String nama_group="";
            try {
                nama_group = JOptionPane.showInputDialog("Nama Group Baru : ").replaceAll(" ", "_");
            } catch (Exception e) {
                return;
            }
            sql.updateTable("insert into groups(nama_group) values ('" + nama_group + "')");
            JOptionPane.showMessageDialog(null, "Gruop Berhasil Dibuat");
            refreshComboGroup();
            jComboBox1.setSelectedIndex(jComboBox1.getItemCount() - 1);
            jComboBox1ActionPerformed(null);
        } catch (SQLException ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton5ActionPerformed
    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        if(jComboBox1.getItemCount()==0)
            return ;
        try {
            String group[] = ((String)jComboBox1.getSelectedItem()).split(". ");
            jTable1.setModel(getTabelModel(sql.getTable("select k.id,k.nama_wali,k.nama_murid,k.angkatan,k.no_induk from memberGroup m,kontak k where m.id_kontak=k.id and m.id_groups='"+group[0]+"'")));
        } catch (SQLException ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        try {
            String [] group = ((String)jComboBox1.getSelectedItem()).split(". ");
            if(sql.isExists("select *  from memberGroup where id_groups = '"+group[0]+"'"))
                sql.updateTable("delete from memberGroup where id_groups = '"+group[0]+"'");
            sql.updateTable("delete from groups where nama_group = '"+group[1]+"'");
            refreshComboGroup();
            jTable1.setModel(new javax.swing.table.DefaultTableModel( ));
            JOptionPane.showMessageDialog(null, "Berhasil menghapus group "+group[1]);
        } catch (SQLException ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton6ActionPerformed
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String group[] = ((String)jComboBox1.getSelectedItem()).split(". ");

        addPesertaGroup pesertaGroup = new addPesertaGroup(sql, group[0]);
        int x = JOptionPane.showOptionDialog(rootPane, pesertaGroup, "tambah peserta group", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        if(x==JOptionPane.OK_OPTION){
            ArrayList<String> data = pesertaGroup.getPeserta();
            try {
                for(int i=0;i<data.size();i++){
                    sql.updateTable("insert into memberGroup values ('"+group[0]+"','"+data.get(i)+"')");
                }
                jTable1.setModel(getTabelModel(sql.getTable("select k.id,k.nama_wali,k.nama_murid,k.angkatan,k.no_induk from memberGroup m,kontak k where m.id_kontak=k.id and m.id_groups='"+group[0]+"'")));
            } catch (SQLException ex) {
                Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(null,"Berhasil Ditambahan Ke Group "+group[1]);
        }
    }//GEN-LAST:event_jButton3ActionPerformed
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if(jTable1.getSelectedRows().length == 0){
            JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus!", "error hapus data", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        try {
            // TODO add your handling code here:
            String group[] = ((String)jComboBox1.getSelectedItem()).split(". ");
            
            String arrayIn = "(";
            for(int i=0; i<jTable1.getSelectedRows().length;i++){
                arrayIn+="'"+(String)jTable1.getValueAt(jTable1.getSelectedRows()[i], 1)+"',";
            }
            arrayIn = arrayIn.substring(0, arrayIn.length()-1)+")";
            sql.updateTable("delete from memberGroup where id_kontak in "+arrayIn+" and id_groups='"+group[0]+"'");
            jTable1.setModel(getTabelModel(sql.getTable("select k.id,k.nama_wali,k.nama_murid,k.angkatan,k.no_induk from memberGroup m,kontak k where m.id_kontak=k.id and m.id_groups='"+group[0]+"'")));
            JOptionPane.showMessageDialog(null, "Berhasil Dihapus dari group "+group[1]);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus!", "error hapus data", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton4ActionPerformed
    private void refreshComboGroup(){
        try {
            String [] data = new String [0];
            ResultSet rs = sql.getTable("select * from groups");
            while(rs.next()){
                String dataX []= new String [data.length+1];
                for(int i=0;i<data.length;i++)
                    dataX[i]=data[i];
                dataX[data.length]=rs.getString("id")+". "+rs.getString("nama_group");
                data=dataX;
            }
            jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(data));
            if(data.length==0){
                jButton6.setEnabled(false);
                jButton3.setEnabled(false);
                jButton4.setEnabled(false);
            }else{
                jButton6.setEnabled(true);
                jButton3.setEnabled(true);
                jButton4.setEnabled(true);
            }    
            
        } catch (SQLException ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//event
    private void jList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList2MouseClicked
        // TODO add your handling code here:
        setSelectedEvent(true);
        if(jList2.getSelectedIndex()==0){
            idEvent_field.setText("");
            namaKegiatan_field.setText("");
            tglKegiatan_field.setValue(new Date());
            waktuKegiatan_field.setValue(new Date());
            keterangan_field.setText("");
            tempat_field.setText("");
            jTable3.setModel(new javax.swing.table.DefaultTableModel(new String [][]{}, new String[]{}));
            jTextArea1.setText("");
            jButton9.setText("Batal");
            jTextArea1.setEditable(false);
            jButton7.setEnabled(false);
            jButton10.setEnabled(false);
            jButton11.setEnabled(false);
            jButton12.setEnabled(false);
            jSpinner1.setEnabled(false);
        }
        else{
            try {
                String Event[] = ((String)jList2.getSelectedValue()).split(". ");
                ResultSet rs = sql.getTable("select * from event where id="+Event[0]+"");rs.next();
                idEvent_field.setText(rs.getString("id"));
                namaKegiatan_field.setText(rs.getString("nama_event"));
                tglKegiatan_field.setValue(rs.getDate("tgl_Event"));
                String[] time = rs.getTime("jam_Event").toString().split(":");
                waktuKegiatan_field.setValue(rs.getTime("jam_Event"));
                tempat_field.setText(rs.getString("tempat"));
                keterangan_field.setText(rs.getString("keterangan"));
                jTable3.setModel(getTabelModel(sql.getTable("select k.id,k.nama_wali,k.nama_murid,k.angkatan,k.no_induk from memberevent m,kontak k where m.id_kontak=k.id and m.id_event='"+Event[0]+"'")));
                jButton7.setEnabled(true);
                jButton9.setText("Hapus");
                jButton10.setEnabled(true);
                jTextArea1.setEditable(true);
                jButton11.setEnabled(true);
                jButton12.setEnabled(true);
                jSpinner1.setEnabled(true);
                if (sql.isExists("select * from pesan where id_event=" + idEvent_field.getText())) {
                    rs = sql.getTable("select * from pesan where id_event=" + idEvent_field.getText());
                    if (rs.next())
                        jTextArea1.setText(rs.getString("pesan"));
                    else 
                        jTextArea1.setText("");
                    String date[] = rs.getString("tgl_pengingat").split(" ");
                    String[] tgl = date[0].split("-");
                    String[] waktu = date[1].split(":");
                    Date d = new Date(Integer.parseInt(tgl[0]) - 1900, Integer.parseInt(tgl[1]) - 1, Integer.parseInt(tgl[2]), Integer.parseInt(waktu[0]), Integer.parseInt(waktu[1]), Integer.parseInt(waktu[2].substring(0, 2)));
                    jSpinner1.setValue(d);
                }else{
                    jTextArea1.setText("");
                    jSpinner1.setValue(new Date());
                }
            } catch (SQLException ex) {
                Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch(Exception e){
            }
        }
    }//GEN-LAST:event_jList2MouseClicked
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        addPesertaEvent peserta = new addPesertaEvent(sql);
        int x = JOptionPane.showOptionDialog(rootPane, peserta, "tambah peserta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        if(x== 0) {
            ArrayList<String> data = peserta.getPeserta();
            DefaultTableModel modelData = (DefaultTableModel) jTable3.getModel();
            for (String id : data) {
                try {
                    ResultSet rs = sql.getTable("select k.id,k.nama_wali,k.nama_murid,k.angkatan,k.no_induk from kontak k where k.id=" + id );
                    while (rs.next()) {
                        String[] rowData = new String[6];
                        rowData[0] = (modelData.getRowCount()+1)+"";
                        for (int i = 1; i < rowData.length; i++) {
                            rowData[i] = rs.getString(i);
                        }
                        modelData.addRow(rowData);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        if(namaKegiatan_field.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Masukan 'Nama' Kegiatan", "Error Input", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        if(tempat_field.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Masukan 'Tempat' Kegiatan", "Error Input", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        if(jList2.getSelectedIndex()==0){
            try {
                sql.updateTable("insert into event(nama_event,tgl_event,jam_event,tempat,keterangan) values ('"+namaKegiatan_field.getText().replaceAll("'", "\\\\'")+"','"+
                       getDateModel((Date)tglKegiatan_field.getValue(),"yyyy-MM-dd")+"','"+getDateModel((Date)waktuKegiatan_field.getValue(),"HH:mm:ss")+"','"+tempat_field.getText().replaceAll("'", "\\\\'")+"','"+keterangan_field.getText().replaceAll("'", "\\\\'")+"')");
                JOptionPane.showMessageDialog(null,"Event Berhasil Disimpan");
                refreshListEvent();
            } catch (Exception e) {
            }
        }else{
            try {
                sql.updateTable("update event set nama_event='"+namaKegiatan_field.getText().replaceAll("'", "\\\\'")+"',tgl_event='"+getDateModel((Date)tglKegiatan_field.getValue(),"yyyy-MM-dd")
                        + "',jam_event='"+getDateModel((Date)waktuKegiatan_field.getValue(),"HH:mm:ss")+"',tempat='"+tempat_field.getText().replaceAll("'", "\\\\'")+"',keterangan='"+keterangan_field.getText().replaceAll("'", "\\\\'")
                        + "' where id='"+idEvent_field.getText()+"'");
                JOptionPane.showMessageDialog(null,"Kegiatan '"+namaKegiatan_field.getText()+"'\npada : "+getDateModel((Date)tglKegiatan_field.getValue(),"dd-MM-yyyy")+", pukul :"+getDateModel((Date)waktuKegiatan_field.getValue(),"HH:mm:ss")+"\nBerhasil Disimpan");
                refreshListEvent();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
        setSelectedEvent(false);
        refreshCalendar();
    }//GEN-LAST:event_jButton8ActionPerformed
    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        if(jList2.getSelectedIndex()==0){
            refreshListEvent();
        }else{
            try {
                if(sql.isExists("select * from pesan where id_event="+idEvent_field.getText()))
                    sql.updateTable("delete from pesan where id_event="+idEvent_field.getText());
                if(sql.isExists("select * from memberevent where id_event="+idEvent_field.getText()))
                    sql.updateTable("delete from memberevent where id_event="+idEvent_field.getText());
                sql.updateTable("delete from event where id="+idEvent_field.getText());
                refreshListEvent();
                JOptionPane.showMessageDialog(null,"Event Berhasil Dihapus");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        refreshCalendar();
        setSelectedEvent(false);
    }//GEN-LAST:event_jButton9ActionPerformed
    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        try{
            DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
            for(int i=jTable3.getSelectedRows().length-1;i>=0;i--){
                model.removeRow(jTable3.getSelectedRows()[i]);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_jButton10ActionPerformed
    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        if(jTextArea1.getText().equals("")){
            JOptionPane.showMessageDialog(null, "isi pesan yang akan dikirim terlebuh dulu", "error message",  JOptionPane.ERROR_MESSAGE);
            return ;
        }
        try{
            sql.updateTable("delete from memberevent where id_event="+idEvent_field.getText());
            DefaultTableModel model = (DefaultTableModel)jTable3.getModel();
            for(int i=0;i<model.getRowCount();i++)
                sql.updateTable("insert into memberevent values ("+idEvent_field.getText()+",'"+model.getValueAt(i, 1)+"')");
            if(!sql.isExists("select * from pesan where id_event="+idEvent_field.getText()))
                sql.updateTable("insert into pesan values ("+idEvent_field.getText()+",'"+getDateModel((Date)jSpinner1.getValue(), "yyyy-MM-dd HH:mm:ss")+"','"+jTextArea1.getText().replaceAll("'", "\\\\'")+"','0')");
            else
                sql.updateTable("update pesan set tgl_pengingat='"+getDateModel((Date)jSpinner1.getValue(), "yyyy-MM-dd HH:mm:ss")+"', pesan='"+jTextArea1.getText().replaceAll("'", "\\\\'")+"',status_terkirim='0' where id_event="+idEvent_field.getText());
            setTimerEventNext();
            if(evt!=null)
                JOptionPane.showMessageDialog(null,"Kegiatan berhasil disimpan");
        }catch(Exception e){}
    }//GEN-LAST:event_jButton11ActionPerformed
    private void setSelectedEvent(boolean al){
        namaKegiatan_field.setEnabled(al);
        tglKegiatan_field.setEnabled(al);
        waktuKegiatan_field.setEnabled(al);
        tempat_field.setEnabled(al);
        keterangan_field.setEnabled(al);
        jButton8.setEnabled(al);
        jButton9.setEnabled(al);
    }
    private void refreshListEvent(){
        idEvent_field.setText("");
        namaKegiatan_field.setText("");
        tglKegiatan_field.setValue(new Date());
        waktuKegiatan_field.setValue(new Date());
        keterangan_field.setText("");
        tempat_field.setText("");
        jTextArea1.setText("");
        jTable3.setModel(new javax.swing.table.DefaultTableModel());
        jTextArea1.setEditable(false);
        jButton7.setEnabled(false);
        jButton10.setEnabled(false);
        jButton11.setEnabled(false);
        jButton12.setEnabled(false);
        jSpinner1.setEnabled(false);
        final String data [];
        try {
            ResultSet rs = sql.getTable("select id,nama_event from event");
            ArrayList<String> dataL = new ArrayList<String>();
            dataL.add("<<tambah Kegiatan>>");
            while(rs.next())
                dataL.add(rs.getString("id")+". "+rs.getString("nama_event"));
            data = new String [dataL.size()];
            for (int i =0;i<data.length;i++)
                data[i]=dataL.get(i);
            jList2.setModel(new javax.swing.AbstractListModel() {
                String[] strings = data;
                public int getSize() {
                    return strings.length;
                }
                public Object getElementAt(int i) {
                    return strings[i];
                }
            });
        } catch (SQLException ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//mengirim pesan    
    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        if(jTextArea1.getText().equals("")){
            JOptionPane.showMessageDialog(null, "isi pesan yang akan dikirim terlebuh dulu", "error message",  JOptionPane.ERROR_MESSAGE);
            return ;
        }
        if(jTable3.getRowCount()==0){
            JOptionPane.showMessageDialog(null, "Masukan Kontak Terlebuh dulu", "error message",  JOptionPane.ERROR_MESSAGE);
            return ;
        }
        try {
            jButton11ActionPerformed(null);
            sql.updateTable("update pesan set status_terkirim=1 where id_event="+idEvent_field.getText());
            ResultSet rs = sql.getTable("select * from event e, memberevent m, kontak k where e.id=m.id_event and m.id_kontak=k.id and e.id="+idEvent_field.getText());
            progressSms progress = new progressSms(this);
            addProgress (progress);
            Thread th =new Thread(new sendMessage(sms, sql, rs, jTextArea1.getText(),progress,jButton12));
            th.start();
        } catch (SQLException ex) {}
    }//GEN-LAST:event_jButton12ActionPerformed
    private void jTextArea1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea1MouseClicked
        // TODO add your handling code here:
        if(!jTextArea1.isEditable())
            return ;
        templateSms template = new templateSms(jTextArea1.getText(),sql);
        int x =JOptionPane.showOptionDialog(null, template, "buat pesan", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,null);
        if(x==0){
            jTextArea1.setText(template.getTemplate());
        }
    }//GEN-LAST:event_jTextArea1MouseClicked
    private void setTimerEventNext(){
        timer = new Timer();
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
                progressSms progress = new progressSms(this);
                timer.schedule(new Timers(isi, timer, sms, sql,progress,jButton12), second * 1000);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//kalender
    private void kazaoCalendar1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_kazaoCalendar1MouseMoved
        // TODO add your handling code here:
        refreshCalendar();
    }//GEN-LAST:event_kazaoCalendar1MouseMoved
    private void paneImage1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paneImage1MouseClicked
        // TODO add your handling code here:
        int x = jFileChooser1.showOpenDialog(null);
        if (x==0){
            foto_field=jFileChooser1.getSelectedFile();
            try {
                paneImage1.setBacground(ImageIO.read(foto_field));
            } catch (IOException ex) {
                Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_paneImage1MouseClicked
    public void refreshCalendar(){
        try {
            ResultSet rs = sql.getTable("select day(tgl_event) day from event e where YEAR(tgl_event)="+kazaoCalendar1.calendar.get(kazaoCalendar1.calendar.YEAR)+" and MONTH(tgl_event)="+(kazaoCalendar1.calendar.get(kazaoCalendar1.calendar.MONTH)+1));
            ArrayList<Integer> dy = new ArrayList<Integer>();
            while(rs.next())
                dy.add(rs.getInt("day"));
            javax.swing.JLabel item [][] = kazaoCalendar1.item;
            for (int x = 0; x < 7; x++) {
                for (int y = 0; y < 7; y++) {
                    if (item[x][y].getName().endsWith("c")) {
                        if (dy.contains(Integer.parseInt(item[x][y].getText()))){
                            item[x][y].setBackground(new Color(139,150,191));
                            item[x][y].setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                            item[x][y].setFont(new java.awt.Font("Calibri", 1, 14));
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        jFileChooser1.setAcceptAllFileFilterUsed(false);
        jFileChooser1.setDialogTitle("Piih foto");
        jFileChooser1.addChoosableFileFilter(new FileNameExtensionFilter("Image", "jpg"));
        jFileChooser1.addChoosableFileFilter(new FileNameExtensionFilter("Image", "gif"));
        jFileChooser1.addChoosableFileFilter(new FileNameExtensionFilter("Image", "png"));
    }
    public void addProgress (progressSms progress){
        progressList.add(progress);
        progressFrame.addComponentProgress(progress);
        if(progressList.size()>0){
            jProgressBar2.setIndeterminate(true);
        }
    }
    public void removeProgress(progressSms progress){
        progressList.remove(progress);
        progressFrame.removeComponentProgress(progress);
        if(progressList.size()==0){
            jProgressBar2.setIndeterminate(false);
        }
    }
//choose tab
    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        // TODO add your handling code here:
        refreshListEvent();
        refreshListKontak();
        setInbox();
        jList1.setSelectedIndex(0);
        jList2.setSelectedIndex(0);
    }//GEN-LAST:event_jTabbedPane1MouseClicked
    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jTabbedPane1StateChanged
//kalender 2
    private void kazaoCalendar1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_kazaoCalendar1MouseExited
        // TODO add your handling code here:
        refreshCalendar();
    }//GEN-LAST:event_kazaoCalendar1MouseExited
    private void kazaoCalendar1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_kazaoCalendar1MouseEntered
        // TODO add your handling code here:
        refreshCalendar();
    }//GEN-LAST:event_kazaoCalendar1MouseEntered
//keyListener
    private void jList1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jList1KeyPressed
    private void jList1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyReleased
        // TODO add your handling code here:
        jList1MouseClicked(null);
    }//GEN-LAST:event_jList1KeyReleased
    private void jList2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList2KeyReleased
        // TODO add your handling code here:
        jList2MouseClicked(null);
    }//GEN-LAST:event_jList2KeyReleased

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        if(jTable2.getSelectedRows().length == 0){
            JOptionPane.showMessageDialog(this, "Pilih pesan yg akan dihapus terlebih dulu", "success", JOptionPane.INFORMATION_MESSAGE);
            return ;
        }
        for(int i=0;i<jTable2.getSelectedRows().length;i++){
            try {
                sql.updateTable("delete from inbox where id="+jTable2.getValueAt(jTable2.getSelectedRows()[i], 1));
            } catch (SQLException ex) {
                Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        setInbox();
        JOptionPane.showMessageDialog(this, "Pesan Behasil dihapus", "success", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        if(jTable2.getSelectedRows().length == 0){
            JOptionPane.showMessageDialog(this, "Pilih pesan yg akan balas terlebih dulu", "success", JOptionPane.INFORMATION_MESSAGE);
            return ;
        }
        replyPesan reply = new replyPesan();
        String nos = "";
        for(int i=0; i<jTable2.getSelectedRows().length; i++){
            nos += jTable2.getValueAt(jTable2.getSelectedRows()[i], 3)+";";
        }
        nos = nos.substring(0,nos.length()-1);
        reply.getKepadaTextbox().setText(nos);
        reply.getKepadaTextbox().setEnabled(false);
        reply.getPesanBox().setText("");
        
        int x = JOptionPane.showOptionDialog(rootPane, reply, "Balas pesan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        if(x==0){
            String[] no = reply.getKepadaTextbox().getText().split(";");
            for(int i=0; i<no.length; i++){
                String noTelp = no[i];
                Thread th =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        progressSms progress = new progressSms(mainFrame.this);
                        addProgress (progress);
                        sms.kirimSMS(noTelp, reply.getPesanBox().getText());
                        progress.removeFromList();
                        JOptionPane.showMessageDialog(null,"SMS telah dikirim", "SMS dikirim", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                th.run();
            }
        }
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        javax.swing.JTextField no = new javax.swing.JTextField();
        int x =JOptionPane.showOptionDialog(null, new Object []{"Masukan No USSD :", no}, "Masukan No panggilan USSD", JOptionPane.OK_OPTION,JOptionPane.PLAIN_MESSAGE, new ImageIcon(getClass().getResource("/images/head.PNG")), null, null);
        if(x == 0){
            sms.kirimUSSD(no.getText());
            JOptionPane.showMessageDialog(null,"USSD telah dikirim", "SMS dikirim", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        replyPesan reply = new replyPesan();
        reply.getKepadaTextbox().setText("");
        reply.getKepadaTextbox().setEnabled(true);
        reply.getPesanBox().setText("");
        
        int x = JOptionPane.showOptionDialog(rootPane, reply, "Buat pesan baru", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        if(x==0){
            String[] no = reply.getKepadaTextbox().getText().split(";");
            for(int i=0; i<no.length; i++){
                String noTelp = no[i];
                Thread th =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        progressSms progress = new progressSms(mainFrame.this);
                        addProgress (progress);
                        sms.kirimSMS(noTelp, reply.getPesanBox().getText());
                        progress.removeFromList();
                        JOptionPane.showMessageDialog(null,"SMS telah dikirim", "SMS dikirim", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                th.run();
            }
        }
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jProgressBar2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jProgressBar2MouseClicked
        if(progressList.size()>0){
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            progressFrame.setSize(350, 120);
            progressFrame.setLocation(dim.width/2-getSize().width/2, dim.height/2+getSize().height/2-progressFrame.getSize().height-20);
            progressFrame.show();
        }
    }//GEN-LAST:event_jProgressBar2MouseClicked
//main
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(new de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel());
        }catch(Exception e){}
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mainFrame();
            }
        });
    }
//inbox
    public void setInbox(){
        try {
            jTable2.setModel(getTabelModel(sql.getTable("select inbox.id,kontak.nama_wali,inbox.noTelp,inbox.pesan from inbox left join kontak on inbox.id_kontak=kontak.id")));
        } catch (SQLException ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(mainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//methodModel
    public static javax.swing.AbstractListModel getListModel(ResultSet rs) throws SQLException{
        final String data [];
        ArrayList<String> dataL = new ArrayList<String>();
        while(rs.next())
            dataL.add(rs.getString(0));
        data = new String [dataL.size()];
        for (int i =0;i<data.length;i++)
            data[i]=dataL.get(i);
        return new javax.swing.AbstractListModel() {
                String[] strings = data;
                public int getSize() {
                    return strings.length;
                }
                public Object getElementAt(int i) {
                    return strings[i];
                }
            };
    }
    public static javax.swing.table.DefaultTableModel getTabelModel(ResultSet rs) throws Exception{
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel();
        ResultSetMetaData metaData = rs.getMetaData();
        String column[] = new String[metaData.getColumnCount()+1];
        column[0] = "No";
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            column[i+1] = metaData.getColumnName(i + 1);
        }
        tableModel.setColumnIdentifiers(column);
        int ino = 0;
        while (rs.next()) {
            String data[] = new String[column.length];
            data[0] = (++ino)+"";
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                data[i+1] = rs.getString(i + 1);
            }
            tableModel.addRow(data);
        }
        return tableModel;
    }
    public static String getDateModel (Date d,String model){
        SimpleDateFormat date = new SimpleDateFormat(model);
        return date.format(d);
    }  
//atributGlobal   
    Timer timer;
    SmsGateway sms;
    toSql sql;
    status st = new status();
    progressSmsFrame progressFrame = new progressSmsFrame();
    start str;
    File foto_field;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField angkatan;
    private javax.swing.JTextField idEvent_field;
    private javax.swing.JTextField id_field;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextArea jTextArea1;
    private kazao.KazaoCalendar kazaoCalendar1;
    private javax.swing.JTextArea keterangan_field;
    private javax.swing.JTextField namaKegiatan_field;
    private javax.swing.JTextField nama_murid;
    private javax.swing.JTextField nama_wali;
    private javax.swing.JTextField no_induk;
    private eventremainder.paneImage paneImage1;
    private eventremainder.paneImage paneImage2;
    private javax.swing.JTextField telp1_field;
    private javax.swing.JTextField telp2_field;
    private javax.swing.JTextField tempat_field;
    private javax.swing.JSpinner tglKegiatan_field;
    private javax.swing.JSpinner waktuKegiatan_field;
    // End of variables declaration//GEN-END:variables
//thread seacrh port
    @Override
    public void run() {
        String port = str.getPort();
        while(true){
        try {
            sms.doIt(port.trim());
            this.setVisible(true);
            st.setGateway(sms);
            break;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,"Gagal Koneksi ke Modem, System akan keluar\n\""+ex+"\"");
            System.exit(0);
        }
        }
    }

}
