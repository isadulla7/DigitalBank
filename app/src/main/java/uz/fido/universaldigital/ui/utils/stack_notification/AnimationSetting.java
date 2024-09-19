package uz.fido.universaldigital.ui.utils.stack_notification;

import android.view.animation.Interpolator;

public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
