package com.example.phoenix.attends;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    int yesNoSelection;

    ListView events_view;
    ArrayList<CourseEvent> events;
    ArrayAdapter<CourseEvent> events_adapter;
    Course course;

    EditText courseName;
    EditText instructorName;
    EditText lectureScheduledCount;
    EditText lectureAttendedCount;
    Button days[];
    EditText hours;
    EditText minutes;
    Button addEvent;

    int positionForEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        int fillValues = 0;
        Course tempCourse = null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //should be used only if this activity always has a parent
        getSupportActionBar().setHomeButtonEnabled(true);
        positionForEdit = getIntent().getIntExtra("position", -1);
        Bundle editCourseBundle = getIntent().getExtras();
        if(editCourseBundle != null) {
            Log.d("myd", "ADD ACTIVITY onCreate => add activity created for editing information of course " + positionForEdit);
            setTitle("Edit Information");
            fillValues = 1;
            tempCourse = (Course)editCourseBundle.getSerializable("courseObj");
        }
        else {
            setTitle("New Course");
            Log.d("myd", "ADD ACTIVITY onCreate => add activity created for creating a new course");
        }
        events = new ArrayList<CourseEvent>();
        events_adapter = new EventsViewAdapter(this, R.layout.list_add_view_events, events);
        courseName = (EditText)findViewById(R.id.course_name_input);
        instructorName = (EditText)findViewById(R.id.instructor_name_input);
        lectureScheduledCount = (EditText)findViewById(R.id.lecture_scheduled_count_input);
        lectureAttendedCount = (EditText)findViewById(R.id.lecture_attended_count_input);
        days = new Button[7];
        days[0] = (Button)findViewById(R.id.event_day_0);
        days[1] = (Button)findViewById(R.id.event_day_1);
        days[2] = (Button)findViewById(R.id.event_day_2);
        days[3] = (Button)findViewById(R.id.event_day_3);
        days[4] = (Button)findViewById(R.id.event_day_4);
        days[5] = (Button)findViewById(R.id.event_day_5);
        days[6] = (Button)findViewById(R.id.event_day_6);
        hours = (EditText)findViewById(R.id.event_time_input_hrs);
        minutes = (EditText)findViewById(R.id.event_time_input_min);
        addEvent = (Button)findViewById(R.id.add_event);
        events_view = (ListView)findViewById(R.id.added_events);
        events_view.setAdapter(events_adapter);
        for(int i = 0;i < days.length;i++) {
            days[i].setTag("off");
            days[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button day = (Button)v;
                    String tag = day.getTag().toString();
                    if(tag.equals("off")) {
                        Log.d("myd", "ADD ACTIVITY days buttons CLICK LISTENER => button was off");
                        day.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.Chocolate));
                        day.setTag("on");
                    }
                    else {
                        Log.d("myd", "ADD ACTIVITY DAY BUTTONS CLICK LISTENER => button was on");
                        day.setTag("off");
                        day.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.Black));
                    }
                }
            });
        }

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEventHandler();
            }
        });
        if(fillValues == 1) {
            courseName.setText(tempCourse.name);
            instructorName.setText(tempCourse.instructor);
            lectureScheduledCount.setText(String.valueOf(tempCourse.noOfLecturesScheduled));
            lectureAttendedCount.setText(String.valueOf(tempCourse.noOfLecturesAttended));
            for(int i = 0;i < tempCourse.events.size();i++)
                events.add(tempCourse.events.get(i));
            Log.d("myd", "ADD ACTIVITY onCreate => adding old information of course " + positionForEdit);
        }
    }

    public void addEventHandler() {
        int tempHours, tempMinutes, cnt = 0, dcnt = 0;
        try {
            tempHours = Integer.parseInt(hours.getText().toString().trim());
            tempMinutes = Integer.parseInt(minutes.getText().toString().trim());
        }
        catch (Exception e) {
            Log.d("myd", "ADD ACTIVITY addEventHandler => min and hrs info not valid");
            Toast.makeText(getApplicationContext(), "min and hrs info not valid!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tempHours < 0 || tempHours > 23 || tempMinutes < 0 || tempMinutes > 59) {
            Log.d("myd", "ADD ACTIVITY addEventHandler => min and hrs info not valid!");
            Toast.makeText(getApplicationContext(), "min and hrs info not valid!", Toast.LENGTH_SHORT).show();
            return;
        }
        for(int i = 0;i < days.length;i++) {
            String tag = days[i].getTag().toString();
            CourseEvent te;
            if(tag.equals("on")) {
                int copyCat = 0;
                for(int j = 0;j < events.size();j++) {
                    te = events.get(j);
                    if(te.day == i && te.minute == tempMinutes && te.hour == tempHours) {
                        copyCat = 1;
                        dcnt++;
                        break;
                    }
                }
                if(copyCat == 0) {
                    Log.d("myd", "ADD ACTIVITY addEventHandler => adding events to course " + positionForEdit);
                    events.add(new CourseEvent(i, tempHours, tempMinutes));
                    events_adapter.notifyDataSetChanged();
                    cnt++;
                }
            }
        }
        if(dcnt != 0) {
            Toast.makeText(getApplicationContext(), String.valueOf(dcnt) + " duplicate event(s)!", Toast.LENGTH_SHORT).show();
            Log.d("myd", "ADD ACTIVITY addEventHandler => there were " + dcnt + "duplicate events");
        }
        else if(cnt == 0) {
            Log.d("myd", "ADD ACTIVITY addEventHandler => no day was selected");
            Toast.makeText(getApplicationContext(), "No day selected!", Toast.LENGTH_SHORT).show();
        }
    }

    public int addCourseHandler() {
        String tempCourseName = courseName.getText().toString();
        String tempInstructorName = instructorName.getText().toString();
        String tempLectureScheduledCount = lectureScheduledCount.getText().toString();
        String tempLectureAttendedCount = lectureAttendedCount.getText().toString();
        int tlsc, tlac;
        try {
            tlsc = Integer.parseInt(tempLectureScheduledCount);
            tlac = Integer.parseInt(tempLectureAttendedCount);
        }
        catch (Exception e) {
            Log.d("myd", "ADD ACTIVITY addCourseHandler => numerical value required in lectures");
            Toast.makeText(getApplicationContext(), "Numerical Value required in lectures!", Toast.LENGTH_SHORT).show();
            return 1;
        }
        if(tempCourseName.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Course name cannot be empty!", Toast.LENGTH_SHORT).show();
            Log.d("myd", "ADD ACTIVITY addCourseHandler => Course name cannot be empty.");
            return 1;
        }
        if(tempInstructorName.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Instructor name cannot be empty!", Toast.LENGTH_SHORT).show();
            Log.d("myd", "ADD ACTIVITY addCourseHandler => Instructor name cannot be empty.");
            return 1;
        }
        if(tlsc < 0 || tlac < 0 || tlsc < tlac) {
            Log.d("myd", "ADD ACTIVITY addCourseHandler => Scheduled and Attended lectures' format incorrect.");
            Toast.makeText(getApplicationContext(), "Scheduled and Attended lectures' format incorrect!", Toast.LENGTH_SHORT).show();
            return 1;
        }

        course = new Course(tempCourseName, tempInstructorName, tlsc, tlac, events);
        for(int i = 0;i < course.events.size();i++) {
            course.events.get(i).setReminder(course.name);
            Log.d("myd", "ADD ACTIVITY addCourseHandler => reminder added for event of course " + tempCourseName);
        }
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem complete = (MenuItem)menu.findItem(R.id.complete);
        if(positionForEdit == -1) {
            complete.setTitle("Done");
            MenuItem deleteCourse = menu.findItem(R.id.delete);
            deleteCourse.setVisible(false);
            this.invalidateOptionsMenu();
            Log.d("myd", "ADD ACTIVITY onPrepareOptionsMenu => delete menu removed");
        }
        else
            complete.setTitle("Update");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.complete) {
            if(addCourseHandler() == 0) {
                Intent respondIntent = new Intent();
                respondIntent.putExtra("position", positionForEdit);
                respondIntent.putExtra("deleted", 0);
                Bundle bundleObject = new Bundle();
                bundleObject.putSerializable("courseObj", (Serializable)course);
                respondIntent.putExtras(bundleObject);
                setResult(RESULT_OK, respondIntent);
                Log.d("myd", "ADD ACTIVITY onOptionsItemSelected => course information is being sent to parent activity");
                finish();
            }
            return true;
        }
        else if(id == R.id.delete) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            yesNoSelection = 1;
                            Intent respondIntent = new Intent();
                            respondIntent.putExtra("deleted", 1);
                            setResult(RESULT_OK, respondIntent);
                            Log.d("myd", "ADD ACTIVITY onOptionsItemSelected => course deletion signal is being sent to parent activity");
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            yesNoSelection = 0;
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteEvent(int position) {
        events.remove(position);
        events_adapter.notifyDataSetChanged();
        Log.d("myd", "ADD ACTIVTY deleteEvent => deleting event " + position);
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
            Log.d("myd", "ADD ACTIVITY EventsViewAdapter CONSTRUCTOR => adapter created");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View eventView = convertView;
            CourseEvent tempEvent = events.get(position);
            if(eventView == null) {
                Log.d("myd", "ADD ACTIVITY EventsViewAdapter getView => inflating grid cell");
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                eventView = inflater.inflate(layoutResourceId, null);
            }
            ImageView clearButton = (ImageView)eventView.findViewById(R.id.clear_event);
            clearButton.setTag(position);
            clearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteEvent(Integer.valueOf(((ImageView) v).getTag().toString()));
                }
            });
            TextView tempDay = (TextView)eventView.findViewById(R.id.day);
            TextView tempTime = (TextView)eventView.findViewById(R.id.time);
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
            Log.d("myd", "ADD ACTIVITY EventsViewAdapter getView => function end");
            return eventView;
        }
    }

}
