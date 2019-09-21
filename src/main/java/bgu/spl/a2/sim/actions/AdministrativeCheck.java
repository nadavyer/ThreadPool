package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdministrativeCheck extends Action {

    private String department;
    private List<String> students;
    private String computerType;
    private List<String> conditions;
    private Warehouse warehouse;


    public AdministrativeCheck(String department, List<String> students, String computerType, List<String> conditions, Warehouse warehouse) {
        super();
        this.department = department;
        this.students = students;
        this.computerType = computerType;
        this.conditions = conditions;
        this.warehouse = warehouse;

        setActionName("Administrative Check");
    }

    public void start() {

        DepartmentPrivateState departmentPs = (DepartmentPrivateState) myPool.getPrivateState(department);
        Promise<Computer> myPromise = warehouse.acquireComputer(computerType);
        List<Action<Boolean>> collecion = new ArrayList<>();

        myPromise.subscribe(new callback() {
            @Override
            public void call() {

                        for (String studentId : students) {
                            StudentPrivateState studentPs = (StudentPrivateState) myPool.getPrivateState(studentId);

                            HashMap<String,Integer> mapi = new HashMap<>();
                            for(String condi : conditions){
                                mapi.put(condi,studentPs.getGrade(condi));
                            }

                            long sig = myPromise.get().checkAndSign(conditions, mapi);

                            // create this action for concurrent errors
                            Action<Boolean> signStudent = new Action<Boolean>() {
                                @Override
                                protected void start() {
                                    setActionName("signStudent");
                                    studentPs.setSignature(sig);
                                    complete(true);
                                }
                            };
                            sendMessage(signStudent, studentId,studentPs);
                            collecion.add(signStudent);
                        }
            }
        });

        then(collecion, new callback() {
            @Override
            public void call() {
                complete(true);
            }
        });
        warehouse.getMutexes().get(computerType).up();
    }
}