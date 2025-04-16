package uz.fido.utils.view.imagezoomcrop.photoview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

public class RotationSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

    // degree values are multiplied by 10 to improve smoothness
    private static final int DEFAULT_MAX = 3600;
    private static final int DEFAULT_PROGRESS = 1800;

    private OnRotationSeekBarChangeListener mRotationListener;

    public RotationSeekBar(Context context) {
        super(context);
        init();
    }

    public RotationSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RotationSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMax(DEFAULT_MAX);
        setProgress(DEFAULT_PROGRESS);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        if (l != null && !(l instanceof OnRotationSeekBarChangeListener)) {
            throw new IllegalArgumentException("Use OnRotationSeekBarChangeListener");
        }
        mRotationListener = (OnRotationSeekBarChangeListener) l;
        super.setOnSeekBarChangeListener(l);
    }

    public void reset() {
        init();
        mRotationListener.resetPreviousProgress();
    }

    private static float fromProgressToDegrees(int progress) {
        return (progress - DEFAULT_PROGRESS) / 10f;
    }

    public static abstract class OnRotationSeekBarChangeListener implements OnSeekBarChangeListener {

        private float mPreviousProgress;

        public OnRotationSeekBarChangeListener(@NonNull RotationSeekBar seekBar) {
            mPreviousProgress = seekBar.getProgress();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            final float angle = fromProgressToDegrees(progress);
            final float delta = (progress - mPreviousProgress) / 10f;
            onRotationProgressChanged((RotationSeekBar) seekBar, angle, delta, fromUser);
            mPreviousProgress = progress;
        }

        void resetPreviousProgress() {
            mPreviousProgress = DEFAULT_PROGRESS;
        }

        /**
         * Notification that the rotation progress level has changed.
         *
         * @param seekBar  The SeekBar whose progress has changed
         * @param angle    The current SeekBar angle
         * @param delta    The difference in degrees from the previous call
         * @param fromUser True if the progress change was initiated by the user
         */
        public abstract void onRotationProgressChanged(
                @NonNull RotationSeekBar seekBar, float angle, float delta, boolean fromUser);

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

}