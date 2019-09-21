package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

public class AddSpaces extends Action{

    private String courseName;
    private Integer placesToAdd;

    public AddSpaces(String courseName, String placesToAdd){
        super();
        this.courseName = courseName;

        if(placesToAdd.equals("-")){
            this.placesToAdd=-1;
        }
        else{
            this.placesToAdd = Integer.valueOf(placesToAdd);
        }
        setActionName("Add Spaces");
    }
    public void start(){

        //set the new space in the course
        CoursePrivateState coursePs = (CoursePrivateState)ps;
        coursePs.setAvailableSpots(coursePs.getAvailableSpots() + placesToAdd.intValue());
        complete(true);
    }
}
