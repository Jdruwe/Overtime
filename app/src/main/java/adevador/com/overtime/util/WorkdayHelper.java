package adevador.com.overtime.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import adevador.com.overtime.model.Workday;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.Query;
import se.emilsjolander.sprinkles.Transaction;

public class WorkdayHelper {

    public static void save(ArrayList<Date> dates, int hours, int minutes) {

        Transaction t = new Transaction();
        try {
            for (Date date : dates) {
                Workday workday = new Workday();
                workday.setDate(date);
                workday.setHours(hours);
                workday.setMinutes(minutes);
                if (!workday.save(t)) {
                    return;
                }
            }
            t.setSuccessful(true);
        } finally {
            t.finish();
        }
    }

    public static Workday get(long id) {
        return Query.one(Workday.class, "SELECT * FROM workdays WHERE id = ?", id).get();
    }

    public static CursorList<Workday> getAll() {
        return Query.all(Workday.class).get();
    }

    public static CursorList<Workday> getAll(int year, int month) {

        Calendar c = Calendar.getInstance();
        c.set(year, month, 0, 0, 0);
        Long begin = c.getTimeInMillis();

        ++month;

        c.set(year, month, 1, 0, 0);
        Long end = c.getTimeInMillis();

        return Query.many(Workday.class, "SELECT * FROM workdays WHERE date BETWEEN ? AND ? ORDER BY date", begin, end).get();
    }

    public static void delete(Workday workday) {

        Transaction t = new Transaction();
        try {
            workday.delete(t);
            t.setSuccessful(true);
        } finally {
            t.finish();
        }

    }


    public static void setGoogleCalendarEventId(Workday workday, long eventId) {
        Transaction t = new Transaction();
        try {
            workday.setGoogleCalendarEventId(eventId);
            if (!workday.save(t)) {
                return;
            }
            t.setSuccessful(true);
        } finally {
            t.finish();
        }
    }
}
