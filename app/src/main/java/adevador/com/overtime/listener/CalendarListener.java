package adevador.com.overtime.listener;

import java.util.Date;
import java.util.Set;

/**
 * Created by druweje on 21/10/2014.
 */
public interface CalendarListener {
    public void dateSelected(Date date);
    public void datesSelected(Set<Date> dates);
}
