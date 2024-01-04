package uz.fido.utils.view.custom_edit_text.mask_edit_text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;

import uz.fido.utils.R;

public class MaskEditText extends TextInputEditText {

    private Drawable drawableRight;
    private Drawable drawableLeft;
    private Drawable drawableTop;
    private Drawable drawableBottom;
    private DrawableClickListener clickListener;
    int actionX, actionY;
    private ClipBoardListener clipBoardListener;
    private static final char REPLACE_CHAR = '#';
    private String mask;
    private boolean updating;
    private TextInputLayout textInputLayout;
    private boolean error = false;
    private ArrayList<String> prefixes = new ArrayList<>();
    private MaskEditInterface maskEditInterface;

    public void setListener(MaskEditInterface maskEditInterface) {
        this.maskEditInterface = maskEditInterface;
    }

    public MaskEditText(Context context) {
        super(context);
    }

    public MaskEditText(Context context, String mask) {
        super(context);
        setMask(mask);
    }

    public MaskEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MaskEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaskEditText);
        try {
            setMask(a.getString(R.styleable.MaskEditText_met_mask));
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void addClipBoardListener(ClipBoardListener listener) {
        if (clipBoardListener != null) {
            clipBoardListener = listener;
        }
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean consumed = super.onTextContextMenuItem(id);
        switch (id) {
            case android.R.id.cut:
                break;
            case android.R.id.paste:
                onTextPaste();
                break;
//            case android.R.id.copy:
        }
        return consumed;
    }

    private void onTextPaste() {
        if (clipBoardListener != null) {
            clipBoardListener.onTextPaste();
        }
    }

    private void applyMask(Editable editable) {
        if (TextUtils.isEmpty(editable) || !hasMask()) {
            return;
        }
        //remove input filters to ignore input type
        InputFilter[] filters = editable.getFilters();
        editable.setFilters(new InputFilter[0]);

        int maskLen = mask.length();
        int textLen = editable.length();

        int i = 0;
        int notSymbolIndex = 0;
        StringBuilder sb = new StringBuilder();
        while (i < maskLen && notSymbolIndex < textLen) {
            if (mask.charAt(i) == editable.charAt(notSymbolIndex) || mask.charAt(i) == REPLACE_CHAR) {
                sb.append(editable.charAt(notSymbolIndex));
                notSymbolIndex++;
            } else {
                sb.append(mask.charAt(i));
            }
            i++;
        }

        editable.clear();
        editable.append(sb.toString());

        //reset filters
        editable.setFilters(filters);
    }

    public boolean hasMask() {
        return !TextUtils.isEmpty(mask);
    }

    public String getRawText() {
        String text = String.valueOf(super.getText());
        return getUnmaskedText(text);
    }

    public void setMask(String mask) {
        this.mask = mask;
//        MaskedFormatter maskedFormatter = new MaskedFormatter(mask.toString());
//        addTextChangedListener(new MaskedWatcher(maskedFormatter, this));
        if (hasMask()) {
            setMaxLength(mask.length());
            addTextChangedListener(new MaskFormatter());
        }
    }

    String regex;

    public void setMask(String mask, String regex) {
        this.mask = mask;
        this.regex = regex;
//        MaskedFormatter maskedFormatter = new MaskedFormatter(mask.toString());
//        addTextChangedListener(new MaskedWatcher(maskedFormatter, this));
        if (hasMask()) {
            setMaxLength(mask.length());
            addTextChangedListener(new MaskFormatter());
        }
    }

    public void setMaxLength(int length) {
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }

    public String getUnmaskedText(String text) {
        if (TextUtils.isEmpty(text) || !hasMask()) {
            return text;
        }
        int maskLen = mask.length();
        int textLen = text.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maskLen && i < textLen; i++) {
            if (mask.charAt(i) == REPLACE_CHAR) {
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }

    public void setPrefix(String prefix) {
        ArrayList<String> prefixes = new ArrayList<>(Arrays.asList(prefix.split(",")));
        this.prefixes = prefixes;
    }

    public void setTextInputLayout(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    private void checkForPrefix(Editable editable) {
        boolean found = false;
        for (String prefix : prefixes) {
            String str = editable.toString();
            if (str.length() >= prefix.length()) {
                str = str.substring(0, prefix.length());
            } else {
                found = true;
            }
            if (prefix.equals(str)) {
                found = true;
            }
        }
        if (textInputLayout != null) {
            if (!found) {
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError(getContext().getString(R.string.wrong_format));
                error = true;

            } else {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
                error = false;
            }
        }
        if (maskEditInterface != null) {
            maskEditInterface.checkError(error);
        }
    }

    private class MaskFormatter implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (prefixes.size() > 0) {
                checkForPrefix(editable);
            }
            if (updating || !hasMask()) {
                return;
            }
            updating = true;
            applyMask(editable);
            updating = false;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top,
                                     Drawable right, Drawable bottom) {
        if (left != null) {
            drawableLeft = left;
        }
        if (right != null) {
            drawableRight = right;
        }
        if (top != null) {
            drawableTop = top;
        }
        if (bottom != null) {
            drawableBottom = bottom;
        }
        super.setCompoundDrawables(left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Rect bounds;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            actionX = (int) event.getX();
            actionY = (int) event.getY();
            if (drawableBottom != null
                    && drawableBottom.getBounds().contains(actionX, actionY)) {
                clickListener.onClick(DrawableClickListener.DrawablePosition.BOTTOM);
                return super.onTouchEvent(event);
            }

            if (drawableTop != null
                    && drawableTop.getBounds().contains(actionX, actionY)) {
                clickListener.onClick(DrawableClickListener.DrawablePosition.TOP);
                return super.onTouchEvent(event);
            }

            if (drawableLeft != null) {
                bounds = null;
                bounds = drawableLeft.getBounds();

                int x, y;
                int extraTapArea = (int) (13 * getResources().getDisplayMetrics().density + 0.5);

                x = actionX;
                y = actionY;

                if (!bounds.contains(actionX, actionY)) {
                    /** Gives the +20 area for tapping. */
                    x = (int) (actionX - extraTapArea);
                    y = (int) (actionY - extraTapArea);

                    if (x <= 0)
                        x = actionX;
                    if (y <= 0)
                        y = actionY;

                    /** Creates square from the smallest value */
                    if (x < y) {
                        y = x;
                    }
                }

                if (bounds.contains(x, y) && clickListener != null) {
                    clickListener
                            .onClick(DrawableClickListener.DrawablePosition.LEFT);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    return false;

                }
            }

            if (drawableRight != null) {
                bounds = null;
                bounds = drawableRight.getBounds();
                int x, y;
                int extraTapArea = 13;

                x = (int) (actionX + extraTapArea);
                y = (int) (actionY - extraTapArea);
                x = getWidth() - x;
                if (x <= 0) {
                    x += extraTapArea;
                }
                if (y <= 0)
                    y = actionY;

                /**If drawble bounds contains the x and y points then move ahead.*/
                if (bounds.contains(x, y) && clickListener != null) {
                    clickListener
                            .onClick(DrawableClickListener.DrawablePosition.RIGHT);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    return false;
                }
                return super.onTouchEvent(event);
            }

        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        drawableRight = null;
        drawableBottom = null;
        drawableLeft = null;
        drawableTop = null;
        super.finalize();
    }

    public void setDrawableClickListener(DrawableClickListener listener) {
        this.clickListener = listener;
    }
}

