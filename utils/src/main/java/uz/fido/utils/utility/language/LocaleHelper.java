package uz.fido.utils.utility.language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

import io.paperdb.Paper;

public class LocaleHelper {

    public static String getLanguage(Context context) {
        if (getPersistedData(Locale.getDefault().getLanguage()) != null && !getPersistedData(Locale.getDefault().getLanguage()).isEmpty()) {
            return getPersistedData(Locale.getDefault().getLanguage());
        } else {
            return "uz";
        }
    }

    public static Context setLocale(Context context, String language) {
        persist(language);
        return updateResources(context, language);
    }

    public static int getSelectedLang(Context context) {
        String str = getLanguage(context).toLowerCase(Locale.ROOT);
        switch (str) {
            case "en":
            case "eng":
                return 3;
            case "uz":
            case "uzl":
                return 2;
            case "uzc":
                return 1;
            default:
                return 0;
        }
    }

    private static String getPersistedData(String defaultLanguage) {
        return Paper.book().read("lang", defaultLanguage);
    }

    private static void persist(String language) {
        Paper.book().write("lang", language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        context = context.createConfigurationContext(config);
        return context;
    }
}
