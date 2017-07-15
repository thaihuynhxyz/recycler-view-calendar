package com.th.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendar_view);

        // fake mData
        List<Date> dateList = new ArrayList<>();
        dateList.add(new Date());
        calendarView.setData(dateList);
        calendarView.notifyDataSetChanged();
    }
}
