package uz.scan_card.cardscan;

import static uz.scan_card.cardscan.base.image.BitmapHelper.getByteArray;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import uz.scan_card.cardscan.base.ScanActivityImpl;
import uz.scan_card.cardscan.base.ScanBaseActivity;


/**
 * The ScanActivity class provides the main interface to the scanning functionality. To use this
 * activity, call the  method and override
 * onActivityResult in your own activity to get the result of the scan.
 */
public class ScanActivity {
    private static final String TAG = "ScanActivity";
    public static final int SCAN_REQUEST_CODE = 51234;
    public static final String CARD_NUMBER = "card_number";

    public static Intent buildIntent(@NonNull Activity activity, Boolean delayShowingExpiration, @Nullable String titleText, int hintText, @Nullable Drawable torchOnIcon, @Nullable Drawable torchOffIcon) {
        ScanBaseActivity.warmUp(activity.getApplicationContext());
        Intent intent = new Intent(activity, ScanActivityImpl.class);
        intent.putExtra(ScanBaseActivity.DELAY_SHOWING_EXPIRATION, delayShowingExpiration);
        if (titleText != null) {
            intent.putExtra(ScanActivityImpl.SCAN_CARD_TEXT, titleText);
        }
        intent.putExtra(ScanActivityImpl.POSITION_CARD_TEXT, hintText);
        if (torchOnIcon != null) {
            intent.putExtra(ScanActivityImpl.SCAN_CARD_TORCH_ON, getByteArray(torchOnIcon));
        }
        if (torchOffIcon != null) {
            intent.putExtra(ScanActivityImpl.SCAN_CARD_TORCH_OFF, getByteArray(torchOffIcon));
        }
        return intent;
    }

    public static @Nullable
    CreditCard creditCardFromResult(Intent intent) {
        String number = intent.getStringExtra(ScanActivityImpl.RESULT_CARD_NUMBER);
        String month = intent.getStringExtra(ScanActivityImpl.RESULT_EXPIRY_MONTH);
        String year = intent.getStringExtra(ScanActivityImpl.RESULT_EXPIRY_YEAR);

        if (TextUtils.isEmpty(number)) {
            return null;
        }

        return new CreditCard(number, month, year);
    }
}
