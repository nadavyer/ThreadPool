package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import java.util.ArrayList;
import java.util.List;

public class CloseCourse extends Action {

    private String courseName;
    private String department;


    public CloseCourse(String courseName, String department){
        super();
        this.courseName=courseName;
        this.department = department;

        setActionName("Close A Course");
    }

    public void start(){

         DepartmentPrivateState departmentPs = (DepartmentPrivateState) myPool.getPrivateState(actorId);
         CoursePrivateState coursePs = (CoursePrivateState)myPool.getPrivateState(courseName);

         Action<Boolean> closeAndchangeSpots = new Action<Boolean>() {
            @Override
            protected void start() {
                setActionName("closeAndchangeSpots");
                coursePs.setAvailableSpots(-1);
                complete(true);

            }
        };
        // By sending this messege - we are changing the availble spots to "-1" so other student wont be register
        sendMessage(closeAndchangeSpots, courseName, coursePs);
        List<Action<Boolean>> collection2 = new ArrayList();
        collection2.add(closeAndchangeSpots);

        then(collection2, new callback() {
            @Override
            public void call() {

                List<Action<Boolean>> collection = new ArrayList();

                // we are making a list of unregister actions - for every student who is current participate in the course
                List<String> students = new ArrayList<>(coursePs.getRegStudents());

                for (String student : students) {
                    Unregister unregisterStudent = new Unregister(student, courseName);
                    collection.add(unregisterStudent);
                    sendMessage(unregisterStudent, courseName, coursePs);
                }



                //after all students in the course were unregister remove the course
                        departmentPs.removeCourse(courseName);
                        coursePs.setAvailableSpots(-1);
                        complete(true);
            }});

    }
}
