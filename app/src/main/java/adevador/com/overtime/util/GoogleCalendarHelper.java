package adevador.com.overtime.util;

import android.content.ContentResolver;
import android.provider.CalendarContract;

public class GoogleCalendarHelper {

    public static void deleteCalendarEvent(ContentResolver contentResolver, long eventId) {
        String[] selectionArgs = new String[]{Long.toString(eventId)};
        contentResolver.delete(
                CalendarContract.Events.CONTENT_URI,
                CalendarContract.Events._ID + " = ? ",
                selectionArgs
        );
    }

}
