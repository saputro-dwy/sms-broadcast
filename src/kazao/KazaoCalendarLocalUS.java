package kazao;

public class KazaoCalendarLocalUS extends KazaoCalendarLocal {    
    public KazaoCalendarLocalUS() {
        setInfoFormat("dddd, mm/dd/yyyy");
        setShortDateFormat("mm/dd/yyyy");
        setLongDateFormat("dddd, mmmm d yyyy");
        setTimeFormat("hh:ii:ss");
        setDateTimeFormat("mm/dd/yyyy hh:ii:ss");
        setLongDay(new String[] {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        setMaxYear(3000);
        setMinYear(1900);
        setLongMonths(new String[] {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"});
        setShortDay(new String[] {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"});
        setShortMonths(new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});
        setStartDay(0);
        setTypeOfDay('s');
    }    
}
