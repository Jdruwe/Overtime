package adevador.com.overtime;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import se.emilsjolander.sprinkles.Migration;
import se.emilsjolander.sprinkles.Sprinkles;

public class Overtime extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Sprinkles sprinkles = Sprinkles.init(getApplicationContext());

        sprinkles.addMigration(new Migration() {
            @Override
            protected void onPreMigrate() {

            }

            @Override
            protected void doMigration(SQLiteDatabase db) {
                db.execSQL(
                        "CREATE TABLE Workdays (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "date INTEGER," +
                                "hours INTEGER," +
                                "minutes INTEGER," +
                                "googleCalendarEventId INTEGER" +
                                ")"
                );
            }

            @Override
            protected void onPostMigrate() {

            }
        });
    }

}
