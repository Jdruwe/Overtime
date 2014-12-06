package adevador.com.overtime.listener;

import java.util.ArrayList;
import java.util.Date;

import adevador.com.overtime.model.Workday;

/**
 * Created by druweje on 21/10/2014.
 */
public interface TimeListener {
    public void timeWorked(ArrayList<Date> dates, int hours, int minutes);
    public void deleteWorkday(Workday workday);
}
