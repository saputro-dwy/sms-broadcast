# sms-broadcast
[![N|Solid](http://filkom.ub.ac.id/assets/ptiik/images/xfilkom_header.png.pagespeed.ic.YFOp_JxhdT.png)](http://filkom.ub.ac.id/)


Proyek dari Tugas Kuliah dulu, yang dirasa dapat membantu untuk dishare. 
Silahkan di utak-atik yg penting logo filkom jgn sampai dihilangkan :D.

Tested 120 sms terkirim semua dengan MODEM WAVECOME.

#Environment 

  - MySQL <- database agar enak aja select dan simpan datanya
  - Driver Modem <- untuk wavecome sih pakek Prolific_DriverInstaller
  - JDK <- karena java, mesti pakek JDK
  - NETBEANS <- karena project lama saya pakek netbeans jadi UI-nya susah kalo setting ulang ke IDE lain, mungkin bisa diambil core-nya aja di folder toModem
  - javax.comm <- ini yg agak ribet karena saya pakek javax.com jadi cuman support di JDK 32 buat yg windows 64 gk ada masalah kalo diinstall JDK 32, compilenya bisa diseting di projectnya (cara pasang-nya bisa tanyakan mbah GOOGLE).

#Library 

  - http://smslib.org <- SMS Gatewaynya
  - MySql Connector <- ke database mysql
