package adevador.com.overtime.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import adevador.com.overtime.model.Workday;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by druweje on 21/10/2014.
 */
public class WorkdayUtil {

    private static Realm getRealm(Context context) {
        return Realm.getInstance(context);
    }

    public static void save(Context context, ArrayList<Date> dates, int hours, int minutes) {
        Realm realm = getRealm(context);
        for (Date date : dates) {
            realm.beginTransaction();
            Workday workday = realm.createObject(Workday.class);
            workday.setDate(date);
            workday.setHours(hours);
            workday.setMinutes(minutes);
            realm.commitTransaction();
        }
    }

    public static RealmResults<Workday> getAll(Context context) {
        Realm realm = getRealm(context);
        return realm.where(Workday.class)
                .findAll();
    }

    public static RealmResults<Workday> getAll(Context context, int year, int month) {
        Realm realm = getRealm(context);

        Calendar c = Calendar.getInstance();
        c.set(year, month, 0, 0, 0);
        Date begin = c.getTime();

        ++month;

        c.set(year, month, 1, 0, 0);
        Date end = c.getTime();

        RealmResults<Workday> result = realm.where(Workday.class)
                .between("date", begin, end)
                .findAll();

        result.sort("date");

        return result;
    }

    public static Workday get(Context context, Date date) {
        Realm realm = getRealm(context);
        return realm.where(Workday.class)
                .equalTo("date", date)
                .findFirst();
    }

    public static void delete(Context context, Workday workday) {
        Realm realm = getRealm(context);
        realm.beginTransaction();

        Workday workday1 = realm.where(Workday.class)
                .equalTo("date", workday.getDate())
                .equalTo("hours", workday.getHours())
                .equalTo("minutes", workday.getMinutes())
                .findFirst();

        workday1.removeFromRealm();
        realm.commitTransaction();
    }

    public static void deleteAll(Context context) {
        Realm realm = getRealm(context);
        realm.beginTransaction();
        RealmResults<Workday> workday = realm.where(Workday.class)
                .findAll();
        workday.clear();
        realm.commitTransaction();
    }
}
