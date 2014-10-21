package adevador.com.overtime.data;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adevador.com.overtime.activity.MainActivity;
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
