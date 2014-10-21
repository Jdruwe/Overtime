package adevador.com.overtime.listener;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by druweje on 21/10/2014.
 */
public interface TimeListener {
    public void timeWorked(ArrayList<Date> dates, int hours, int minutes);
}
