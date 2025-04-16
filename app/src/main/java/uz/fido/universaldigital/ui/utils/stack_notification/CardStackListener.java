package uz.fido.universaldigital.ui.utils.stack_notification;

import android.view.View;

public interface CardStackListener {
    void onCardDisappeared(View view, int position);

    CardStackListener DEFAULT = (view, position) -> {
    };
}
