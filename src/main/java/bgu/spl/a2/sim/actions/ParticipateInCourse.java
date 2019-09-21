package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ParticipateInCourse extends Action <Boolean> {

    private String studentId;
    private String courseName;
    private String grade;


    public ParticipateInCourse(String studentId, String courseName, List<String> grade) {
        super();
        this.studentId = studentId;
        this.courseName = courseName;
        this.grade = grade.get(0);

        setActionName("Participate In Course");
    }

    public void start() {

        CoursePrivateState coursePs = (CoursePrivateState) myPool.getPrivateState(actorId);
        List<String> prequisites = coursePs.getPrequisites();

        StudentPrivateState studentPs = (StudentPrivateState) myPool.getPrivateState(studentId);


        //we create subaction that checks if the student meed the prequisites for the course
        Action<Boolean> checkPrerequisites = new Action<Boolean>() {
            @Override
            protected void start() {
                setActionName("checkPrerequisites");
                for (String courseToCheck : prequisites) {
                    if (!studentPs.getGrades().containsKey(courseToCheck)) {
                        complete(false);
                        return;
                    }
                }
                complete(true);
            }
        };


        sendMessage(checkPrerequisites, studentId, studentPs);
        List<Action<Boolean>> collection = new ArrayList<>();
        collection.add(checkPrerequisites);

        //if the student meets the prequisties and the course is not closed it sign the student to the course
        then(collection, new callback() {
            @Override
            public void call() {
                if (checkPrerequisites.getResult().get()) {
                    if (coursePs.getAvailableSpots() == 0) {
                        complete(false);
                        return;
                        //checks if the course is closed
                    } else if (coursePs.getAvailableSpots() == -1) {
                        complete(false);
                        return;
                        // check if student passed prerequisites
                    } else {
                        coursePs.addStudent(studentId);

                        //we create this subaction to add the grade for the student. try to avoid concurrent exc.
                        Action<Boolean> addGrade = new Action<Boolean>() {
                            @Override
                            protected void start() {
                                setActionName("addGrade");
                                studentPs.setGrade(courseName,Integer.valueOf(grade));
                                complete(true);
                            }
                        };

                        sendMessage(addGrade, studentId, studentPs);
                        List<Action<Boolean>> collection3 = new ArrayList<>();
                        collection3.add(addGrade);

                        then(collection3, new callback() {
                            @Override
                            public void call() {
                                complete(true);
                            }
                        });
                    }
                } else {
                    complete(false);
                }
            }
        });
    }
}
