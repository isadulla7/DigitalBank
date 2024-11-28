package uz.fido.universaldigital.ui.utils.lang;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import java.util.Locale;

import uz.fido.universaldigital.ui.utils.extensions.PaperExtensionKt;

public class LocaleHelper {

    public static String getLanguage(Context context) {
        getPersistedData(Locale.getDefault().getLanguage(), context);
        if (!getPersistedData(Locale.getDefault().getLanguage(), context).isEmpty()) {
            return getPersistedData(Locale.getDefault().getLanguage(), context);
        } else {
            return "uz";
        }
    }

    public static Context setLocale(Context context, String language) {
        persist(language, context);
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

    private static String getPersistedData(String defaultLanguage, Context context) {
        return PaperExtensionKt.getFromPaper(context, "lang", defaultLanguage);
    }

    private static void persist(String language, Context context) {

        Log.d("TAG", "persist: "+language);
        PaperExtensionKt.saveToPaper(context, "lang", language);
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
