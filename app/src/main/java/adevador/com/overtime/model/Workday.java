package adevador.com.overtime.model;

import java.io.Serializable;
import java.util.Date;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.AutoIncrement;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

@Table("Workdays")
public class Workday extends Model implements Serializable{

    @Key
    @AutoIncrement
    @Column("id")
    private long id;

    @Column("date")
    private Date date;

    @Column("hours")
    private int hours;

    @Column("minutes")
    private int minutes;

    @Column("googleCalendarEventId")
    private long googleCalendarEventId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public long getGoogleCalendarEventId() {
        return googleCalendarEventId;
    }

    public void setGoogleCalendarEventId(long googleCalendarEventId) {
        this.googleCalendarEventId = googleCalendarEventId;
    }
}
