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

import java.util.Date;
import java.util.List;

public class CalendarView extends ConstraintLayout {

    private CalendarAdapter mAdapter;

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

        final TextView mMonthView = root.findViewById(R.id.month);
        final RecyclerView recyclerView = root.findViewById(R.id.days);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 7, LinearLayoutManager.HORIZONTAL, true));

        mAdapter = new CalendarAdapter();
        final CalendarSnapHelper snapHelper = new CalendarSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mAdapter.setDimensions(recyclerView.getMeasuredWidth() / 7, (recyclerView.getHeight() - context.getResources().getDimensionPixelSize(R.dimen.dp22)) / 6);
                mAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(mAdapter);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean mScrolled = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mScrolled) {
                    mScrolled = false;
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTime(mAdapter.getItem(snapHelper.getSnapPosition()));
//                    mMonthView.setText(String.format("%tB", calendar));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx != 0 || dy != 0) mScrolled = true;
            }
        });
    }

    public void setData(List<Date> data) {
        mAdapter.setData(data);
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }
}
