package adevador.com.overtime.export;

import android.content.Context;
import android.os.Environment;
import android.util.JsonWriter;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import adevador.com.overtime.data.WorkdayUtil;
import adevador.com.overtime.model.Workday;
import io.realm.RealmResults;

/**
 * Created by druweje on 6/11/2014.
 */
public class JSONExport {
    public static void generateJSON(Context context) {

        if (isExternalStorageWritable()) {

            RealmResults<Workday> workdays = WorkdayUtil.getAll(context);

            File outputFile = new File(getExportDirectory(), "overtime-export.json");
            OutputStream out = null;
            try {
                out = new FileOutputStream(outputFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            JsonWriter writer = null;
            try {
                writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.setIndent("  ");

                writer.beginArray();

                for (Workday workday : workdays) {
                    writer.beginObject();
                    writer.name("date").value(workday.getDate().getTime());
                    writer.name("hours").value(workday.getHours());
                    writer.name("minutes").value(workday.getMinutes());
                    writer.endObject();
                }

                writer.endArray();

                writer.close();
                Toast.makeText(context, "Export created: Documents/Overtime/overtime-export", Toast.LENGTH_LONG).show();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(context, "Something went wrong while creating the export", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Something went wrong while creating the export", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Invalid storage media", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getExportDirectory() {
        String pathToExternalStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File appDirectory = new File(pathToExternalStorage + "/" + "Overtime");
        // have the object build the directory structure, if needed.
        appDirectory.mkdirs();
        return appDirectory;
    }
}
