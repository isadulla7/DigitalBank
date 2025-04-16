package uz.fido.utils.format;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtil {

    public static String toString(long i) {
        DecimalFormat nf = new DecimalFormat("###,##0");
        DecimalFormatSymbols dfs = nf.getDecimalFormatSymbols();
        dfs.setGroupingSeparator(' ');
        nf.setDecimalFormatSymbols(dfs);
        return nf.format(i);
    }

    public static String toString(Long l) {
        if (l == null) return "";
        else
            return toString(l.longValue());
    }

    public static String toString(double i) {
        DecimalFormat nf = new DecimalFormat("#,##0.00");
        DecimalFormatSymbols dfs = nf.getDecimalFormatSymbols();
        dfs.setGroupingSeparator(' ');
        nf.setDecimalFormatSymbols(dfs);
        return nf.format(i);
    }

    public static String toString(Double d) {
        if (d == null) return "";
        else return toString(d.doubleValue());
    }

    public static String toString(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    public static String toString(Date date, String pattern) {
        if (date == null || pattern == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(date);
    }

    public static String toString(Date date, Locale locale) {
        if (date == null)
            return null;

        DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale);

        return fmt.format(date);
    }

    public static String toString(Date date, Locale locale, int dateFormat, int timeFormat) {
        DateFormat fmt = DateFormat.getDateTimeInstance(dateFormat, timeFormat, locale);

        return fmt.format(date);
    }

    public static String toString(String s) {
        return (s == null) ? "" : s;
    }

    @SuppressWarnings({"deprecation"})
    public static String format(Date date) {
        if (date == null)
            return "";
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        Date this_year = new Date();
        this_year.setHours(0);
        this_year.setMinutes(0);
        this_year.setSeconds(0);
        this_year.setDate(1);
        this_year.setMonth(0);


        String[] months = {"january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"};
        String prefix = "";
        String suffix = "";
        String pattern = "dd.MM.yyyy HH:mm:ss";
        if (date.after(today)) {
            prefix = "сегодня";
            pattern = "HH:mm";
        }
        if (date.before(today) && date.after(this_year)) {
            pattern = "dd";
            suffix = months[date.getMonth()];
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return prefix + " " + sdf.format(date) + " " + suffix;
    }

    public static Double parse(String s) {
        DecimalFormat nf = new DecimalFormat("#,###.00");
        DecimalFormatSymbols dfs = nf.getDecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        nf.setDecimalSeparatorAlwaysShown(true);
        dfs.setGroupingSeparator(' ');
        nf.setDecimalFormatSymbols(dfs);
        try {
            Number n = nf.parse(s);
            return n.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0D;
        }

    }


}
