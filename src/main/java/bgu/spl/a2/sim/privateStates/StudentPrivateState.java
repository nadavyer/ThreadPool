package bgu.spl.a2.sim.privateStates;

import java.util.HashMap;

import bgu.spl.a2.PrivateState;

/**
 * this class describe student private state
 */
public class StudentPrivateState extends PrivateState{

	private HashMap<String, Integer> grades;
	private long signature;
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public StudentPrivateState() {
		super();
		grades = new HashMap<>();
	}

	synchronized public Integer getGrade(String courseName){
		if(grades.containsKey(courseName)){
			return grades.get(courseName);
		}
		else
			return -1;
	}

	synchronized public void setGrade(String course, Integer grade){
		grades.put(course, grade);
	}

	synchronized public void removeGrade(String course){
		if(grades.containsKey(course)){
			grades.remove(course);
		}
	}

	public HashMap<String, Integer> getGrades() {
		return grades;
	}

	public long getSignature() {
		return signature;
	}

	public void setSignature(long signature) {
		this.signature = signature;
	}
}
