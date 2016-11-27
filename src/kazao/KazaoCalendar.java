package kazao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
public class KazaoCalendar extends javax.swing.JPanel {
    private ImageIcon imToday, imNext, imPrev;
    public Calendar calendar = Calendar.getInstance();
    private boolean loading = false;
    private Color backgroundColor = new java.awt.Color(255, 255, 255);
    
    public JLabel[][] item = new JLabel[7][7];
    private KazaoCalendarLocal localize;
    
    private int cur_x = 1, cur_y = 0;
    
    private Font font = (new java.awt.Font("Arial", 1, 14));
    
    private Font fontMenu, fontDays, fontItem, fontToday, fontSaturday, fontSunday, fontOver, fontOther, fontSelected;
    private Color backgroundMenu, backgroundDays, backgroundItem, backgroundToday, backgroundSaturday, backgroundSunday, backgroundOver, backgroundOther, backgroundSelected,
            foregroundMenu, foregroundDays, foregroundItem, foregroundToday, foregroundSaturday, foregroundSunday, foregroundOver, foregroundOther, foregroundSelected;
    
    private Timer timer = new Timer();
    private boolean doPrev = false, doNext = false;
    private int onTimer = 0;
    private int[] arr = new int[7];
    private int cur_month, cur_year;
    
    private KazaoCalendarAdapter adapter = null;
    private Vector<KazaoCalendarAdapter> adapters = new Vector();
    private ArrayList<Integer> dayEvent = new ArrayList<Integer>();
    public KazaoCalendar() {
        localize = new KazaoCalendarLocal();
        init();
    }
    
    public KazaoCalendar(KazaoCalendarLocal localize) {
        this.localize = localize;
        init();
    }
    public void setEvent(ArrayList<Integer> dy){
        dayEvent=dy;
        updateCalendar();
        update();
    }
    public void setFontCalendar(Font font) {
        this.font = font;
        setFontCalendar();
    }
    
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        update();
    }
    
    public void setDateTime(String dateTime, String dateTimeFormat) throws KazaoCalendarDateTimeException {
        Calendar cal = Calendar.getInstance();
        if (dateTime.length() < dateTimeFormat.length()) {
            throw new KazaoCalendarDateTimeException();
        }
        int date = -1, month = -1, year = -1, hour = -1, minute = -1, second = -1;
        boolean hour_24 = true;
        int index_format = 0, index_date = 0;
        while (true) {
            // date dd
            if (dateTimeFormat.substring(index_format, index_format+Math.min(2, dateTimeFormat.length()-index_format)).startsWith("dd")) {
                try {
                    int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(2, dateTime.length()-index_date)));
                    if (x < 1 || x > 31) {
                        throw new KazaoCalendarDateTimeException("date "+x+" invalid, range is 1 to 31");
                    } else {
                        date = x;
                    }
                } catch (NumberFormatException ex) {
                    throw new KazaoCalendarDateTimeException();
                }
                index_format += 2;
                index_date += 2;
                // date d
            } else if (dateTimeFormat.substring(index_format, index_format+Math.min(1, dateTimeFormat.length()-index_format)).startsWith("d")) {
                if (dateTime.length() - index_date > dateTimeFormat.length() - index_format) {
                    try {
                        if (isNumber(dateTime.charAt(index_date+Math.min(1, dateTime.length()-index_date)))) {
                            int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(2, dateTime.length()-index_date)));
                            if (x < 1 || x > 31) {
                                try {
                                    x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                                    date = x;
                                } catch (NumberFormatException ex) {
                                    ex.printStackTrace();
                                }
                                index_date += 1;
                            } else {
                                date = x;
                                index_date += 2;
                            }
                            
                        } else {
                            int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                            if (x < 1 || x > 31) {
                                throw new KazaoCalendarDateTimeException("date "+x+" invalid, range is 1 to 31");
                            } else {
                                date = x;
                            }
                            index_date += 1;
                        }
                    } catch (NumberFormatException ex) {
                        throw new KazaoCalendarDateTimeException();
                    }
                } else {
                    try {
                        date = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                    } catch (NumberFormatException ex) {
                        throw new KazaoCalendarDateTimeException();
                    }
                    index_date += 1;
                }
                index_format += 1;
                // month mm
            } else if (dateTimeFormat.substring(index_format, index_format+Math.min(2, dateTimeFormat.length()-index_format)).startsWith("mm")) {
                try {
                    int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(2, dateTime.length()-index_date)));
                    if (x < 1 || x > 12) {
                        throw new KazaoCalendarDateTimeException("month "+x+" invalid, range is 1 to 12");
                    } else {
                        month = x;
                    }
                } catch (NumberFormatException ex) {
                    throw new KazaoCalendarDateTimeException();
                }
                index_format += 2;
                index_date += 2;
                // month m
            } else if (dateTimeFormat.substring(index_format, index_format+Math.min(1, dateTimeFormat.length()-index_format)).startsWith("m")) {
                if (dateTime.length() - index_date > dateTimeFormat.length() - index_format) {
                    try {
                        if (isNumber(dateTime.charAt(index_date+Math.min(1, dateTime.length()-index_date)))) {
                            int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(2, dateTime.length()-index_date)));
                            if (x < 1 || x > 12) {
                                try {
                                    x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                                    month = x;
                                    index_date += 1;
                                } catch (NumberFormatException ex) {
                                    throw new KazaoCalendarDateTimeException("month "+x+" invalid, range is 1 to 12");
                                }
                            } else {
                                month = x;
                                index_date += 2;
                            }
                        } else {
                            int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                            if (x < 1 || x > 12) {
                                throw new KazaoCalendarDateTimeException("month "+x+" invalid, range is 1 to 12");
                            } else {
                                month = x;
                            }
                            index_date += 1;
                        }
                    } catch (NumberFormatException ex) {
                        throw new KazaoCalendarDateTimeException();
                    }
                } else {
                    try {
                        month = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                    } catch (NumberFormatException ex) {
                        throw new KazaoCalendarDateTimeException();
                    }
                    index_date += 1;
                }
                index_format += 1;
                // year yy
            } else if (dateTimeFormat.substring(index_format, index_format+Math.min(2, dateTimeFormat.length()-index_format)).startsWith("yy")) {
                try {
                    year = Integer.parseInt(new String(""+Calendar.getInstance().get(cal.YEAR)).substring(0, 2)+dateTime.substring(index_date, index_date+Math.min(2, dateTime.length()-index_date)));
                } catch (NumberFormatException ex) {
                    throw new KazaoCalendarDateTimeException();
                }
                index_format += 2;
                index_date += 2;
                // year yyyy
            } else if (dateTimeFormat.substring(index_format, index_format+Math.min(2, dateTimeFormat.length()-index_format)).startsWith("yyyy")) {
                try {
                    year = Integer.parseInt(dateTime.substring(index_date, Math.min(4, dateTime.length()-index_date)));
                } catch (NumberFormatException ex) {
                    throw new KazaoCalendarDateTimeException();
                }
                index_format += 4;
                index_date += 4;
                // hour hh
            } else if (dateTimeFormat.substring(index_format, index_format+Math.min(2, dateTimeFormat.length()-index_format)).startsWith("hh")) {
                try {
                    int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(2, dateTime.length()-index_date)));
                    if (x < 1 || x > 23) {
                        throw new KazaoCalendarDateTimeException("hour "+x+" invalid, range is 1 to 23");
                    } else {
                        hour = x;
                    }
                } catch (NumberFormatException ex) {
                    throw new KazaoCalendarDateTimeException();
                }
                hour_24 = true;
                index_format += 2;
                index_date += 2;
                // hour h
            } else if (dateTimeFormat.substring(index_format, index_format+Math.min(1, dateTimeFormat.length()-index_format)).startsWith("h")) {
                if (dateTime.length() - index_date > dateTimeFormat.length() - index_format) {
                    try {
                        if (isNumber(dateTime.charAt(index_date+Math.min(1, dateTime.length()-index_date)))) {
                            int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(2, dateTime.length()-index_date)));
                            if (x < 1 || x > 11) {
                                try {
                                    x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                                    hour = x;
                                    index_date += 1;
                                } catch (NumberFormatException ex) {
                                    throw new KazaoCalendarDateTimeException("hour "+x+" invalid, range is 1 to 11");
                                }
                            } else {
                                hour = x;
                                index_date += 2;
                            }
                        } else {
                            int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                            if (x < 1 || x > 11) {
                                throw new KazaoCalendarDateTimeException("hour "+x+" invalid, range is 1 to 11");
                            } else {
                                hour = x;
                            }
                            index_date += 1;
                        }
                    } catch (NumberFormatException ex) {
                        throw new KazaoCalendarDateTimeException();
                    }
                } else {
                    try {
                        int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                        if (x < 1 || x > 11) {
                            throw new KazaoCalendarDateTimeException();
                        } else {
                            hour = x;
                        }
                    } catch (NumberFormatException ex) {
                        throw new KazaoCalendarDateTimeException();
                    }
                    index_date += 1;
                }
                hour_24 = false;
                index_format += 1;
                // minute ii
            } else if (dateTimeFormat.substring(index_format, index_format+Math.min(2, dateTimeFormat.length()-index_format)).startsWith("ii")) {
                try {
                    int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(2, dateTime.length()-index_date)));
                    if (x < 1 || x > 59) {
                        throw new KazaoCalendarDateTimeException("minute "+x+" invalid, range is 1 to 59");
                    } else {
                        minute = x;
                    }
                } catch (NumberFormatException ex) {
                    throw new KazaoCalendarDateTimeException();
                }
                index_format += 2;
                index_date += 2;
                // minute i
            } else if (dateTimeFormat.substring(index_format, index_format+Math.min(1, dateTimeFormat.length()-index_format)).startsWith("i")) {
                if (dateTime.length() - index_date > dateTimeFormat.length() - index_format) {
                    try {
                        if (isNumber(dateTime.charAt(index_date+Math.min(1, dateTime.length()-index_date)))) {
                            int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(2, dateTime.length()-index_date)));
                            if (x < 1 || x > 59) {
                                try {
                                    x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                                    minute = x;
                                    index_date += 1;
                                } catch (NumberFormatException ex) {
                                    throw new KazaoCalendarDateTimeException("minute "+x+" invalid, range is 1 to 59");
                                }
                            } else {
                                minute = x;
                                index_date += 2;
                            }
                        } else {
                            int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                            if (x < 1 || x > 59) {
                                throw new KazaoCalendarDateTimeException("minute "+x+" invalid, range is 1 to 59");
                            } else {
                                minute = x;
                            }
                            index_date += 1;
                        }
                    } catch (NumberFormatException ex) {
                        throw new KazaoCalendarDateTimeException();
                    }
                } else {
                    try {
                        minute = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                    } catch (NumberFormatException ex) {
                        throw new KazaoCalendarDateTimeException();
                    }
                    index_date += 1;
                }
                index_format += 1;
                // second ss
            } else if (dateTimeFormat.substring(index_format, index_format+Math.min(2, dateTimeFormat.length()-index_format)).startsWith("ss")) {
                try {
                    int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(2, dateTime.length()-index_date)));
                    if (x < 1 || x > 59) {
                        throw new KazaoCalendarDateTimeException("second "+x+" invalid, range is 1 to 59");
                    } else {
                        second = x;
                    }
                } catch (NumberFormatException ex) {
                    throw new KazaoCalendarDateTimeException();
                }
                index_format += 2;
                index_date += 2;
                // second s
            } else if (dateTimeFormat.substring(index_format, index_format+Math.min(1, dateTimeFormat.length()-index_format)).startsWith("s")) {
                if (dateTime.length() - index_date > dateTimeFormat.length() - index_format) {
                    try {
                        if (isNumber(dateTime.charAt(index_date+Math.min(1, dateTime.length()-index_date)))) {
                            int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(2, dateTime.length()-index_date)));
                            if (x < 1 || x > 59) {
                                try {
                                    x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                                    second = x;
                                    index_date += 1;
                                } catch (NumberFormatException ex) {
                                    throw new KazaoCalendarDateTimeException("second "+x+" invalid, range is 1 to 59");
                                }                                                                
                            } else {
                                second = x;
                                index_date += 2;
                            }
                        } else {
                            int x = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                            if (x < 1 || x > 59) {
                                throw new KazaoCalendarDateTimeException("second "+x+" invalid, range is 1 to 59");
                            } else {
                                second = x;
                            }
                            index_date += 1;
                        }
                    } catch (NumberFormatException ex) {
                        throw new KazaoCalendarDateTimeException();
                    }
                } else {
                    try {
                        second = Integer.parseInt(dateTime.substring(index_date, index_date+Math.min(1, dateTime.length()-index_date)));
                    } catch (NumberFormatException ex) {
                        throw new KazaoCalendarDateTimeException();
                    }
                    index_date += 1;
                }
                index_format += 1;
            } else {
                index_date += 1;
                index_format += 1;
            }
            if (index_format >= dateTimeFormat.length()) {
                break;
            }
        }
        if (date != -1) {
            cal.set(cal.DATE, date);
        }
        if (month != -1) {
            cal.set(cal.MONTH, month-1);
        }
        if (year != -1) {
            cal.set(cal.YEAR, year);
        }
        if (hour != -1) {
            if (hour_24) {
                cal.set(cal.HOUR_OF_DAY, hour);
            } else {
                cal.set(cal.HOUR, hour);
            }
        }
        if (minute != -1) {
            cal.set(cal.MINUTE, minute);
        }
        if (second != -1) {
            cal.set(cal.SECOND, second);
        }
        calendar.setTimeInMillis(cal.getTimeInMillis());
        update();
    }
    
    public void setDateTime(String dateTime) throws KazaoCalendarDateTimeException {
        setDateTime(dateTime, localize.getDateTimeFormat());
    }
    
    public void setDate(Date date) {
        calendar.setTimeInMillis(date.getTime());
        update();
    }
    
    public void setDate(java.sql.Date date) {
        calendar.setTimeInMillis(date.getTime());
        update();
    }
    
    public Calendar getCalendar() {
        return calendar;
    }
    
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    public void setBackgroundMenuColor(Color menuBackground) {
        this.backgroundMenu = menuBackground;
    }
    
    public void setForegroundMenuColor(Color menuForeground) {
        this.foregroundMenu = menuForeground;
    }
    
    public void setLocalize(KazaoCalendarLocal localize) {
        this.localize = localize;
        initCalendar();
        update();
    }
    
    public KazaoCalendarLocal getLocalize() {
        return localize;
    }
    
    public void setNextButtonVisible(boolean flag) {
        pSelanjutnya.setVisible(flag);
    }
    
    public boolean isNextButtonVisible() {
        return pSelanjutnya.isVisible();
    }
    
    public void setPrevButtonVisible(boolean flag) {
        pSebelumnya.setVisible(flag);
    }
    
    public boolean isPrevButtonVisible() {
        return pSebelumnya.isVisible();
    }
    
    public void setInfoVisible(boolean flag) {
        pMenuBawah.setVisible(flag);
    }
    
    public boolean isInfoVisible() {
        return pMenuBawah.isVisible();
    }
    
    private void hideSelected(boolean flag) {
        if (flag) {
            cur_x = -1;
            cur_y = -1;
        }
    }
    
    @Deprecated
    public void setChangeListener(KazaoCalendarAdapter adapter) {
        this.adapter = adapter;
    }
    
    public void addChangeListener(KazaoCalendarAdapter adapter) {
        this.adapters.add(adapter);
    }
    
    public void update() {
        hideMonthEdit();
        hideYearEdit();
        updateCalendar();
        setCurrentDate();
        updateUI();
    }
    
    public String getShortDate() {
        return getDate(localize.getShortDateFormat());
    }
    
    public String getLongDate() {
        return getDate(localize.getLongDateFormat());
    }
    
    private void setFontCalendar() {
        fontDays = new Font(font.getName(), Font.BOLD, 10);
        fontMenu = new Font(font.getName(), Font.BOLD, 10);
        fontItem = new Font(font.getName(), Font.PLAIN, 10);
        fontOther = new Font(font.getName(), Font.PLAIN, 10);
        fontOver = new Font(font.getName(), Font.PLAIN, 10);
        fontSaturday = new Font(font.getName(), Font.PLAIN, 10);
        fontSelected = new Font(font.getName(), Font.PLAIN, 10);
        fontSunday = new Font(font.getName(), Font.PLAIN, 10);
        fontToday = new Font(font.getName(), Font.BOLD, 10);
        
        lBulan.setFont(fontMenu);
        lTahun.setFont(fontMenu);
        lTanggal.setFont(fontMenu);
        cbBulan.setFont(fontMenu);
        tTahun.setFont(fontMenu);
    }
    
    private void updateCalendar() {
        lBulan.setText(" "+localize.getLongMonths()[calendar.get(calendar.MONTH)]);
        lTahun.setText(""+calendar.get(calendar.YEAR));
        int x = localize.getStartDay();
        String [] hari = {"Minggu","Senin","Selasa","Rabu","Kamis","Jumat","Sabtu"};
        for (int i=0; i < 7; i++) {
            item[0][i].setFont(fontDays);
            item[0][i].setBackground(backgroundDays);
            item[0][i].setForeground(foregroundDays);
            item[0][i].setText(hari[i]);
            arr[i] = x+i > 6 ? x+i-7 : x+i;
        }
        cur_month = calendar.get(calendar.MONTH);
        cur_year = calendar.get(calendar.YEAR);
        Calendar cal = (Calendar) calendar.clone();
        int max = getMaxDay(cal);
        cal.add(cal.MONTH, -1);
        int max_before = getMaxDay(cal);
        cal = (Calendar) calendar.clone();
        cal.set(cal.DATE, 1);
        int start_day = cal.get(cal.DAY_OF_WEEK);
        int count_day_before = getMaxDay(arr, start_day-1)-1;
        int ia = 1;
        int ja = 0;
        for (int i=max_before-count_day_before; i <= max_before; i++) {
            item[ia][ja].setText(""+i);
            item[ia][ja].setName(item[ia][ja].getName().substring(0, 8)+"_b");
            setColor(ia, ja);
            ja++;
            if (ja == 7) {
                ja = 0;
                ia++;
            }
        }
        for (int i=1; i <= max; i++) {
            item[ia][ja].setText(""+i);
            item[ia][ja].setName(item[ia][ja].getName().substring(0, 8)+"_c");
            setColor(ia, ja);
            if(dayEvent.contains(i))
                item[ia][ja].setBackground(new Color(139,150,191));
            ja++;
            if (ja == 7) {
                ja = 0;
                ia++;
            }
        }
        int z = 1;
        while (true) {
            item[ia][ja].setText(""+z);
            item[ia][ja].setName(item[ia][ja].getName().substring(0, 8)+"_a");
            setColor(ia, ja);
            z++;
            ja++;
            if (ja == 7) {
                ja = 0;
                ia++;
            }
            if (ia == 7) {
                break;
            }
        }
        updateUI();
    }
    
    private void setCurrentDate() {
        // focuskan
        int max = getMaxDay(calendar);
        if (calendar.get(calendar.DATE) > max) {
            calendar.set(calendar.DATE, max);
        }
        for (int i=1; i < 7; i++) {
            for (int j=0; j < 7; j++) {
                if (item[i][j].getName().endsWith("c") && calendar.get(calendar.DATE) == Integer.parseInt(item[i][j].getText())) {
                    int old_x = cur_x;
                    int old_y = cur_y;
                    cur_x = i;
                    cur_y = j;
                    setColor(old_x, old_y);
                    setColor(cur_x, cur_y);
                    break;
                }
            }
        }
        showDate();
    }
    
    private int getMaxDay(int[] arr, int day) {
        int result = -1;
        for (int i=0; i < arr.length; i++) {
            if (arr[i] == day) {
                result =  i;
                break;
            }
        }
        return result;
    }
    
    public int getMaxDay(Calendar cal) {
        int year = cal.get(cal.YEAR);
        int month = cal.get(cal.MONTH)+1;
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            return 31;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else {
            if (year % 4 == 0) {
                return 29;
            } else {
                return 28;
            }
        }
    }
    
    public void showDate() {
        calendar.set(calendar.DATE, Integer.parseInt(item[cur_x][cur_y].getText()));
        
        if (adapter != null) {
            adapter.onChange(calendar);
        }
        for (int i=0; i < adapters.size(); i++) {
            adapters.elementAt(i).onChange(calendar);
            adapters.elementAt(i).onChange(this);
        }
        lTanggal.setText(" "+getDate(localize.getInfoFormat()));
    }
    
    public String getDate(String format) {
        String y = ""+(char) 1, m = ""+(char) 2, d = ""+(char) 3, h = ""+(char) 4, i = ""+(char) 5, s = ""+(char) 6;
        
        format = KazaoCalendarUtil.replace(format, "yyyyy", y+y+y+y+y);
        format = KazaoCalendarUtil.replace(format, "yyyy", y+y+y+y);
        format = KazaoCalendarUtil.replace(format, "yyy", y+y+y+y);
        format = KazaoCalendarUtil.replace(format, "yy", y+y);
        format = KazaoCalendarUtil.replace(format, "y", y+y);
        format = KazaoCalendarUtil.replace(format, "mmmm", m+m+m+m);
        format = KazaoCalendarUtil.replace(format, "mmm", m+m+m);
        format = KazaoCalendarUtil.replace(format, "mm", m+m);
        format = KazaoCalendarUtil.replace(format, "m", m);
        format = KazaoCalendarUtil.replace(format, "dddd", d+d+d+d);
        format = KazaoCalendarUtil.replace(format, "ddd", d+d+d);
        format = KazaoCalendarUtil.replace(format, "dd", d+d);
        format = KazaoCalendarUtil.replace(format, "d", d);
        format = KazaoCalendarUtil.replace(format, "hhh", h+h+h);
        format = KazaoCalendarUtil.replace(format, "hh", h+h);
        format = KazaoCalendarUtil.replace(format, "h", h);
        format = KazaoCalendarUtil.replace(format, "iii", i+i+i);
        format = KazaoCalendarUtil.replace(format, "ii", i+i);
        format = KazaoCalendarUtil.replace(format, "i", i);
        format = KazaoCalendarUtil.replace(format, "sss", s+s+s);
        format = KazaoCalendarUtil.replace(format, "ss", s+s);
        format = KazaoCalendarUtil.replace(format, "s", s);
        
        format = KazaoCalendarUtil.replace(format, y+y+y+y+y, ""+calendar.get(calendar.YEAR));
        format = KazaoCalendarUtil.replace(format, y+y+y+y, ""+calendar.get(calendar.YEAR));
        format = KazaoCalendarUtil.replace(format, y+y+y, ""+calendar.get(calendar.YEAR));
        format = KazaoCalendarUtil.replace(format, y+y, new String(""+calendar.get(calendar.YEAR)).substring(3));
        format = KazaoCalendarUtil.replace(format, y, new String(""+calendar.get(calendar.YEAR)).substring(3));
        format = KazaoCalendarUtil.replace(format, m+m+m+m, localize.getLongMonths()[calendar.get(calendar.MONTH)]);
        format = KazaoCalendarUtil.replace(format, m+m+m, localize.getShortMonths()[calendar.get(calendar.MONTH)]);
        format = KazaoCalendarUtil.replace(format, m+m, KazaoCalendarUtil.lpad(""+(calendar.get(calendar.MONTH)+1), 2, "0"));
        format = KazaoCalendarUtil.replace(format, m, ""+calendar.get(calendar.MONTH)+1);
        format = KazaoCalendarUtil.replace(format, d+d+d+d, localize.getLongDay()[calendar.get(calendar.DAY_OF_WEEK)-1]);
        format = KazaoCalendarUtil.replace(format, d+d+d, localize.getShortDay()[calendar.get(calendar.DAY_OF_WEEK)-1]);
        format = KazaoCalendarUtil.replace(format, d+d, KazaoCalendarUtil.lpad(""+calendar.get(calendar.DATE), 2, "0"));
        format = KazaoCalendarUtil.replace(format, d, ""+calendar.get(calendar.DATE));
        format = KazaoCalendarUtil.replace(format, h+h+h, KazaoCalendarUtil.lpad(""+calendar.get(calendar.HOUR_OF_DAY), 2, "0"));
        format = KazaoCalendarUtil.replace(format, h+h, KazaoCalendarUtil.lpad(""+calendar.get(calendar.HOUR_OF_DAY), 2, "0"));
        format = KazaoCalendarUtil.replace(format, h, ""+calendar.get(calendar.HOUR_OF_DAY));
        format = KazaoCalendarUtil.replace(format, i+i+i, KazaoCalendarUtil.lpad(""+calendar.get(calendar.MINUTE), 2, "0"));
        format = KazaoCalendarUtil.replace(format, i+i, KazaoCalendarUtil.lpad(""+calendar.get(calendar.MINUTE), 2, "0"));
        format = KazaoCalendarUtil.replace(format, i, ""+calendar.get(calendar.MINUTE));
        format = KazaoCalendarUtil.replace(format, s+s+s, KazaoCalendarUtil.lpad(""+calendar.get(calendar.SECOND), 2, "0"));
        format = KazaoCalendarUtil.replace(format, s+s, KazaoCalendarUtil.lpad(""+calendar.get(calendar.SECOND), 2, "0"));
        format = KazaoCalendarUtil.replace(format, s, ""+calendar.get(calendar.SECOND));
        return format;
    }
    
    private void setColor() {
        pMenuAtas.setBackground(backgroundMenu);
        pMenuBawah.setBackground(backgroundMenu);
        
        lBulan.setForeground(foregroundMenu);
        lTahun.setForeground(foregroundMenu);
        lTanggal.setForeground(foregroundMenu);
        
        setBackground(backgroundColor);
    }
    
    private void initCalendar() {
        loading = true;
        cbBulan.removeAllItems();
        for (int i=0; i < localize.getLongMonths().length; i++) {
            cbBulan.addItem(localize.getLongMonths()[i]);
        }
        loading = false;
        update();
    }
    
    private void showMonthEdit() {
        cbBulan.setSelectedIndex(calendar.get(calendar.MONTH));
        pBulan.remove(lBulan);
        pBulan.add(cbBulan, BorderLayout.CENTER);
        updateUI();
        cbBulan.requestFocus();
    }
    
    private void hideMonthEdit() {
        //if (cbBulan.isShowing()) {
        lBulan.setText(" "+localize.getLongMonths()[calendar.get(calendar.MONTH)]);
        pBulan.remove(cbBulan);
        pBulan.add(lBulan, BorderLayout.CENTER);
        updateUI();
        //}
    }
    
    private void setMonthValueFromEdit() {
        calendar.set(calendar.MONTH, cbBulan.getSelectedIndex());
        updateCalendar();
        setCurrentDate();
        showDate();
    }
    
    private void hideYearEdit() {
        //if (tTahun.isShowing()) {
        lTahun.setText(""+calendar.get(calendar.YEAR));
        lTahun.setVisible(true);
        seTahun.setVisible(false);
        tTahun.setVisible(false);
        //}
    }
    
    private void showYearEdit() {
        seTahun.getModel().setValue(calendar.get(calendar.YEAR));
        tTahun.setText(""+calendar.get(calendar.YEAR));
        lTahun.setVisible(false);
        seTahun.setVisible(true);
        tTahun.setVisible(true);
        tTahun.requestFocus();
    }
    
    private void setYearValueFromEdit() {
        if (!tTahun.getText().trim().equals("")) {
            if (Integer.parseInt(tTahun.getText()) >= localize.getMinYear() || Integer.parseInt(tTahun.getText()) <= localize.getMaxYear()) {
                seTahun.getModel().setValue(Integer.parseInt(tTahun.getText()));
                calendar.set(calendar.YEAR, Integer.parseInt(tTahun.getText()));
            } else {
                tTahun.setText(seTahun.getModel().getValue().toString());
            }
            updateCalendar();
            setCurrentDate();
            showDate();
        }
    }
    
    private void setColor(int x, int y) {
        if (arr[y] == 5) {
            item[x][y].setBackground(backgroundSaturday);
            item[x][y].setForeground(foregroundSaturday);
            item[x][y].setFont(fontSaturday);    
        } else if (arr[y] == 0) {
            item[x][y].setBackground(backgroundSunday);
            item[x][y].setForeground(foregroundSunday);
            item[x][y].setFont(fontSunday);
        } else {
            item[x][y].setBackground(backgroundItem);
            item[x][y].setForeground(foregroundItem);
            item[x][y].setFont(fontItem);
        }
        if (item[x][y].getName().endsWith("c")) {
            Calendar cal = Calendar.getInstance();
            if (cal.get(cal.DATE) == Integer.parseInt(item[x][y].getText()) &&
                    cal.get(cal.MONTH) == calendar.get(calendar.MONTH) &&
                    cal.get(cal.YEAR) == calendar.get(calendar.YEAR) ) {
                item[x][y].setBackground(backgroundToday);
                item[x][y].setForeground(foregroundToday);
                item[x][y].setFont(fontToday);
            }
        } else {
            item[x][y].setBackground(backgroundOther);
            item[x][y].setForeground(foregroundOther);
            item[x][y].setFont(fontOther);
        }
        
        if (cur_x == x && cur_y == y) {
            item[x][y].setBackground(backgroundSelected);
            item[x][y].setForeground(foregroundSelected);
            item[x][y].setFont(fontSelected);
            if (!cbBulan.isShowing() && !tTahun.isShowing()) {
                item[x][y].requestFocus();
            }
        }
        item[x][y].setBorder(javax.swing.BorderFactory.createEmptyBorder());
    }
    
    private void calendarOnMouseEntered(java.awt.event.MouseEvent evt) {
        JLabel l = (JLabel) evt.getSource();
        if (can(l)) {
            l.setBackground(backgroundOver);
            l.setForeground(foregroundOver);
        }
    }
    
    private void calendarOnMouseExited(java.awt.event.MouseEvent evt) {
        JLabel l = (JLabel) evt.getSource();
        if (can(l)) {
            int x = Integer.parseInt(l.getName().substring(5, 6));
            int y = Integer.parseInt(l.getName().substring(7, 8));
            setColor(x, y);
        }
    }
    
    private void calendarOnMouseClicked(java.awt.event.MouseEvent evt) {
        
        JLabel l = (JLabel) evt.getSource();
        if (can(l)) {
            if (evt.getClickCount() == 2) {
                if (adapter != null) {
                    adapter.onDoubleClick();
                }
                for (int i=0; i < adapters.size(); i++) {
                    adapters.elementAt(i).onDoubleClick();
                }
                return;
            }
            
            int x = Integer.parseInt(l.getName().substring(5, 6));
            int y = Integer.parseInt(l.getName().substring(7, 8));
            int old_x = cur_x;
            int old_y = cur_y;
            cur_x = x;
            cur_y = y;
            
            if (item[cur_x][cur_y].getName().endsWith("b")) {
                if (cur_month == 0) {
                    calendar.set(calendar.MONTH, 11);
                    calendar.set(calendar.YEAR, cur_year-1);
                } else {
                    calendar.set(calendar.MONTH, cur_month-1);
                }
            } else if (item[cur_x][cur_y].getName().endsWith("a")) {
                if (cur_month == 11) {
                    calendar.set(calendar.MONTH, 0);
                    calendar.set(calendar.YEAR, cur_year+1);
                } else {
                    calendar.set(calendar.MONTH, cur_month+1);
                }
            } else {
                calendar.set(calendar.MONTH, cur_month);
                calendar.set(calendar.YEAR, cur_year);
            }
            setColor(old_x, old_y);
            setColor(cur_x, cur_y);
            item[cur_x][cur_y].requestFocus();
            calendar.set(calendar.DATE, Integer.parseInt(item[cur_x][cur_y].getText()));
            showDate();
        }
    }
    
    private void calendarOnEnter(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == evt.VK_ENTER) {
            if (adapter != null) {
                adapter.onDoubleClick();
            }
            for (int i=0; i < adapters.size(); i++) {
                adapters.elementAt(i).onDoubleClick();
            }
        }
    }
    
    private void calendarHariMouseClicked(java.awt.event.MouseEvent evt) {
        hideYearEdit();
        hideMonthEdit();
    }
    
    private void calendarOnKeyPressed(java.awt.event.KeyEvent evt) {
        int old_x = cur_x;
        int old_y = cur_y;
        
        boolean is_can = false;
        if (evt.getKeyCode() == evt.VK_UP) {
            if (cur_x == 1 && cur_y == 0) {
                if (can('b', cur_month, cur_year)) {
                    if (item[cur_x][cur_y].getName().endsWith("c")) {
                        calendar.add(calendar.MONTH, -1);
                    }
                    updateCalendar();
                    cur_x = 6;
                    cur_y = 6;
                    if (item[cur_x][cur_y].getName().endsWith("b")) {
                        if (cur_month == 0) {
                            calendar.set(calendar.MONTH, 11);
                            calendar.set(calendar.YEAR, cur_year-1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month-1);
                        }
                    } else if (item[cur_x][cur_y].getName().endsWith("a")) {
                        if (cur_month == 11) {
                            calendar.set(calendar.MONTH, 0);
                            calendar.set(calendar.YEAR, cur_year+1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month+1);
                        }
                    } else {
                        calendar.set(calendar.MONTH, cur_month);
                        calendar.set(calendar.YEAR, cur_year);
                    }
                    setColor(old_x, old_y);
                    setColor(cur_x, cur_y);
                    is_can = true;
                }
            } else {
                if (can(cur_x > 1 ? item[cur_x-1][cur_y] : item[6][cur_y-1])) {
                    if (cur_x > 1) {
                        cur_x--;
                    } else {
                        cur_x = 6;
                        cur_y--;
                    }
                    if (item[cur_x][cur_y].getName().endsWith("b")) {
                        if (cur_month == 0) {
                            calendar.set(calendar.MONTH, 11);
                            calendar.set(calendar.YEAR, cur_year-1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month-1);
                        }
                    } else if (item[cur_x][cur_y].getName().endsWith("a")) {
                        if (cur_month == 11) {
                            calendar.set(calendar.MONTH, 0);
                            calendar.set(calendar.YEAR, cur_year+1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month+1);
                        }
                    } else {
                        calendar.set(calendar.MONTH, cur_month);
                        calendar.set(calendar.YEAR, cur_year);
                    }
                    setColor(old_x, old_y);
                    setColor(cur_x, cur_y);
                    is_can = true;
                }
            }
            calendar.set(calendar.DATE, Integer.parseInt(item[cur_x][cur_y].getText()));
            showDate();
        }
        if (evt.getKeyCode() == evt.VK_DOWN) {
            if (cur_x == 6 && cur_y == 6) {
                if (can('a', cur_month, cur_year)) {
                    if (item[cur_x][cur_y].getName().endsWith("c")) {
                        calendar.add(calendar.MONTH, 1);
                    }
                    updateCalendar();
                    cur_x = 1;
                    cur_y = 0;
                    if (item[cur_x][cur_y].getName().endsWith("b")) {
                        if (cur_month == 0) {
                            calendar.set(calendar.MONTH, 11);
                            calendar.set(calendar.YEAR, cur_year-1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month-1);
                        }
                    } else if (item[cur_x][cur_y].getName().endsWith("a")) {
                        if (cur_month == 11) {
                            calendar.set(calendar.MONTH, 0);
                            calendar.set(calendar.YEAR, cur_year+1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month+1);
                        }
                    } else {
                        calendar.set(calendar.MONTH, cur_month);
                        calendar.set(calendar.YEAR, cur_year);
                    }
                    setColor(old_x, old_y);
                    setColor(cur_x, cur_y);
                    is_can = true;
                }
            } else {
                if (can(cur_x < 6 ? item[cur_x+1][cur_y] : item[1][cur_y+1])) {
                    if (cur_x < 6) {
                        cur_x++;
                    } else {
                        cur_x = 1;
                        cur_y++;
                    }
                    if (item[cur_x][cur_y].getName().endsWith("b")) {
                        if (cur_month == 0) {
                            calendar.set(calendar.MONTH, 11);
                            calendar.set(calendar.YEAR, cur_year-1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month-1);
                        }
                    } else if (item[cur_x][cur_y].getName().endsWith("a")) {
                        if (cur_month == 11) {
                            calendar.set(calendar.MONTH, 0);
                            calendar.set(calendar.YEAR, cur_year+1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month+1);
                        }
                    } else {
                        calendar.set(calendar.MONTH, cur_month);
                        calendar.set(calendar.YEAR, cur_year);
                    }
                    setColor(old_x, old_y);
                    setColor(cur_x, cur_y);
                    is_can = true;
                }
            }
            calendar.set(calendar.DATE, Integer.parseInt(item[cur_x][cur_y].getText()));
            showDate();
        }
        if (evt.getKeyCode() == evt.VK_LEFT) {
            if (cur_x == 1 && cur_y == 0) {
                if (can('b', cur_month, cur_year)) {
                    if (item[cur_x][cur_y].getName().endsWith("c")) {
                        calendar.add(calendar.MONTH, -1);
                    }
                    updateCalendar();
                    cur_x = 6;
                    cur_y = 6;
                    if (item[cur_x][cur_y].getName().endsWith("b")) {
                        if (cur_month == 0) {
                            calendar.set(calendar.MONTH, 11);
                            calendar.set(calendar.YEAR, cur_year-1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month-1);
                        }
                    } else if (item[cur_x][cur_y].getName().endsWith("a")) {
                        if (cur_month == 11) {
                            calendar.set(calendar.MONTH, 0);
                            calendar.set(calendar.YEAR, cur_year+1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month+1);
                        }
                    } else {
                        calendar.set(calendar.MONTH, cur_month);
                        calendar.set(calendar.YEAR, cur_year);
                    }
                    setColor(old_x, old_y);
                    setColor(cur_x, cur_y);
                    is_can = true;
                }
            } else {
                if (can(cur_y > 0 ? item[cur_x][cur_y-1] : item[cur_x-1][6])) {
                    if (cur_y > 0) {
                        cur_y--;
                    } else {
                        cur_y = 6;
                        cur_x--;
                    }
                    if (item[cur_x][cur_y].getName().endsWith("b")) {
                        if (cur_month == 0) {
                            calendar.set(calendar.MONTH, 11);
                            calendar.set(calendar.YEAR, cur_year-1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month-1);
                        }
                    } else if (item[cur_x][cur_y].getName().endsWith("a")) {
                        if (cur_month == 11) {
                            calendar.set(calendar.MONTH, 0);
                            calendar.set(calendar.YEAR, cur_year+1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month+1);
                        }
                    } else {
                        calendar.set(calendar.MONTH, cur_month);
                        calendar.set(calendar.YEAR, cur_year);
                    }
                    setColor(old_x, old_y);
                    setColor(cur_x, cur_y);
                    is_can = true;
                }
            }
            calendar.set(calendar.DATE, Integer.parseInt(item[cur_x][cur_y].getText()));
            showDate();
        }
        if (evt.getKeyCode() == evt.VK_RIGHT) {
            if (cur_x == 6 && cur_y == 6) {
                if (can('a', cur_month, cur_year)) {
                    if (item[cur_x][cur_y].getName().endsWith("c")) {
                        calendar.add(calendar.MONTH, 1);
                    }
                    updateCalendar();
                    cur_x = 1;
                    cur_y = 0;
                    if (item[cur_x][cur_y].getName().endsWith("b")) {
                        if (cur_month == 0) {
                            calendar.set(calendar.MONTH, 11);
                            calendar.set(calendar.YEAR, cur_year-1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month-1);
                        }
                    } else if (item[cur_x][cur_y].getName().endsWith("a")) {
                        if (cur_month == 11) {
                            calendar.set(calendar.MONTH, 0);
                            calendar.set(calendar.YEAR, cur_year+1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month+1);
                        }
                    } else {
                        calendar.set(calendar.MONTH, cur_month);
                        calendar.set(calendar.YEAR, cur_year);
                    }
                    setColor(old_x, old_y);
                    setColor(cur_x, cur_y);
                    is_can = true;
                }
            } else {
                if (can(cur_y < 6 ? item[cur_x][cur_y+1] : item[cur_x+1][0])) {
                    if (cur_y < 6) {
                        cur_y++;
                    } else {
                        cur_y = 0;
                        cur_x++;
                    }
                    if (item[cur_x][cur_y].getName().endsWith("b")) {
                        if (cur_month == 0) {
                            calendar.set(calendar.MONTH, 11);
                            calendar.set(calendar.YEAR, cur_year-1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month-1);
                        }
                    } else if (item[cur_x][cur_y].getName().endsWith("a")) {
                        if (cur_month == 11) {
                            calendar.set(calendar.MONTH, 0);
                            calendar.set(calendar.YEAR, cur_year+1);
                        } else {
                            calendar.set(calendar.MONTH, cur_month+1);
                        }
                    } else {
                        calendar.set(calendar.MONTH, cur_month);
                        calendar.set(calendar.YEAR, cur_year);
                    }
                    setColor(old_x, old_y);
                    setColor(cur_x, cur_y);
                    is_can = true;
                }
            }
            if (is_can) {
                calendar.set(calendar.DATE, Integer.parseInt(item[cur_x][cur_y].getText()));
                showDate();
            }
        }
    }
    
    private boolean isNumber(char x) {
        char[] a = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        boolean result = false;
        for (int i=0; i < a.length; i++) {
            if (a[i] == x) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    private boolean can(JLabel l) {
        boolean result = true;
        if (cur_month == 0 && cur_year == getLocalize().getMinYear() && l.getName().endsWith("b")) {
            result = false;
        }
        if (cur_month == 11 && cur_year == getLocalize().getMaxYear() && l.getName().endsWith("a")) {
            result = false;
        }
        return result;
    }
    
    private boolean can(char tipe, int month, int year) {
        boolean result = true;
        if (month == 0 && year == getLocalize().getMinYear() && tipe == 'b') {
            result = false;
        }
        if (month == 11 && year == getLocalize().getMaxYear() && tipe == 'a') {
            result = false;
        }
        return result;
    }
    
    private void prev() {
        hideYearEdit();
        hideMonthEdit();
        if (calendar.get(calendar.MONTH) == 0) {
            if (calendar.get(calendar.YEAR) > localize.getMinYear()) {
                calendar.set(calendar.MONTH, 11);
                calendar.add(calendar.YEAR, -1);
            }
        } else {
            calendar.add(calendar.MONTH, -1);
        }
        updateCalendar();
        setCurrentDate();
        showDate();
    }
    
    private void next() {
        hideYearEdit();
        hideMonthEdit();
        if (calendar.get(calendar.MONTH) == 11) {
            if (calendar.get(calendar.YEAR) < localize.getMaxYear()) {
                calendar.set(calendar.MONTH, 0);
                calendar.add(calendar.YEAR, 1);
            }
        } else {
            calendar.add(calendar.MONTH, 1);
        }
        updateCalendar();
        setCurrentDate();
        showDate();
    }
    
    public void today() {
        calendar = Calendar.getInstance();
        updateCalendar();
        setCurrentDate();
    }
    
    private void init() {
        initComponents();
        
        imToday = new ImageIcon(getClass().getResource("/images/today.png"));
        imNext = new ImageIcon(getClass().getResource("/images/next.png"));
        imPrev = new ImageIcon(getClass().getResource("/images/prev.png"));
        
        seTahun.setVisible(false);
        tTahun.setVisible(false);
        cSelanjutnya.setIcon(imNext);
        cSebelumnya.setIcon(imPrev);
        cHariIni.setIcon(imToday);
        
        seTahun.getModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (Integer.parseInt(seTahun.getModel().getValue().toString()) < localize.getMinYear()) {
                    seTahun.getModel().setValue(localize.getMinYear());
                    tTahun.setText(""+localize.getMinYear());
                } else if (Integer.parseInt(seTahun.getModel().getValue().toString()) > localize.getMaxYear()) {
                    seTahun.getModel().setValue(localize.getMaxYear());
                    tTahun.setText(""+localize.getMaxYear());
                } else {
                    tTahun.setText(seTahun.getModel().getValue().toString());
                    calendar.set(calendar.YEAR, Integer.parseInt(seTahun.getModel().getValue().toString()));
                }
                updateCalendar();
                setCurrentDate();
                showDate();
            }
        });
        backgroundDays = new Color(244, 254, 194);
        backgroundItem = new Color(238, 238, 238);
        backgroundMenu = new java.awt.Color(0, 102, 204);
        backgroundOther = new Color(250, 250, 250);
        backgroundOver = new Color(255, 255, 0);
        backgroundSaturday = new Color(185, 252, 185);
        backgroundSelected = new Color(255, 0, 0);
        backgroundSunday = new Color(250, 200, 200);
        backgroundToday = new Color(0, 255, 0);
        
        foregroundDays = new Color(0, 0, 0);
        foregroundItem = new Color(0, 0, 0);
        foregroundMenu = new java.awt.Color(255, 255, 255);
        foregroundOther = new Color(230, 230, 230);
        foregroundOver = new Color(0, 0, 0);
        foregroundSaturday = new Color(0, 0, 0);
        foregroundSelected = new Color(255, 255, 0);
        foregroundSunday = new Color(0, 0, 0);
        foregroundToday = new Color(0, 0, 255);
        
        setColor();
        for (int i=0; i < 7; i++) {
            for (int j=0; j < 7; j++) {
                item[i][j] = new JLabel();
                item[i][j].setName("item_"+i+"_"+j);
                item[i][j].setHorizontalAlignment(JLabel.CENTER);
                item[i][j].setOpaque(true);
                item[i][j].setFocusable(true);
                if (i > 0) {
                    item[i][j].addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent evt) {
                            calendarOnMouseEntered(evt);
                        }
                        public void mouseExited(java.awt.event.MouseEvent evt) {
                            calendarOnMouseExited(evt);
                        }
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            calendarOnMouseClicked(evt);
                        }
                    });
                    item[i][j].addKeyListener(new java.awt.event.KeyAdapter() {
                        public void keyPressed(java.awt.event.KeyEvent evt) {
                            calendarOnKeyPressed(evt);
                        }
                    });
                    item[i][j].addKeyListener(new java.awt.event.KeyAdapter() {
                        public void keyPressed(java.awt.event.KeyEvent evt) {
                            calendarOnEnter(evt);
                        }
                    });
                    
                } else {
                    item[i][j].addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            calendarHariMouseClicked(evt);
                        }
                    });
                }
                pKalender.add(item[i][j]);
            }
        }
        TimerTask tt = new TimerTask() {
            public void run() {
                if (isShowing()) {
                    if (doPrev || doNext) {
                        onTimer++;
                        if (onTimer > 5) {
                            if (doPrev) {
                                prev();
                            }
                            if (doNext) {
                                next();
                            }
                        }
                    }
                }
            }
        };
        timer.schedule(tt, 0, 100);
        setFontCalendar();
        initCalendar();
        update();
        today();
    }
    
    public boolean isMax() {
        return isMax('d');
    }
    
    public boolean isMax(char tipe) {
        boolean result = false;
        if (tipe == 's') {
            if (calendar.get(calendar.SECOND) == 23 && calendar.get(calendar.MINUTE) == 59 && calendar.get(calendar.HOUR_OF_DAY) == 59 && calendar.get(calendar.DATE) == 31 && calendar.get(calendar.MONTH) == 11 && calendar.get(calendar.YEAR) == getLocalize().getMaxYear()) {
                result = true;
            }
        } else if (tipe == 'i') {
            if (calendar.get(calendar.MINUTE) == 59 && calendar.get(calendar.HOUR_OF_DAY) == 59 && calendar.get(calendar.DATE) == 31 && calendar.get(calendar.MONTH) == 11 && calendar.get(calendar.YEAR) == getLocalize().getMaxYear()) {
                result = true;
            }
        } else if (tipe == 'h') {
            if (calendar.get(calendar.HOUR_OF_DAY) == 59 && calendar.get(calendar.DATE) == 31 && calendar.get(calendar.MONTH) == 11 && calendar.get(calendar.YEAR) == getLocalize().getMaxYear()) {
                result = true;
            }
        } else if (tipe == 'd') {
            if (calendar.get(calendar.DATE) == 31 && calendar.get(calendar.MONTH) == 11 && calendar.get(calendar.YEAR) == getLocalize().getMaxYear()) {
                result = true;
            }
        } else {
            if (calendar.get(calendar.MONTH) == 11 && calendar.get(calendar.YEAR) == getLocalize().getMaxYear()) {
                result = true;
            }
        }
        return result;
    }
    
    public boolean isMin() {
        return isMin('d');
    }
    
    public boolean isMin(char tipe) {
        boolean result = false;
        if (tipe == 's') {
            if (calendar.get(calendar.SECOND) == 0 && calendar.get(calendar.MINUTE) == 0 && calendar.get(calendar.HOUR_OF_DAY) == 0 && calendar.get(calendar.DATE) == 1 && calendar.get(calendar.MONTH) == 0 && calendar.get(calendar.YEAR) == getLocalize().getMinYear()) {
                result = true;
            }
        } else if (tipe == 'i') {
            if (calendar.get(calendar.MINUTE) == 0 && calendar.get(calendar.HOUR_OF_DAY) == 0 && calendar.get(calendar.DATE) == 1 && calendar.get(calendar.MONTH) == 0 && calendar.get(calendar.YEAR) == getLocalize().getMinYear()) {
                result = true;
            }
        } else if (tipe == 'h') {
            if (calendar.get(calendar.HOUR_OF_DAY) == 0 && calendar.get(calendar.DATE) == 1 && calendar.get(calendar.MONTH) == 0 && calendar.get(calendar.YEAR) == getLocalize().getMinYear()) {
                result = true;
            }
        } else if (tipe == 'd') {
            if (calendar.get(calendar.DATE) == 1 && calendar.get(calendar.MONTH) == 0 && calendar.get(calendar.YEAR) == getLocalize().getMinYear()) {
                result = true;
            }
        } else {
            if (calendar.get(calendar.MONTH) == 0 && calendar.get(calendar.YEAR) == getLocalize().getMinYear()) {
                result = true;
            }
        }
        return result;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cbBulan = new javax.swing.JComboBox();
        lBulan = new javax.swing.JLabel();
        pMenuAtas = new javax.swing.JPanel();
        pSebelumnya = new javax.swing.JPanel();
        cSebelumnya = new javax.swing.JButton();
        pMenuAtasTengah = new javax.swing.JPanel();
        pBulan = new javax.swing.JPanel();
        spBulanAtas = new javax.swing.JLabel();
        spBulanBawah = new javax.swing.JLabel();
        pTahun = new javax.swing.JPanel();
        seTahun = new javax.swing.JSpinner();
        tTahun = new javax.swing.JTextField();
        lTahun = new javax.swing.JLabel();
        pSelanjutnya = new javax.swing.JPanel();
        cSelanjutnya = new javax.swing.JButton();
        pKalender = new javax.swing.JPanel();
        pMenuBawah = new javax.swing.JPanel();
        pHariIni = new javax.swing.JPanel();
        cHariIni = new javax.swing.JButton();
        pTanggal = new javax.swing.JPanel();
        lTanggal = new javax.swing.JLabel();
        spTanggalAtas = new javax.swing.JLabel();
        spTanggalBawah = new javax.swing.JLabel();

        cbBulan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbBulan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBulanActionPerformed(evt);
            }
        });

        lBulan.setForeground(new java.awt.Color(255, 255, 255));
        lBulan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lBulanMouseClicked(evt);
            }
        });

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setFocusable(false);
        setMinimumSize(new java.awt.Dimension(178, 156));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        pMenuAtas.setBackground(new java.awt.Color(0, 102, 204));
        pMenuAtas.setFocusable(false);
        pMenuAtas.setPreferredSize(new java.awt.Dimension(100, 24));
        pMenuAtas.setLayout(new java.awt.BorderLayout());

        pSebelumnya.setFocusable(false);
        pSebelumnya.setOpaque(false);
        pSebelumnya.setPreferredSize(new java.awt.Dimension(24, 100));
        pSebelumnya.setLayout(null);

        cSebelumnya.setMnemonic('p');
        cSebelumnya.setToolTipText("<html><b>Previuos month</b>&nbsp;&nbsp;&nbsp;(<i><font color=red>alt+p</font></i>)</html>");
        cSebelumnya.setBorder(null);
        cSebelumnya.setContentAreaFilled(false);
        cSebelumnya.setFocusPainted(false);
        cSebelumnya.setFocusable(false);
        cSebelumnya.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                cSebelumnyaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cSebelumnyaMouseReleased(evt);
            }
        });
        cSebelumnya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cSebelumnyaActionPerformed(evt);
            }
        });
        pSebelumnya.add(cSebelumnya);
        cSebelumnya.setBounds(2, 2, 20, 20);

        pMenuAtas.add(pSebelumnya, java.awt.BorderLayout.WEST);

        pMenuAtasTengah.setFocusable(false);
        pMenuAtasTengah.setOpaque(false);
        pMenuAtasTengah.setLayout(new java.awt.BorderLayout());

        pBulan.setBackground(new java.awt.Color(255, 255, 255));
        pBulan.setFocusable(false);
        pBulan.setOpaque(false);
        pBulan.setLayout(new java.awt.BorderLayout());

        spBulanAtas.setFocusable(false);
        spBulanAtas.setPreferredSize(new java.awt.Dimension(0, 2));
        pBulan.add(spBulanAtas, java.awt.BorderLayout.NORTH);

        spBulanBawah.setFocusable(false);
        spBulanBawah.setPreferredSize(new java.awt.Dimension(0, 2));
        pBulan.add(spBulanBawah, java.awt.BorderLayout.SOUTH);

        pMenuAtasTengah.add(pBulan, java.awt.BorderLayout.CENTER);

        pTahun.setBackground(new java.awt.Color(255, 255, 255));
        pTahun.setFocusable(false);
        pTahun.setOpaque(false);
        pTahun.setPreferredSize(new java.awt.Dimension(54, 100));
        pTahun.setLayout(null);
        pTahun.add(seTahun);
        seTahun.setBounds(36, 2, 16, 20);

        tTahun.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        tTahun.setText("2007");
        tTahun.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tTahunFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tTahunFocusLost(evt);
            }
        });
        tTahun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tTahunKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tTahunKeyTyped(evt);
            }
        });
        pTahun.add(tTahun);
        tTahun.setBounds(2, 2, 34, 20);

        lTahun.setBackground(new java.awt.Color(255, 255, 255));
        lTahun.setForeground(new java.awt.Color(255, 255, 255));
        lTahun.setText("2007");
        lTahun.setFocusable(false);
        lTahun.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lTahunMouseClicked(evt);
            }
        });
        pTahun.add(lTahun);
        lTahun.setBounds(4, 2, 48, 20);

        pMenuAtasTengah.add(pTahun, java.awt.BorderLayout.EAST);

        pMenuAtas.add(pMenuAtasTengah, java.awt.BorderLayout.CENTER);

        pSelanjutnya.setFocusable(false);
        pSelanjutnya.setOpaque(false);
        pSelanjutnya.setPreferredSize(new java.awt.Dimension(24, 100));
        pSelanjutnya.setLayout(null);

        cSelanjutnya.setMnemonic('n');
        cSelanjutnya.setToolTipText("<html><b>Next month</b>&nbsp;&nbsp;&nbsp;(<i><font color=red>alt+n</font></i>)</html>");
        cSelanjutnya.setBorder(null);
        cSelanjutnya.setContentAreaFilled(false);
        cSelanjutnya.setFocusPainted(false);
        cSelanjutnya.setFocusable(false);
        cSelanjutnya.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                cSelanjutnyaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cSelanjutnyaMouseReleased(evt);
            }
        });
        cSelanjutnya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cSelanjutnyaActionPerformed(evt);
            }
        });
        pSelanjutnya.add(cSelanjutnya);
        cSelanjutnya.setBounds(2, 2, 20, 20);

        pMenuAtas.add(pSelanjutnya, java.awt.BorderLayout.EAST);

        add(pMenuAtas, java.awt.BorderLayout.NORTH);

        pKalender.setFocusable(false);
        pKalender.setOpaque(false);
        pKalender.setLayout(new java.awt.GridLayout(7, 7, 1, 1));
        add(pKalender, java.awt.BorderLayout.CENTER);

        pMenuBawah.setBackground(new java.awt.Color(0, 102, 204));
        pMenuBawah.setFocusable(false);
        pMenuBawah.setPreferredSize(new java.awt.Dimension(100, 24));
        pMenuBawah.setLayout(new java.awt.BorderLayout());

        pHariIni.setFocusable(false);
        pHariIni.setOpaque(false);
        pHariIni.setPreferredSize(new java.awt.Dimension(24, 100));
        pHariIni.setLayout(null);

        cHariIni.setMnemonic('t');
        cHariIni.setToolTipText("<html><b>Today</b>&nbsp;&nbsp;&nbsp;(<i><font color=red>alt+t</font></i>)</html>");
        cHariIni.setBorder(null);
        cHariIni.setContentAreaFilled(false);
        cHariIni.setFocusPainted(false);
        cHariIni.setFocusable(false);
        cHariIni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cHariIniActionPerformed(evt);
            }
        });
        pHariIni.add(cHariIni);
        cHariIni.setBounds(2, 2, 20, 20);

        pMenuBawah.add(pHariIni, java.awt.BorderLayout.WEST);

        pTanggal.setFocusable(false);
        pTanggal.setOpaque(false);
        pTanggal.setLayout(new java.awt.BorderLayout());

        lTanggal.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        lTanggal.setFocusable(false);
        pTanggal.add(lTanggal, java.awt.BorderLayout.CENTER);

        spTanggalAtas.setFocusable(false);
        spTanggalAtas.setPreferredSize(new java.awt.Dimension(0, 2));
        pTanggal.add(spTanggalAtas, java.awt.BorderLayout.NORTH);

        spTanggalBawah.setFocusable(false);
        spTanggalBawah.setPreferredSize(new java.awt.Dimension(0, 2));
        pTanggal.add(spTanggalBawah, java.awt.BorderLayout.SOUTH);

        pMenuBawah.add(pTanggal, java.awt.BorderLayout.CENTER);

        add(pMenuBawah, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
    
    private void cSelanjutnyaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cSelanjutnyaMouseReleased
        doNext = false;
    }//GEN-LAST:event_cSelanjutnyaMouseReleased
    
    private void cSelanjutnyaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cSelanjutnyaMousePressed
        doNext = true;
        onTimer = 0;
    }//GEN-LAST:event_cSelanjutnyaMousePressed
    
    private void cSebelumnyaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cSebelumnyaMouseReleased
        doPrev = false;
    }//GEN-LAST:event_cSebelumnyaMouseReleased
    
    private void cSebelumnyaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cSebelumnyaMousePressed
        doPrev = true;
        onTimer = 0;
    }//GEN-LAST:event_cSebelumnyaMousePressed
    
    private void cbBulanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBulanActionPerformed
        if (!loading) {
            setMonthValueFromEdit();
        }
    }//GEN-LAST:event_cbBulanActionPerformed
    
    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        update();
    }//GEN-LAST:event_formComponentResized
    
    private void cHariIniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cHariIniActionPerformed
        hideMonthEdit();
        hideYearEdit();
        today();
    }//GEN-LAST:event_cHariIniActionPerformed
    
    private void lBulanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lBulanMouseClicked
        showMonthEdit();
        hideYearEdit();
    }//GEN-LAST:event_lBulanMouseClicked
    
    private void tTahunKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tTahunKeyPressed
        if (evt.getKeyCode() == evt.VK_ENTER) {
            setYearValueFromEdit();
        }
        if (evt.getKeyCode() == evt.VK_UP) {
            if (Integer.parseInt(tTahun.getText()) < localize.getMaxYear()) {
                tTahun.setText(""+(Integer.parseInt(tTahun.getText())+1));
                setYearValueFromEdit();
            }
        }
        if (evt.getKeyCode() == evt.VK_DOWN) {
            if (Integer.parseInt(tTahun.getText()) > localize.getMinYear()) {
                tTahun.setText(""+(Integer.parseInt(tTahun.getText())-1));
                setYearValueFromEdit();
            }
        }
        if (evt.getKeyCode() == evt.VK_PAGE_UP) {
            tTahun.setText(""+localize.getMaxYear());
            setYearValueFromEdit();
        }
        if (evt.getKeyCode() == evt.VK_PAGE_DOWN) {
            tTahun.setText(""+localize.getMinYear());
            setYearValueFromEdit();
        }
    }//GEN-LAST:event_tTahunKeyPressed
    
    private void tTahunKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tTahunKeyTyped
        char[] a = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        if (!KazaoCalendarUtil.inArray(a, evt.getKeyChar()) || tTahun.getText().length() == 4) {
            evt.setKeyChar((char) 0);
        }
    }//GEN-LAST:event_tTahunKeyTyped
    
    private void tTahunFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tTahunFocusLost
        setYearValueFromEdit();
    }//GEN-LAST:event_tTahunFocusLost
    
    private void cSebelumnyaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cSebelumnyaActionPerformed
        prev();
    }//GEN-LAST:event_cSebelumnyaActionPerformed
    
    private void cSelanjutnyaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cSelanjutnyaActionPerformed
        next();
    }//GEN-LAST:event_cSelanjutnyaActionPerformed
    
    private void tTahunFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tTahunFocusGained
        setYearValueFromEdit();
    }//GEN-LAST:event_tTahunFocusGained
    
    private void lTahunMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lTahunMouseClicked
        showYearEdit();
        hideMonthEdit();
    }//GEN-LAST:event_lTahunMouseClicked
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cHariIni;
    private javax.swing.JButton cSebelumnya;
    private javax.swing.JButton cSelanjutnya;
    private javax.swing.JComboBox cbBulan;
    private javax.swing.JLabel lBulan;
    private javax.swing.JLabel lTahun;
    private javax.swing.JLabel lTanggal;
    private javax.swing.JPanel pBulan;
    private javax.swing.JPanel pHariIni;
    private javax.swing.JPanel pKalender;
    private javax.swing.JPanel pMenuAtas;
    private javax.swing.JPanel pMenuAtasTengah;
    private javax.swing.JPanel pMenuBawah;
    private javax.swing.JPanel pSebelumnya;
    private javax.swing.JPanel pSelanjutnya;
    private javax.swing.JPanel pTahun;
    private javax.swing.JPanel pTanggal;
    private javax.swing.JSpinner seTahun;
    private javax.swing.JLabel spBulanAtas;
    private javax.swing.JLabel spBulanBawah;
    private javax.swing.JLabel spTanggalAtas;
    private javax.swing.JLabel spTanggalBawah;
    private javax.swing.JTextField tTahun;
    // End of variables declaration//GEN-END:variables
    
}
