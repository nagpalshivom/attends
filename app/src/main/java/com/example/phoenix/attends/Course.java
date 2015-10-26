package com.example.phoenix.attends;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phoenix on 18/10/15.
 */
public class Course implements Serializable {

    String name, instructor;
    int noOfLecturesScheduled, noOfLecturesAttended;
    ArrayList<CourseEvent> events;

    Course(String name, String instructor, int noOfLecturesScheduled, int noOfLecturesAttended, ArrayList<CourseEvent> events) {
        this.name = name;
        this.instructor = instructor;
        this.noOfLecturesScheduled = noOfLecturesScheduled;
        this.noOfLecturesAttended = noOfLecturesAttended;
        this.events = events;
        Log.d("myd", "COURSE CONTRUCTOR => Course " + name + " created with instructor : " + instructor + " lecture ratio : " + noOfLecturesAttended + " / " + noOfLecturesScheduled + " and event count : " + events.size());
    }
}
