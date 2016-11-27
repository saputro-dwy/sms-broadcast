/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package toSql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
/**
 *
 * @author Komando
 */
public class toSql {
    Statement statmen;
    Connection koneksi;
    public static String database_name = "eventreminder";
    public toSql(String host, String user,String password, String db){
        try { 
            Class.forName("com.mysql.jdbc.Driver");
            koneksi = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+db, user, password);
            statmen = koneksi.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"Gagal Koneksi ke Database, System akan keluar\n\""+ex+"\"","Error Database",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,"Gagal Koneksi ke Database, System akan keluar\n\""+ex+"\"","Error Database",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    public toSql(String host, String user,String password){
        try { 
            Class.forName("com.mysql.jdbc.Driver");
            koneksi = DriverManager.getConnection("jdbc:mysql://"+host+"/", user, password);
            statmen = koneksi.createStatement();
            //create db dianggep belum ada
            updateTable("CREATE DATABASE "+toSql.database_name);
            updateTable("USE "+toSql.database_name);
            
            updateTable(initDBEventReminder.get_init_database_event());
            updateTable(initDBEventReminder.get_init_database_groups());
            updateTable(initDBEventReminder.get_init_database_inbox());
            updateTable(initDBEventReminder.get_init_database_kontak());
            updateTable(initDBEventReminder.get_init_database_memberevent());
            updateTable(initDBEventReminder.get_init_database_membergroup());
            updateTable(initDBEventReminder.get_init_database_pesan());
            updateTable(initDBEventReminder.get_init_database_template());
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1007) {
                try {
                    updateTable("USE "+toSql.database_name);
                } catch (SQLException ex1) {
                    Logger.getLogger(toSql.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }else{
                JOptionPane.showMessageDialog(null,"Gagal Koneksi ke Database, System akan keluar\n\""+ex+"\"","Error Database",JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,"Gagal Koneksi ke Database, System akan keluar\n\""+ex+"\"","Error Database",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    public ResultSet getTable(String q) throws SQLException{
        return statmen.executeQuery(q);
    }
    public int getRowCount(ResultSet resultSet) {
        if (resultSet == null) {
            return 0;
        }
        try {
            resultSet.last();
            return resultSet.getRow();
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            try {
                resultSet.beforeFirst();
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        }
        return 0;
    }
    public boolean updateTable(String q) throws SQLException{
        return statmen.execute(q);
    }
    public boolean isExists(String q) throws SQLException{
        ResultSet rs = statmen.executeQuery(q);
        int i=0;
        while(rs.next())
            i++;
        return (i!=0);
    }
    public PreparedStatement getPreparedStatement(String q) throws SQLException{
        return koneksi.prepareStatement(q);
    }
}
