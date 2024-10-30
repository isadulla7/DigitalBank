package uz.fido.universaldigital.ui.utils.calendar_view;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uz.fido.universaldigital.R;
import uz.fido.universaldigital.ui.utils.extensions.ExtensionsKt;

public class CalendarView extends LinearLayoutCompat {

    private ImageView previousButton, nextButton;
    private TextView currentDate;
    public GridView calendarGridView;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private final SimpleDateFormat formatter = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
    private Calendar cal = Calendar.getInstance(Locale.getDefault());
    private Context context;
    private GridAdapter mAdapter;
    int prev = -1, pos, cr_pos = -2;

    List<Date> dayValueInCells;
    List<EventObjects> mEvents = new ArrayList<>();
    Calendar today_date = Calendar.getInstance();
    Date color_date;
    DateSelector mDateSelector;
    MonthChanger mMonthChanger;
    List<ColoredDate> colorFulDates = new ArrayList<>();

    Drawable todayIndicator = null, selectedIndicator = null, eventIndicator = null;
    int dateColor = 0, nonMonthDateColor = 0, todayDateColor = 0, selectedDateColor = 0;
    Typeface monthFontFace = null, weekFontFace = null, dateFontFace = null;
    int monthTextStyle = 0, weekTextStyle = 0, dateTextStyle = 0;
    Drawable nextIcon = null, prevIcon = null;
    int calendarBackgroundColor = 0;
    boolean animatingMonths = true;
    GridLayoutAnimationController animationController;

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        cal = getZeroTime(cal);
        today_date = getZeroTime(today_date);
        color_date = today_date.getTime();

        todayIndicator = AppCompatResources.getDrawable(context, R.drawable.calendarview_today);
        selectedIndicator = AppCompatResources.getDrawable(context, R.drawable.calendarview_select_date);
        eventIndicator = AppCompatResources.getDrawable(context, R.drawable.calendarview_event);
        dateColor = context.getColor(R.color.blackColor);
        nonMonthDateColor = context.getColor(R.color.hintColor);
        todayDateColor = context.getColor(R.color.blackColor);
        selectedDateColor = context.getColor(R.color.frozenWhite);
        calendarBackgroundColor = context.getColor(R.color.cardBgColor);

        try (TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KalendarView, 0, 0)) {

            int drwTodayId = typedArray.getResourceId(R.styleable.KalendarView_todayIndicator, 0);
            if (drwTodayId != 0)
                todayIndicator = AppCompatResources.getDrawable(context, drwTodayId);

            int drwSelectedId = typedArray.getResourceId(R.styleable.KalendarView_selectedIndicator, 0);
            if (drwSelectedId != 0)
                selectedIndicator = AppCompatResources.getDrawable(context, drwSelectedId);

            int drwEventId = typedArray.getResourceId(R.styleable.KalendarView_eventIndicator, 0);
            if (drwEventId != 0)
                eventIndicator = AppCompatResources.getDrawable(context, drwEventId);

            int colorDate = typedArray.getColor(R.styleable.KalendarView_dateColor, 0);
            if (colorDate != 0)
                dateColor = colorDate;
            int colorNonMonth = typedArray.getColor(R.styleable.KalendarView_nonMonthDateColor, 0);
            if (colorNonMonth != 0)
                nonMonthDateColor = colorNonMonth;
            int colorToday = typedArray.getColor(R.styleable.KalendarView_todayDateColor, 0);
            if (colorToday != 0)
                todayDateColor = colorToday;
            int colorSelected = typedArray.getColor(R.styleable.KalendarView_selectedDateColor, 0);
            if (colorSelected != 0)
                selectedDateColor = colorSelected;

            // Set a custom font family via its reference
            int monthFontId = typedArray.getResourceId(R.styleable.KalendarView_monthFontFamily, 0);
            if (monthFontId != 0)
                monthFontFace = ResourcesCompat.getFont(context, monthFontId);
            int weekFontId = typedArray.getResourceId(R.styleable.KalendarView_weekFontFamily, 0);
            if (weekFontId != 0)
                weekFontFace = ResourcesCompat.getFont(context, weekFontId);
            int dateFontId = typedArray.getResourceId(R.styleable.KalendarView_dateFontFamily, 0);
            if (dateFontId != 0)
                dateFontFace = ResourcesCompat.getFont(context, dateFontId);
            //for text styles
            monthTextStyle = typedArray.getResourceId(R.styleable.KalendarView_monthTextStyle, 0);
            weekTextStyle = typedArray.getResourceId(R.styleable.KalendarView_weekTextStyle, 0);
            dateTextStyle = typedArray.getResourceId(R.styleable.KalendarView_dateTextStyle, 0);

            //for next icon
            int tempNextIcon = typedArray.getResourceId(R.styleable.KalendarView_nextIcon, 0);
            if (tempNextIcon != 0)
                nextIcon = AppCompatResources.getDrawable(context, tempNextIcon);
            //for prev icon
            int tempPrevIcon = typedArray.getResourceId(R.styleable.KalendarView_prevIcon, 0);
            if (tempPrevIcon != 0)
                prevIcon = AppCompatResources.getDrawable(context, tempPrevIcon);

            //for calendar background
            int colorBg = typedArray.getColor(R.styleable.KalendarView_calendarBackground, 0);
            if (colorBg != 0)
                calendarBackgroundColor = colorBg;

            //to set the animation controller
            animatingMonths = typedArray.getBoolean(R.styleable.KalendarView_animatingMonths, true);
        }

        initializeUILayout();
        setUpCalendarAdapter();
        setPreviousButtonClickEvent();
        setNextButtonClickEvent();
        setGridCellClickEvents();
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initializeUILayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendarview, this);
        previousButton = view.findViewById(R.id.previous_month);
        nextButton = view.findViewById(R.id.next_month);
        currentDate = view.findViewById(R.id.display_current_date);
        calendarGridView = view.findViewById(R.id.calendar_grid);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.grid_anim);
        animationController = new GridLayoutAnimationController(animation, 0f, .1f);
    }

    private void setPreviousButtonClickEvent() {
        previousButton.setOnClickListener(v -> {
            cal.add(Calendar.MONTH, -1);
            setUpCalendarAdapter();
            if (mMonthChanger != null)
                mMonthChanger.onMonthChanged(cal.getTime());
        });
    }

    private void setNextButtonClickEvent() {
        nextButton.setOnClickListener(v -> {
            cal.add(Calendar.MONTH, 1);
            setUpCalendarAdapter();
            if (mMonthChanger != null)
                mMonthChanger.onMonthChanged(cal.getTime());
        });
    }

    public void setGridCellClickEvents() {
        calendarGridView.setOnItemClickListener((parent, view, position, id) -> {
            pos = (int) view.getTag();
            LinearLayoutCompat llParent = view.findViewById(R.id.ll_parent);
            llParent.setBackground(selectedIndicator);
            TextView txt = view.findViewById(R.id.calendar_date_id);
            txt.setTextColor(selectedDateColor);
            color_date = dayValueInCells.get(pos);

            if ((prev != -1) && prev != cr_pos) {
                LinearLayoutCompat prevParent = parent.getChildAt(prev).findViewById(R.id.ll_parent);
                prevParent.setBackgroundColor(calendarBackgroundColor);
                TextView txtd = parent.getChildAt(prev).findViewById(R.id.calendar_date_id);
                txtd.setTextColor((int) (txtd.getTag()) == 0 ? dateColor : nonMonthDateColor);
                int customDateColor = mAdapter.getDateColor(dayValueInCells.get(prev));
                if (customDateColor != 0 && (int) txtd.getTag() == 0)
                    txtd.setTextColor(customDateColor);
            }
            if (prev == cr_pos) {
                View childView = parent.getChildAt(prev);
                if (childView != null) {
                    LinearLayoutCompat todayParent = childView.findViewById(R.id.ll_parent);
                    todayParent.setBackground(todayIndicator);
                    TextView txtd = childView.findViewById(R.id.calendar_date_id);
                    txtd.setTextColor(todayDateColor);
                    int customDateColor = mAdapter.getDateColor(dayValueInCells.get(prev));
                    if (customDateColor != 0)
                        txtd.setTextColor(customDateColor);
                }
            }
            prev = pos;

            int month_id = (int) txt.getTag();
            if (month_id == -1) {
                cr_pos = pos;
            }
            if (month_id == 1) {
                cal.add(Calendar.MONTH, 1);
                setUpCalendarAdapter();
                cr_pos = -2;
                //to inform month changed
                if (mMonthChanger != null)
                    mMonthChanger.onMonthChanged(cal.getTime());
            }
            if (month_id == 2) {
                cal.add(Calendar.MONTH, -1);
                setUpCalendarAdapter();
                cr_pos = -2;
                //to inform month changed
                if (mMonthChanger != null)
                    mMonthChanger.onMonthChanged(cal.getTime());
            }
            if (mDateSelector != null)
                mDateSelector.onDateClicked(color_date);
        });
    }

    private void setUpCalendarAdapter() {
        dayValueInCells = new ArrayList<>();
        Calendar mCal = (Calendar) cal.clone();
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 1;
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
        while (dayValueInCells.size() < MAX_CALENDAR_COLUMN) {
            dayValueInCells.add(mCal.getTime());
            mCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        String sDate = formatter.format(cal.getTime());
        currentDate.setText(ExtensionsKt.capitalizeFirstChar(sDate));
        prev = dayValueInCells.indexOf(color_date);
        cr_pos = dayValueInCells.indexOf(today_date.getTime());
        mAdapter = new GridAdapter(context, dayValueInCells, cal, mEvents, color_date, colorFulDates);
        mAdapter.setDrawables(todayIndicator, selectedIndicator, eventIndicator);
        mAdapter.setTextColors(dateColor, nonMonthDateColor, todayDateColor, selectedDateColor);
        calendarGridView.setAdapter(mAdapter);

        //for animating month changes
        if (animatingMonths && !isInEditMode())
            calendarGridView.setLayoutAnimation(animationController);
    }

    public void addEvents(List<EventObjects> mEvents) {
        if (mAdapter != null) {
            mAdapter.allEvents.addAll(mEvents);
            this.mEvents.addAll(mEvents);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void clearEvents() {
        if (mAdapter != null) {
            mAdapter.allEvents.clear();
            this.mEvents.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setDateSelector(DateSelector mSelector) {
        this.mDateSelector = mSelector;
    }

    public void setMonthChanger(MonthChanger mChanger) {
        this.mMonthChanger = mChanger;
    }

    private Calendar getZeroTime(Calendar mCalendar) {
        Calendar tempCal = (Calendar) mCalendar.clone();
        tempCal.set(Calendar.HOUR, 0);
        tempCal.set(Calendar.MINUTE, 0);
        tempCal.set(Calendar.SECOND, 0);
        tempCal.set(Calendar.MILLISECOND, 0);
        return tempCal;
    }

}
