package adevador.com.overtime.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by druweje on 21/10/2014.
 */
public class Workday extends RealmObject {

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
