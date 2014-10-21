package adevador.com.overtime.generator;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Jeroen on 28/09/2014.
 */
public class IconGenerator {
    public static Drawable getIcon(Iconify.IconValue iconValue, int color, int dpSize, Context context) {
        return new IconDrawable(context, iconValue).colorRes(color).sizeDp(dpSize);
    }
}
