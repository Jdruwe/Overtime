package adevador.com.overtime.listener;

import java.util.ArrayList;
import java.util.Date;

import adevador.com.overtime.model.Workday;

/**
 * Created by druweje on 21/10/2014.
 */
public interface TimeListener {
    public void startSelected(int hour, int minute);
    public void endSelected(int hour, int minute);
}
