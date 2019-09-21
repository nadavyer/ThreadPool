package bgu.spl.a2.sim.privateStates;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.a2.PrivateState;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState{

	private Integer availableSpots;
	private Integer registered;
	private List<String> regStudents;
	private List<String> prequisites;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public CoursePrivateState() {
		super();
		regStudents = new ArrayList<>();
		prequisites = new ArrayList<>();
		this.registered=0;

	}

	public Integer getAvailableSpots() {
		return availableSpots;
	}

	public Integer getRegistered() {
		return registered;
	}

	public List<String> getRegStudents() {
		return regStudents;
	}

	public List<String> getPrequisites() {
		return prequisites;
	}

	public void setAvailableSpots(Integer availableSpots){
		this.availableSpots = availableSpots;
	}

	public void setRegistered(Integer registered){
		this.registered=registered;
	}

	public void setPrequisites(List<String> prequisites){
		this.prequisites=prequisites;
	}



	public void addStudent(String studentId){
		if(availableSpots!=-1 && !regStudents.contains(studentId)) {
			registered++;
			availableSpots--;
			regStudents.add(studentId);
		}
	}

	public void removeStudent(String studentId){
		if(regStudents.contains(studentId)) {
			getRegStudents().remove(studentId);
			if(availableSpots != -1) {
				setAvailableSpots(availableSpots + 1);
			}
			setRegistered(registered - 1);
		}
	}
}
