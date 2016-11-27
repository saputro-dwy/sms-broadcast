package kazao;

public class KazaoCalendarLocal {
    private static String[] SHORT_DAYS = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private static String[] LONG_DAYS = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private static String[] SHORT_MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static String[] LONG_MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};    
    private static String INFO_FORMAT = "dddd, mm/dd/yyyy";
    private static String SHORT_DATE_FORMAT = "mm/dd/yyyy";
    private static String LONG_DATE_FORMAT = "dddd, mmmm d yyyy";
    private static String TIME_FORMAT = "hh:ii:ss";
    private static String DATE_TIME_FORMAT = "mm/dd/yyyy hh:ii:ss";
    private static int MIN_YEAR = 1900, MAX_YEAR = 3000;
    private static int START_DAY = 0;
    private static char TYPE_OF_DAY = 's';       
    
    
    private String[] shortDays;
    private String[] longDays;
    private String[] shortMonths;
    private String[] longMonths;
    private String infoFormat;
    private String shortDateFormat;
    private String longDateFormat;
    private String timeFormat;
    private String dateTimeFormat;
    private int minYear;
    private int maxYear;
    private int startDay;
    private char typeOfDay;    
    public static KazaoCalendarLocalID LOCAL_ID = new KazaoCalendarLocalID();
    public static KazaoCalendarLocalEN LOCAL_EN = new KazaoCalendarLocalEN();
    public static KazaoCalendarLocalUS LOCAL_US = new KazaoCalendarLocalUS();
    
    public KazaoCalendarLocal() {
        shortDays = SHORT_DAYS;
        longDays = LONG_DAYS;
        longMonths = LONG_MONTHS;
        shortMonths = SHORT_MONTHS;
        infoFormat = INFO_FORMAT;
        shortDateFormat = SHORT_DATE_FORMAT;
        longDateFormat = LONG_DATE_FORMAT;
        dateTimeFormat = DATE_TIME_FORMAT;
        timeFormat = TIME_FORMAT;
        minYear = MIN_YEAR;
        maxYear = MAX_YEAR;
        startDay = START_DAY;
        typeOfDay = TYPE_OF_DAY;
    }
    
    /**
     * shortDays is an array of initial days begining from "sun" end with "sat"
     * default shortDays is "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
     */
    public void setShortDay(String[] shortDays) {
        if (shortDays.length == 7) {
            this.shortDays = shortDays;
        } else {
            try {
                throw new Exception("Invalid shortDays, must array with 7 initial days");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * @return shortDays
     */
    public String[] getShortDay() {
        return shortDays;
    }
    
    /**
     * longDays is an array of initial days begining from "Sunday" end with "Saturday"
     * default longDays is "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
     */
    public void setLongDay(String[] longDays) {
        if (longDays.length == 7) {
            this.longDays = longDays;
        } else {
            try {
                throw new Exception("Invalid longDays, must array with 7 initial days");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * @return longDays
     */
    public String[] getLongDay() {
        return longDays;
    }
    
    /**
     * shortMonths is an array of initial days begining from "January" end with "December"
     * default shortMonths is "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
     */
    public void setShortMonths(String[] months) {
        if (months.length == 12) {
            this.shortMonths = months;
        } else {
            try {
                throw new Exception("Invalid months, must array with 12 initial months");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * 
     * 
     * @return shortMonths
     */
    public String[] getShortMonths() {
        return shortMonths;
    }
    /**
     * longMonths is an array of initial days begining from "January" end with "December"
     * default longMonths is "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
     */
    public void setLongMonths(String[] months) {
        if (months.length == 12) {
            this.longMonths = months;
        } else {
            try {
                throw new Exception("Invalid months, must array with 12 initial months");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * 
     * 
     * @return longMonths
     */
    public String[] getLongMonths() {
        return longMonths;
    }
    /**
     * infoFormat is string to infoFormat date
     * d    = date 1 to 31 depending of month
     * dd   = date with leading zero
     * ddd  = short day name
     * dddd = long day name
     * m    = month 1 to 12
     * mm   = month with leading zero
     * mmm  = short month name
     * mmmm = long month name
     * yy   = short year (2 digit)
     * yyyy = long year (4 digit)
     */
    public void setInfoFormat(String infoFormat) {
        this.infoFormat = infoFormat;
    }
    /**
     * 
     * 
     * 
     * 
     * @return infoFormat
     */
    public String getInfoFormat() {
        return infoFormat;
    }
    /**
     * shortDateFormat is string to shortDateFormat date
     * d    = date 1 to 31 depending of month
     * dd   = date with leading zero
     * ddd  = short day name
     * dddd = long day name
     * m    = month 1 to 12
     * mm   = month with leading zero
     * mmm  = short month name
     * mmmm = long month name
     * yy   = short year (2 digit)
     * yyyy = long year (4 digit)
     */
    public void setShortDateFormat(String formatDateShort) {
        this.shortDateFormat = formatDateShort;
    }
    /**
     * 
     * 
     * 
     * 
     * @return shortDateFormat
     */
    public String getShortDateFormat() {
        return shortDateFormat;
    }
    /**
     * longDateFormat is string to longDateFormat date
     * d    = date 1 to 31 depending of month
     * dd   = date with leading zero
     * ddd  = short day name
     * dddd = long day name
     * m    = month 1 to 12
     * mm   = month with leading zero
     * mmm  = short month name
     * mmmm = long month name
     * yy   = short year (2 digit)
     * yyyy = long year (4 digit)
     */
    public void setLongDateFormat(String formatLongDate) {
        this.longDateFormat = formatLongDate;
    }
    /**
     * 
     * 
     * 
     * 
     * @return longDateFormat
     */
    public String getLongDateFormat() {
        return longDateFormat;
    }
    
        /**
     * longDateFormat is string to longDateFormat date
     * d    = date 1 to 31 depending of month
     * dd   = date with leading zero
     * ddd  = short day name
     * dddd = long day name
     * m    = month 1 to 12
     * mm   = month with leading zero
     * mmm  = short month name
     * mmmm = long month name
     * yy   = short year (2 digit)
     * yyyy = long year (4 digit)
     */
    public void setTimeFormat(String formatTime) {
        this.timeFormat = formatTime;
    }
    /**
     * @return timeFormat
     */
    public String getTimeFormat() {
        return timeFormat;
    }
    
    /**
     * startDay is the begining of day in a week
     */
    public void setStartDay(int startDay) {
        if (startDay >= 0 && startDay <= 6) {
            this.startDay = startDay;
        } else {
            try {
                throw new Exception("Invalid startDay, must 0 to 6");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * @return startDay
     */
    public int getStartDay() {
        return startDay;
    }
    
    /**
     * minYear is the begining of day in a week
     */
    public void setMinYear(int minYear) {
        this.minYear = minYear;
    }
    /**
     * @return minYear
     */
    public int getMinYear() {
        return minYear;
    }
    /**
     * maxYear is the begining of day in a week
     */
    public void setMaxYear(int maxYear) {
        this.maxYear = maxYear;
    }
    /**
     * @return maxYear
     */
    public int getMaxYear() {
        return maxYear;
    }
    /**
     * typeOfDay is type days use to draw this component
     * s = for short says;
     * l = for long days;
     */
    
    public void setTypeOfDay(char typeOfDay) {
        if (typeOfDay == 's' || typeOfDay == 'S' || typeOfDay == 'l' || typeOfDay == 'L') {
            this.typeOfDay = typeOfDay;
        } else {
            try {
                throw new Exception("Invalid typeOfDay, type \"s\" for short day or \"l\" for long day");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * @return typeOfDay
     */
    public char getTypeOfDay() {
        return typeOfDay;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }
}
