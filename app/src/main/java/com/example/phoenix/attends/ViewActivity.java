package com.example.phoenix.attends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {

    Course course;
    ArrayList<CourseEvent> events;
    ArrayAdapter<CourseEvent> events_adapter;
    ListView events_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Log.d("myd", "VIEW ACTIVITY onCreate => reached");
        Intent getCourseObj = getIntent();
        course = (Course)getCourseObj.getSerializableExtra("courseObj");
        setTitle(course.name);
        events = course.events;
        events_adapter = new EventsViewAdapter(this, R.layout.list_view_events, events);
        events_view = (ListView)findViewById(R.id.events);
        events_view.setAdapter(events_adapter);
        TextView instructorName = (TextView)findViewById(R.id.instructor_name);
        TextView attendedLectures = (TextView)findViewById(R.id.attended_lectures);
        TextView scheduledLectures = (TextView)findViewById(R.id.scheduled_lectures);
        TextView lectureRatio = (TextView)findViewById(R.id.lecture_ratio);
        attendedLectures.setText(String.valueOf(course.noOfLecturesAttended));
        scheduledLectures.setText(String.valueOf(course.noOfLecturesScheduled));
        instructorName.setText(course.instructor);
        lectureRatio.setText(String.valueOf(((float) course.noOfLecturesAttended / course.noOfLecturesScheduled) * 100) + " %");

        attendedLectures.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.White));
        scheduledLectures.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.White));
        lectureRatio.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.White));
        instructorName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.White));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.some_thing) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    public class EventsViewAdapter extends ArrayAdapter<CourseEvent> {
        Context context;
        int layoutResourceId;
        ArrayList<CourseEvent> events;
        EventsViewAdapter(Context context, int layoutResourceId, ArrayList<CourseEvent> events) {
            super(context, layoutResourceId, events);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.events = events;
            Log.d("myd", "VIEW ACTIVITY EventsViewAdapter CONSTRUCTOR => constructor");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View eventView = convertView;
            CourseEvent tempEvent = events.get(position);
            if(eventView == null) {
                Log.d("myd", "VIEW ACTIVITY getView => inflating grid cell");
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                eventView = inflater.inflate(layoutResourceId, null);
            }
            TextView tempDay = (TextView)eventView.findViewById(R.id.day);
            TextView tempTime = (TextView)eventView.findViewById(R.id.time);
            tempDay.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.White));
            tempTime.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.White));
            String tempDayVal = "";
            switch (tempEvent.day) {
                case 0:
                    tempDayVal = "Monday";
                    break;

                case 1:
                    tempDayVal = "Tuesday";
                    break;

                case 2:
                    tempDayVal = "Wednesday";
                    break;

                case 3:
                    tempDayVal = "Thursday";
                    break;

                case 4:
                    tempDayVal = "Friday";
                    break;

                case 5:
                    tempDayVal = "Saturday";
                    break;

                case 6:
                    tempDayVal = "Sunday";
                    break;

            }
            tempDay.setText(tempDayVal);
            tempTime.setText(String.format("%02d", tempEvent.hour) + " : " + String.format("%02d", tempEvent.minute));
            return eventView;
        }
    }

}
