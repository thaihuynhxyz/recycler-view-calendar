package com.th.calendar;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int DAY_PER_WEEK = 7;
    private static final int MONTH_PER_YEAR = 12;
    private static final int ITEM_PER_MONTH = 49;
    static final int TYPE_TITLE = 0;
    static final int TYPE_DAY = 1;

    private Calendar mStartCalendar = Calendar.getInstance();
    private Calendar mEndCalendar = Calendar.getInstance();
    private List<Date> mData;
    private int[] mCalendarMatrix = new int[49];
    private int mItemWidth;

    CalendarAdapter() {
        int[][] matrix = new int[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                matrix[i][j] = i * 7 + j;
            }
        }

        // rotate -90
        int[][] rotateMatrix = new int[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                rotateMatrix[i][j] = matrix[j][6 - i];
            }
        }

        // translate y + 1
        for (int i = 0; i < 7; i++) {
            System.arraycopy(rotateMatrix[i], 0, mCalendarMatrix, i * 7 + 1, 6);
        }
    }

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
                return new CalendarAdapter.TitleViewHolder(titleView);
            case TYPE_DAY:
            default:
                View itemView = LayoutInflater.from(context).inflate(R.layout.day_view_holder, parent, false);
                itemView.getLayoutParams().width = mItemWidth;
                return new DayViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_TITLE:
                String title = "";
                switch (position / DAY_PER_WEEK % DAY_PER_WEEK) {
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
                ((TitleViewHolder) holder).title.setText(title);
                break;
            case TYPE_DAY:
                Calendar firstDayOfMonth = (Calendar) mEndCalendar.clone();
                firstDayOfMonth.add(Calendar.MONTH, -position / ITEM_PER_MONTH);
                firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);

                int firstDayIndex = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1;
                int dayPosition = mCalendarMatrix[position % ITEM_PER_MONTH];
                if (dayPosition >= firstDayIndex && dayPosition < firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH) + firstDayIndex) {
                    firstDayOfMonth.add(Calendar.DAY_OF_MONTH, dayPosition - firstDayIndex);
                    ((DayViewHolder) holder).day.setVisibility(View.VISIBLE);
                    ((DayViewHolder) holder).day.setText(String.valueOf(firstDayOfMonth.get(Calendar.DAY_OF_MONTH)));
                    for (Date date : mData) {
                        if (date.compareTo(firstDayOfMonth.getTime()) == 0) {
                            ((DayViewHolder) holder).day.setBackgroundColor(Color.BLUE);
                        } else {
                            ((DayViewHolder) holder).day.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                } else {
                    ((DayViewHolder) holder).day.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null || mData.isEmpty()) return 0;
        mStartCalendar.setTime(mData.get(mData.size() - 1));
        mEndCalendar.setTime(mData.get(0));

        return ((mEndCalendar.get(Calendar.YEAR) - mStartCalendar.get(Calendar.YEAR)) * MONTH_PER_YEAR + mEndCalendar.get(Calendar.MONTH) - mStartCalendar.get(Calendar.MONTH) + 1) * ITEM_PER_MONTH;
    }

    Date getItem(int position) {
        return mData.get(position);
    }

    void setData(List<Date> data) {
        mData = data;
    }

    void setDimensions(int itemWidth) {
        mItemWidth = itemWidth;
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
