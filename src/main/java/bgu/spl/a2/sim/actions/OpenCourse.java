package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OpenCourse extends Action<Boolean> {

    private final String department;
    private final String courseName;
    private Integer initialSpace;
    private List<String> prerequisites;


    public OpenCourse(String department, String courseName, Integer initialSpace, List<String> prerequisites) {
        super();
        this.department = department;
        this.courseName = courseName;
        this.initialSpace = initialSpace;
        this.prerequisites = prerequisites;

        setActionName("Open Course");
    }

    public void start() {

        Action <Boolean> createCourseActor = new Action<Boolean>() {
            @Override
            protected void start() {
                setActionName("createCourseActor");
                complete(true);
            }
        };

        CoursePrivateState coursePs = new CoursePrivateState();
        coursePs.setAvailableSpots(initialSpace);
        coursePs.setPrequisites(prerequisites);
        DepartmentPrivateState departmentPs = (DepartmentPrivateState)myPool.getPrivateState(actorId);
        departmentPs.addCourse(courseName);

        // by sending this method -  we are initializing course as an actor
        sendMessage(createCourseActor, courseName, coursePs).subscribe(new callback() {
            @Override
            public void call() {
                complete(true);
            }
        });
    }
}

