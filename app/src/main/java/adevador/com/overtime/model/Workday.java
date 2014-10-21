package adevador.com.overtime.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by druweje on 21/10/2014.
 */
public class Workday extends RealmObject {

    private Date date;
    private int hours;
    private int minutes;

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
