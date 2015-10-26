package com.example.phoenix.attends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class ScheduleActivity extends AppCompatActivity {

    ArrayList<Course> courses;
    ArrayList<ArrayList<Schedule>> schedule;
    ArrayAdapter<Schedule> scheduleAdapter;
    ArrayList<Schedule> daySchedule;
    Schedule header;
    ListView scheduleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //should be used only if this activity always has a parent
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Schedule");
        Course tempCourse;
        CourseEvent tempEvent;
        header = new Schedule("", "", "", "");
        String tempName, tempInstructorName, tempRatio, tempScheduledTime;
        Intent fromParent = getIntent();
        Bundle coursesBundle = fromParent.getExtras();
        courses = (ArrayList<Course>)coursesBundle.getSerializable("coursesObj");
        schedule = new ArrayList<ArrayList<Schedule>>();
        for(int i = 0;i < 7;i++)
            schedule.add(new ArrayList<Schedule>());
        for(int i = 0;i < courses.size();i++) {
            tempCourse = courses.get(i);
            tempName = tempCourse.name;
            tempInstructorName = tempCourse.instructor;
            tempRatio = String.valueOf(tempCourse.noOfLecturesAttended) + " / " + String.valueOf(tempCourse.noOfLecturesScheduled);
            for(int j = 0;j < tempCourse.events.size();j++) {
                tempEvent = tempCourse.events.get(j);
                Log.d("myd", "SCHEDULED ACTIVITY onCreate => " + String.valueOf(tempEvent.day) + " encountered");
                tempScheduledTime = String.valueOf(tempEvent.hour) + " : " + String.valueOf(tempEvent.minute);
                schedule.get(tempEvent.day).add(new Schedule(tempName, tempInstructorName, tempRatio, tempScheduledTime));
            }
        }
        for(int i = 0;i < 7;i++) {
            schedule.get(i).add(0, header);
            Collections.sort(schedule.get(i), new Comparator<Schedule>() {
                @Override
                public int compare(Schedule lhs, Schedule rhs) {
                    String delimiter = ":";
                    String[] temp1, temp2;
                    int h1, h2, m1, m2;
                    temp1 = lhs.scheduledTime.split(delimiter);
                    temp2 = rhs.scheduledTime.split(delimiter);
                    if(temp1.length == 1)
                        return -1;
                    if(temp2.length == 1)
                        return 1;
                    h1 = Integer.parseInt(temp1[0].trim());
                    m1 = Integer.parseInt(temp1[1].trim());
                    h2 = Integer.parseInt(temp2[0].trim());
                    m2 = Integer.parseInt(temp2[1].trim());
                    if (h1 < h2 || (h1 == h2 && m1 < m2))
                        return -1;
                    else
                        return 1;
                }
            });
        }
        daySchedule = new ArrayList<Schedule>();
        daySchedule.addAll(schedule.get(0));
        scheduleAdapter = new seeScheduleAdapter(this, R.layout.schedule_list_element, daySchedule);
        scheduleView = (ListView)findViewById(R.id.schedule_list);
        scheduleView.setAdapter(scheduleAdapter);
        final RadioGroup daySelect = (RadioGroup)findViewById(R.id.day_select);
        RadioButton mondaySelect = (RadioButton)findViewById(R.id.mon_schedule);
        mondaySelect.setChecked(true);
        daySelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                daySchedule.clear();
                switch (checkedId) {
                    case R.id.mon_schedule:
                        Log.d("myd", "SCHEDULED ACTIVITY onCreate => monday selected with " + String.valueOf(schedule.get(0).size()) + " elements");
                        daySchedule.addAll(schedule.get(0));
                        break;
                    case R.id.tue_schedule:
                        Log.d("myd", "SCHEDULED ACTIVITY onCreate => tuesday selected with " + String.valueOf(schedule.get(1).size()) + " elements");
                        daySchedule.addAll(schedule.get(1));
                        break;
                    case R.id.wed_schedule:
                        Log.d("myd", "SCHEDULED ACTIVITY onCreate => wednesday selected with " + String.valueOf(schedule.get(2).size()) + " elements");
                        daySchedule.addAll(schedule.get(2));
                        break;
                    case R.id.thu_schedule:
                        Log.d("myd", "SCHEDULED ACTIVITY onCreate => thursday selected with " + String.valueOf(schedule.get(3).size()) + " elements");
                        daySchedule.addAll(schedule.get(3));
                        break;
                    case R.id.fri_schedule:
                        Log.d("myd", "SCHEDULED ACTIVITY onCreate => friday selected with " + String.valueOf(schedule.get(4).size()) + " elements");
                        daySchedule.addAll(schedule.get(4));
                        break;
                    case R.id.sat_schedule:
                        Log.d("myd", "SCHEDULED ACTIVITY onCreate => saturday selected with " + String.valueOf(schedule.get(5).size()) + " elements");
                        daySchedule.addAll(schedule.get(5));
                        break;
                    case R.id.sun_schedule:
                        Log.d("myd", "SCHEDULED ACTIVITY onCreate => sunday selected with " + String.valueOf(schedule.get(6).size()) + " elements");
                        daySchedule.addAll(schedule.get(6));
                        break;
                }
                scheduleAdapter.notifyDataSetChanged();
            }
        });
    }

    public class Schedule {

        String name, instructorName, ratio, scheduledTime;
        public Schedule(String name, String instructorName, String ratio, String scheduledTime) {
            this.name = name;
            this.instructorName = instructorName;
            this.ratio = ratio;
            this.scheduledTime = scheduledTime;
        }
    }

    public class seeScheduleAdapter extends ArrayAdapter<Schedule> {

        ArrayList<Schedule> daySchedule;
        Context context;
        int layoutResourceId;

        public seeScheduleAdapter(Context context, int layoutResourceId, ArrayList<Schedule> daySchedule) {
            super(context, layoutResourceId, daySchedule);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.daySchedule = daySchedule;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View scheduleView = convertView;
            Schedule schedule = daySchedule.get(position);
            if(scheduleView == null) {
                Log.d("myd", "SCHEDULED ACTIVITY onCreate => inflating grid cell");
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                scheduleView = inflater.inflate(layoutResourceId, null);
            }
            int rowWidth = scheduleView.getWidth();
            TextView name = (TextView)scheduleView.findViewById(R.id.name);
            TextView instructorName = (TextView)scheduleView.findViewById(R.id.instructor_name);
            TextView lectureRatio = (TextView)scheduleView.findViewById(R.id.lecture_ratio);
            TextView scheduledTime = (TextView)scheduleView.findViewById(R.id.schedule_time);
            if(position != 0) {
                name.setText(schedule.name);
                instructorName.setText(schedule.instructorName);
                lectureRatio.setText(schedule.ratio);
                scheduledTime.setText(schedule.scheduledTime);
            }
            else {
                SpannableString nameS = new SpannableString(getResources().getString(R.string.header_name));
                SpannableString instructorNameS = new SpannableString(getResources().getString(R.string.header_instructor_name));
                SpannableString lectureRatioS = new SpannableString(getResources().getString(R.string.header_lecture_ratio));
                SpannableString scheduledTimeS = new SpannableString(getResources().getString(R.string.header_scheduled_time));
                nameS.setSpan(new UnderlineSpan(), 0, nameS.length(), 0);
                instructorNameS.setSpan(new UnderlineSpan(), 0, instructorNameS.length(), 0);
                lectureRatioS.setSpan(new UnderlineSpan(), 0, lectureRatioS.length(), 0);
                scheduledTimeS.setSpan(new UnderlineSpan(), 0, scheduledTimeS.length(), 0);
                name.setText(nameS);
                instructorName.setText(instructorNameS);
                lectureRatio.setText(lectureRatioS);
                scheduledTime.setText(scheduledTimeS);
            }
            return scheduleView;
        }

    }

}
