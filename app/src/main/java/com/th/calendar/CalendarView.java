package com.th.calendar;

import android.content.Context;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarView extends ConstraintLayout {

    private static final String TAG = CalendarView.class.getSimpleName();

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

        final RecyclerView recyclerView = root.findViewById(R.id.days);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 7, LinearLayoutManager.HORIZONTAL, true));

        mAdapter = new CalendarAdapter();
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
    }

    public void setData(List<Date> data) {
        mAdapter.setData(data);
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    private static class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int DAY_PER_WEEK = 7;
        private static final int MONTH_PER_YEAR = 12;
        private static final int ITEM_PER_MONTH = 49;
        private static final int TYPE_TITLE = 0;
        private static final int TYPE_DAY = 1;

        private List<Date> mData;
        private int mItemWidth;
        private int mItemHeight;

        @Override
        public int getItemViewType(int position) {
            return position % DAY_PER_WEEK == 0 ? TYPE_TITLE : TYPE_DAY;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            switch (viewType) {
                case TYPE_TITLE:
                    View titleView = LayoutInflater.from(context).inflate(R.layout.title_view_holder, parent, false);
                    titleView.getLayoutParams().width = mItemWidth;

                    Log.d(TAG, "onCreateViewHolder: viewType = TYPE_TITLE, width = " + mItemWidth);
                    return new TitleViewHolder(titleView);
                case TYPE_DAY:
                default:
                    View itemView = LayoutInflater.from(context).inflate(R.layout.day_view_holder, parent, false);
                    ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                    layoutParams.width = mItemWidth;
                    layoutParams.height = mItemHeight;
                    Log.d(TAG, "onCreateViewHolder: viewType = TYPE_DAY, width = " + mItemWidth + ", height = " + mItemHeight);
                    return new DayViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case TYPE_TITLE:
                    String title = "@@";
                    switch (position / 7) {
                        case 0:
                            title = "S";
                            break;
                        case 1:
                            title = "F";
                            break;
                        case 2:
                            title = "T";
                            break;
                        case 3:
                            title = "W";
                            break;
                        case 4:
                            title = "T";
                            break;
                        case 5:
                            title = "M";
                            break;
                        case 6:
                            title = "S";
                            break;
                    }
                    Log.d(TAG, "onBindViewHolder: viewType = TYPE_TITLE, position = " + position + ", title = " + title);
                    ((TitleViewHolder) holder).title.setText(title);
                    break;
                case TYPE_DAY:
                    Log.d(TAG, "onBindViewHolder: viewType = TYPE_DAY, position = " + position + ", title = " + "d");
                    ((DayViewHolder) holder).day.setText("d");
                    break;
            }
        }

        @Override
        public int getItemCount() {
            if (mData == null || mData.isEmpty()) return 0;
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            startCalendar.setTime(mData.get(mData.size() - 1));
            endCalendar.setTime(mData.get(0));

            return ((endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)) * MONTH_PER_YEAR + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH) + 1) * ITEM_PER_MONTH;
        }

        void setData(List<Date> data) {
            mData = data;
        }

        void setDimensions(int itemWidth, int itemHeight) {
            Log.d(TAG, "setDimensions: itemWidth = " + itemWidth + ", itemHeight = " + itemHeight);
            mItemWidth = itemWidth;
            mItemHeight = itemHeight;
        }

        private static class TitleViewHolder extends RecyclerView.ViewHolder {

            TextView title;

            TitleViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView;
            }
        }

        private static class DayViewHolder extends RecyclerView.ViewHolder {

            TextView day;

            DayViewHolder(View itemView) {
                super(itemView);
                day = (TextView) itemView;
            }
        }
    }
}
