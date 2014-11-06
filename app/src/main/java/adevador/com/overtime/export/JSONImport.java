package adevador.com.overtime.export;

import android.content.Context;
import android.util.JsonReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adevador.com.overtime.data.WorkdayUtil;
import adevador.com.overtime.model.Workday;

/**
 * Created by druweje on 6/11/2014.
 */
public class JSONImport {


    public static boolean importJSON(Context context, String filePath) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            List<Workday> workdays = new ArrayList<Workday>();

            reader.beginArray();
            while (reader.hasNext()) {
                workdays.add(getWorkday(reader));
            }
            reader.endArray();

            WorkdayUtil.deleteAll(context);

            for (Workday workday : workdays) {
                ArrayList<Date> dates = new ArrayList<>();
                dates.add(workday.getDate());
                WorkdayUtil.save(context, dates, workday.getHours(), workday.getMinutes());
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    static Workday getWorkday(JsonReader reader) {
        Workday workday = new Workday();

        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "date":
                        workday.setDate(new Date(reader.nextLong()));
                        break;
                    case "hours":
                        workday.setHours(reader.nextInt());
                        break;
                    case "minutes":
                        workday.setMinutes(reader.nextInt());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return workday;
    }
}
