package com.th.calendar;

import android.content.Context;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.Calendar;

import static com.th.calendar.CalendarAdapter.MONTH_PER_YEAR;
import static com.th.calendar.CalendarAdapter.TYPE_DAY;
import static com.th.calendar.CalendarAdapter.TYPE_TITLE;

public class CalendarView extends ConstraintLayout implements View.OnClickListener {

    private CalendarSnapHelper mSnapHelper;

    public CalendarView(Context context) {
        super(context);
        init(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    void init(final Context context, AttributeSet attrs) {
        View root = inflate(context, R.layout.calendar_view, this);

        final TextView monthView = root.findViewById(R.id.month);
        final View nextView = root.findViewById(R.id.next);
        final View prevView = root.findViewById(R.id.prev);
        final RecyclerView recyclerView = root.findViewById(R.id.days);
        final CalendarAdapter adapter = new CalendarAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(context, 13, LinearLayoutManager.HORIZONTAL, true);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case TYPE_TITLE:
                        return 1;
                    case TYPE_DAY:
                        return 2;
                    default:
                        return -1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);

        mSnapHelper = new CalendarSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                adapter.setDimensions(recyclerView.getMeasuredWidth() / 7);
                Calendar endCalendar = Calendar.getInstance();
                Calendar startCalendar = Calendar.getInstance();
                Calendar currentCalendar = Calendar.getInstance();
                startCalendar.set(1900, 0, 1);
                endCalendar.set(2100, 0, 1);
                adapter.setStartCalendar(startCalendar);
                adapter.setEndCalendar(endCalendar);
                recyclerView.setAdapter(adapter);

                mSnapHelper.gotoMonth((endCalendar.get(Calendar.YEAR) - currentCalendar.get(Calendar.YEAR)) * MONTH_PER_YEAR + endCalendar.get(Calendar.MONTH) - currentCalendar.get(Calendar.MONTH));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int mSnapPosition = -1;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int snapPosition = mSnapHelper.getSnapPosition();
                if (snapPosition != mSnapPosition) {
                    mSnapPosition = snapPosition;
                    Calendar currentMonthCalendar = adapter.getFirstDayOfMonth(snapPosition);
                    int currentMonth = currentMonthCalendar.get(Calendar.MONTH);
                    int currentYear = currentMonthCalendar.get(Calendar.YEAR);
                    Calendar endCalendar = adapter.getEndCalendar();
                    Calendar startCalendar = adapter.getStartCalendar();
                    nextView.setVisibility(currentMonth == endCalendar.get(Calendar.MONTH) && currentYear == endCalendar.get(Calendar.YEAR) ? GONE : VISIBLE);
                    prevView.setVisibility(currentMonth == startCalendar.get(Calendar.MONTH) && currentYear == startCalendar.get(Calendar.YEAR) ? GONE : VISIBLE);

                    monthView.setText(String.format("%tB", currentMonthCalendar));
                }
            }
        });

        nextView.setOnClickListener(this);
        prevView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                mSnapHelper.next();
                break;
            case R.id.prev:
                mSnapHelper.prev();
                break;
        }
    }
}
