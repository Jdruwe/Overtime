package adevador.com.overtime.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import adevador.com.overtime.model.Workday;
import io.realm.Realm;
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
            Workday dog = realm.createObject(Workday.class);
            dog.setDate(date);
            dog.setHours(hours);
            dog.setMinutes(minutes);
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

        return realm.where(Workday.class)
                .between("date", begin, end)
                .findAll()
                .sort("date");
    }

    public static Workday get(Context context, Date date) {
        Realm realm = getRealm(context);
        return realm.where(Workday.class)
                .equalTo("date", date)
                .findFirst();
    }

    public static void delete(Context context, Date date) {
        Realm realm = getRealm(context);
        realm.beginTransaction();
        RealmResults<Workday> workday = realm.where(Workday.class)
                .equalTo("date", date)
                .findAll();
        workday.remove(0);
        realm.commitTransaction();
    }
}
