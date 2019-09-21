package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RegisterWithPreferences extends Action <Boolean> {

    private String studentId;
    private List<String> preferences;
    private List<String> grades;

    private Iterator<String> iterCourseName;
    private Iterator<String > iterGrade;


    public RegisterWithPreferences(String studentId, List<String> preferences, List<String> grades){
        super();
        this.studentId = studentId;
        this.preferences = preferences;
        this.grades = grades;
        this.iterCourseName = preferences.iterator();
        this.iterGrade = grades.iterator();

        setActionName("Register With Prefernces");
    }

    public void start() {

        //go through the course list and the grades of each student and check if can register by order. when registered one of the students, return.
        if(iterCourseName.hasNext() && iterGrade.hasNext()) {

            List<String> list = new ArrayList<>();
            list.add(iterGrade.next());
            String courseName = iterCourseName.next();

            final ParticipateInCourse tryToRegister = new ParticipateInCourse(studentId, courseName, list);
            CoursePrivateState coursePs = (CoursePrivateState)myPool.getPrivateState(courseName);

            //using subaction to try the course registration. avoiding concurrent exc.
            sendMessage(tryToRegister, courseName, coursePs).subscribe(new callback() {
                @Override
                public void call() {
                    if(tryToRegister.getResult().get()) {
                        complete(true);
                    }
                    else {
                        start();
                    }
                }
            });
        }
        else {
            complete(false);
        }
    }
}

