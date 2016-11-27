/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package toSql;

/**
 *
 * @author Saputro
 */
public class initDBEventReminder {
    public static String get_init_database_event(){
        return "CREATE TABLE `event` (`id` int(4) NOT NULL AUTO_INCREMENT, `nama_event` varchar(100) NOT NULL,`tgl_event` date NOT NULL,`jam_event` time NOT NULL, `Tempat` varchar(100) NOT NULL,`keterangan` text, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
    }
    public static String get_init_database_groups(){
        return"CREATE TABLE `groups` ( `id` int(11) NOT NULL AUTO_INCREMENT, `nama_group` varchar(100) NOT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
    }
    public static String get_init_database_inbox(){
        return "CREATE TABLE `inbox` ( `id` int(11) NOT NULL AUTO_INCREMENT, `id_kontak` int(4) DEFAULT NULL, `noTelp` varchar(14) DEFAULT NULL, `pesan` text, PRIMARY KEY (`id`), KEY `id_k` (`id_kontak`) ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
    }
    public static String get_init_database_kontak(){
        return "CREATE TABLE `kontak` ( `id` int(4) NOT NULL AUTO_INCREMENT, `nama_wali` varchar(300) NOT NULL, `nama_murid` varchar(300) NOT NULL, `angkatan` varchar(300) NOT NULL, `no_induk` varchar(300) NOT NULL, `foto` longblob, `noTelp1` varchar(14) NOT NULL, `noTelp2` varchar(14) NOT NULL,  PRIMARY KEY (`id`) ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
    }
    public static String get_init_database_memberevent(){
        return "CREATE TABLE `memberevent` ( `id_event` int(4) NOT NULL, `id_kontak` int(4) NOT NULL, KEY `id_kontak` (`id_kontak`), KEY `id_event` (`id_event`), CONSTRAINT `id_event` FOREIGN KEY (`id_event`) REFERENCES `event` (`id`), CONSTRAINT `id_kontak` FOREIGN KEY (`id_kontak`) REFERENCES `kontak` (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
    }
    public static String get_init_database_membergroup(){
        return "CREATE TABLE `membergroup` ( `id_groups` int(4) NOT NULL, `id_kontak` int(4) NOT NULL,  KEY `kontak` (`id_kontak`),  KEY `groups` (`id_groups`),  CONSTRAINT `groups` FOREIGN KEY (`id_groups`) REFERENCES `groups` (`id`),  CONSTRAINT `kontak` FOREIGN KEY (`id_kontak`) REFERENCES `kontak` (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
    }
    public static String get_init_database_pesan(){
        return "CREATE TABLE `pesan` ( `id_event` int(4) NOT NULL, `tgl_pengingat` datetime NOT NULL, `pesan` text NOT NULL, `status_terkirim` tinyint(1) DEFAULT '0', PRIMARY KEY (`id_event`), CONSTRAINT `id` FOREIGN KEY (`id_event`) REFERENCES `event` (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
    }
    public static String get_init_database_template(){
        return "CREATE TABLE `template` ( `id` int(11) NOT NULL AUTO_INCREMENT, `preview` varchar(30) DEFAULT NULL, `template` text, PRIMARY KEY (`id`) ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
    }
}
