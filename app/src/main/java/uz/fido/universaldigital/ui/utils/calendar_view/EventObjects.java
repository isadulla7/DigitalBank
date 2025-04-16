package uz.fido.universaldigital.ui.utils.calendar_view;

import java.util.Date;

public class EventObjects {
    private int id;
    private final String message;
    private final Date date;

    public EventObjects(String message, Date date) {
        this.message = message;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }
}
