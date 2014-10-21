package adevador.com.overtime.listener;

import java.util.Date;
import java.util.List;

/**
 * Created by druweje on 21/10/2014.
 */
public interface CalendarListener {
    public void dateSelected(Date date);
    public void datesSelected(List<Date> dates);
}
