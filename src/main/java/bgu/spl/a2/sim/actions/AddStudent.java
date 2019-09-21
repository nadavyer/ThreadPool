package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import java.util.ArrayList;
import java.util.List;

public class AddStudent extends Action <Boolean>{

    private String studentId;

    public AddStudent(String studentId){
        super();
        this.studentId = studentId;
        setActionName("Add Student");
    }

    @Override
    public void start(){

        StudentPrivateState studentPs = new StudentPrivateState();

        // by sending this action -  we are initializing student as an actor avoid concurrent exc
        Action<Boolean> makeStudent = new Action<Boolean>() {
            @Override
            protected void start() {
                setActionName("makeStudent");
                complete(true);
            }
        };
        sendMessage(makeStudent, studentId, studentPs);

        List<Action<Boolean>> collection = new ArrayList<>();
        collection.add(makeStudent); //use for then

        // after student created - insert to department
        then(collection, new callback() {
            @Override
            public void call() {
                complete(true);
                DepartmentPrivateState departmentPs = (DepartmentPrivateState)myPool.getPrivateState(actorId);
                departmentPs.addStudent(studentId);

            }
        });

    }
}
