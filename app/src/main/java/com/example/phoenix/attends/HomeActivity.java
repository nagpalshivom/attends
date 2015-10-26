package com.example.phoenix.attends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    GridView courses_view;
    ArrayList<Course> courses;
    ArrayAdapter<Course> courses_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_custom2);
        int fileExists = 0;
        File coursesFile;
        CourseEvent.context = this;
        try {
            coursesFile = new File(getApplicationContext().getFilesDir(), "courses.ser");
            if(coursesFile.exists())
                fileExists = 1;
        }
        catch (Exception e) {
            Log.d("myd","HOME ACTIVITY onCreate => " + e.toString());
        }
        if(fileExists == 1) {
            Log.d("myd","HOME ACTIVITY onCreate => courses object extracted from file");
            try {
                FileInputStream fin = getApplicationContext().openFileInput("courses.ser");
                ObjectInputStream ois = new ObjectInputStream(fin);
                courses = (ArrayList<Course>) ois.readObject();
            } catch (Exception e) {
                Log.d("myd","HOME ACTIVITY onCreate => " + e.toString());
            }
        }
        else {
            Log.d("myd","HOME ACTIVITY onCreate => empty file created for first time");
            courses = new ArrayList<Course>();
            try {
                FileOutputStream fout = openFileOutput("courses.ser", Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(courses);
                fout.close();
            }
            catch(Exception e) {
                Log.d("myd","HOME ACTIVITY onCreate => " + e.toString());
            }
        }
        courses_view = (GridView)findViewById(R.id.courses_window);
        courses_adapter = new CoursesViewAdapter(this, R.layout.courses_grid_view, courses);
        courses_view.setAdapter(courses_adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            FileOutputStream fout = openFileOutput("courses.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(courses);
            fout.close();
            Log.d("myd","HOME ACTIVITY onStop => file written");
        }
        catch(Exception e) {
            Log.d("myd","HOME ACTIVITY onStop => " + e.toString());
        }
    }

    public void startAddActivity() {
        Intent i = new Intent(this, AddActivity.class);
        Log.d("myd", "HOME ACTIVITY startAddActivity => starting add activity");
                startActivityForResult(i, 10);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        Course course;
        Bundle receiverBundle;
        if(resultCode == RESULT_OK) {
            receiverBundle = i.getExtras();
            if (requestCode == 10) {
                Log.d("myd", "HOME ACTIVITY onActivityResult => new course added");
                course = (Course) receiverBundle.getSerializable("courseObj");
                courses.add(course);
                courses_adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "New Course " + course.name + " added!", Toast.LENGTH_SHORT).show();
            }
            else if (requestCode == 1994) {
                course = (Course) receiverBundle.getSerializable("courseObj");
                int position = i.getIntExtra("position", 0);
                int deleted = i.getIntExtra("deleted", 0);
                if(deleted == 0) {
                    for (int j = 0; j < courses.get(position).events.size(); j++)
                        courses.get(position).events.get(j).removeReminder();
                    courses.set(position, course);
                    Log.d("myd", "HOME ACTIVITY onActivityResult => Course " + course.name + " updated.");
                    Toast.makeText(getApplicationContext(), "Course " + course.name + " updated!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Course " + courses.get(position).name + " deleted!", Toast.LENGTH_SHORT).show();
                    Log.d("myd", "HOME ACTIVITY onActivityResult => Course " + courses.get(position).name + " deleted");
                    for(int j = 0;j < courses.get(position).events.size();j++)
                        courses.get(position).events.get(j).removeReminder();
                    courses.remove(position);
                }
                courses_adapter.notifyDataSetChanged();
            }
        }
    }

    public void startEditActivity(int position) {
        Log.d("myd", "HOME ACTIVITY startEditActivity => starting EditActivity");
        Intent editActivityIntent = new Intent(this, AddActivity.class);
        editActivityIntent.putExtra("position", position);
        Bundle editCourseBundle = new Bundle();
        editCourseBundle.putSerializable("courseObj", courses.get(position));
        editActivityIntent.putExtras(editCourseBundle);
        startActivityForResult(editActivityIntent, 1994);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    public void startScheduleActivity() {
        Log.d("myd", "HOME ACTIVITY startScheduleActivity => starting ScheduleActivity");
        Intent scheduleIntent = new Intent(this, ScheduleActivity.class);
        Bundle coursesBundle = new Bundle();
        coursesBundle.putSerializable("coursesObj", courses);
        scheduleIntent.putExtras(coursesBundle);
        startActivity(scheduleIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_course) {
            startAddActivity();
            return true;
        }
        else if(id == R.id.see_schedule) {
            startScheduleActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewCourseDetails(int position) {
        Log.d("myd", "HOME ACTIVITY viewCourseDetails => starting ViewActivity");
        Intent viewCourseIntent = new Intent(this, ViewActivity.class);
        viewCourseIntent.putExtra("courseObj", courses.get(position));
        startActivity(viewCourseIntent);
    }

    public class CoursesViewAdapter extends ArrayAdapter<Course> {
        Context context;
        int layoutResourceId;
        ArrayList<Course> courses;
        CoursesViewAdapter(Context context, int layoutResourceId, ArrayList<Course> courses) {
            super(context, layoutResourceId, courses);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.courses = courses;
            Log.d("myd", "HOME ACTIVITY CoursesViewAdapter CONSTRUCTOR => constructor");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cell = convertView;
            Course course = courses.get(position);
            if(cell == null) {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                cell = inflater.inflate(layoutResourceId, parent, false);
            }
            ImageView attendedInc = (ImageView)cell.findViewById(R.id.attended_lectures_inc);
            ImageView attendedDec = (ImageView)cell.findViewById(R.id.attended_lectures_dec);
            ImageView scheduledInc = (ImageView)cell.findViewById(R.id.scheduled_lectures_inc);
            ImageView scheduledDec = (ImageView)cell.findViewById(R.id.scheduled_lectures_dec);
            attendedInc.setTag(position);
            attendedDec.setTag(position);
            scheduledInc.setTag(position);
            scheduledDec.setTag(position);
            attendedInc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = Integer.valueOf(((ImageView) v).getTag().toString());
                    if (courses.get(position).noOfLecturesAttended < courses.get(position).noOfLecturesScheduled) {
                        courses.get(position).noOfLecturesAttended++;
                        courses_adapter.notifyDataSetChanged();
                    }
                }
            });
            attendedDec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = Integer.valueOf(((ImageView) v).getTag().toString());
                    if (courses.get(position).noOfLecturesAttended > 0) {
                        courses.get(position).noOfLecturesAttended--;
                        courses_adapter.notifyDataSetChanged();
                    }
                }
            });
            scheduledInc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = Integer.valueOf(((ImageView) v).getTag().toString());
                    courses.get(position).noOfLecturesScheduled++;
                    courses_adapter.notifyDataSetChanged();
                }
            });
            scheduledDec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = Integer.valueOf(((ImageView) v).getTag().toString());
                    if (courses.get(position).noOfLecturesScheduled > 0 && courses.get(position).noOfLecturesAttended < courses.get(position).noOfLecturesScheduled) {
                        courses.get(position).noOfLecturesScheduled--;
                        courses_adapter.notifyDataSetChanged();
                    }
                }
            });
            TextView course_name = (TextView)cell.findViewById(R.id.course_name);
            course_name.setTag(position);
            course_name.setPaintFlags(course_name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            TextView viewCourse = (TextView) cell.findViewById(R.id.view_course);
            course_name.setText(course.name);
            viewCourse.setTag(position);
            viewCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = Integer.valueOf(((TextView) v).getTag().toString());
                    viewCourseDetails(position);
                }
            });
            TextView lecturesAttended = (TextView)cell.findViewById(R.id.attended_lectures);
            TextView lecturesScheduled = (TextView)cell.findViewById(R.id.scheduled_lectures);
            lecturesAttended.setText("Attended : " + String.valueOf(course.noOfLecturesAttended));
            lecturesScheduled.setText("Scheduled : " + String.valueOf(course.noOfLecturesScheduled));
            TextView editCourse = (TextView)cell.findViewById(R.id.edit_course);
            editCourse.setTag(position);
            editCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = Integer.valueOf(((TextView) v).getTag().toString());
                    startEditActivity(position);
                }
            });
            Log.d("myd", "HOME ACTIVITY getView  => finish");
            return cell;
        }
    }

}
