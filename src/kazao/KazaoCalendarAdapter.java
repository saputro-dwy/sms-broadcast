package kazao;

import java.util.Calendar;

public abstract class KazaoCalendarAdapter {
    public abstract void onChange(Calendar calendar);
    public abstract void onChange(KazaoCalendar calendar);
    public abstract void onDoubleClick();
}
