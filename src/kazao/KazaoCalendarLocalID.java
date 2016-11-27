package kazao;

public class KazaoCalendarLocalID extends KazaoCalendarLocal {    
    public KazaoCalendarLocalID() {
        setInfoFormat("dddd, dd-mm-yyyy");
        setShortDateFormat("dd-mm-yyyy");
        setLongDateFormat("dddd, d mmmm yyyy");
        setTimeFormat("hh:ii:ss");
        setDateTimeFormat("dd-mm-yyyy hh:ii:ss");
        setLongDay(new String[] {"Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"});
        setMaxYear(3000);
        setMinYear(1900);
        setLongMonths(new String[] {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "Nopember", "Desember"});
        setShortMonths(new String[] {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nop", "Des"});
        setShortDay(new String[] {"Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"});
        setStartDay(1);
        setTypeOfDay('s');
    }     
}
