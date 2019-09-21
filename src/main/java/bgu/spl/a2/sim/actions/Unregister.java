package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import java.util.ArrayList;
import java.util.List;

public class Unregister extends Action {

    private String studentId;
    private String courseName;

    public Unregister(String studentId, String courseName){
        super();
        this.studentId = studentId;
        this.courseName = courseName;

        setActionName("Unregister");
    }

    public void start(){

        CoursePrivateState coursePs = (CoursePrivateState)myPool.getPrivateState(actorId);
        StudentPrivateState studentPs = (StudentPrivateState) myPool.getPrivateState(studentId);

        //subaction that check if the grade exist in the students grades sheet. avoid concurrent exc
        Action <Boolean> checkIfGradeExists = new Action<Boolean>() {
            @Override
            protected void start() {

                setActionName("checkIfGradeExists");
                if (studentPs.getGrade(courseName) != null) {
                    complete(true);
                } else {
                    complete(false);
                }
            }

        };
        sendMessage(checkIfGradeExists, studentId, studentPs);
        List<Action<Boolean>> collection = new ArrayList<>();
        collection.add(checkIfGradeExists);

        then(collection, new callback() {
            @Override
            public void call() {
                Action<Boolean> checkIfStudentExist = new Action<Boolean>() {
                    @Override
                    protected void start() {
                        setActionName("checkIfStudentExist");
                        studentPs.removeGrade(courseName);
                        complete(true);
                    }
                };

                //check if the student exist in the course by subaction
                sendMessage(checkIfStudentExist, studentId, studentPs);
                List<Action<Boolean>> collection2 = new ArrayList<>();
                collection2.add(checkIfStudentExist);

                then(collection2, new callback() {
                    @Override
                    public void call() {
                        coursePs.removeStudent(studentId);
                        complete(true);
                    }
                });
            }
        });
    }
}