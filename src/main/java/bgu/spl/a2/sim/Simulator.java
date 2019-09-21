/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.JsonFiles.JsonActs;
import bgu.spl.a2.sim.JsonFiles.JsonComp;
import bgu.spl.a2.sim.JsonFiles.JsonObjs;
import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	private static Warehouse warehouse;
	public static ActorThreadPool actorTP;
	private static CountDownLatch actionsPerPahse;


	/**
	 * Begin the simulation Should not be called before attachActorThreadPool()
	 */
	public static void start(){
			actorTP.start();
	}

	/**
	 * attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	 *
	 * @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	 */
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool){
		actorTP = myActorThreadPool;
	}

	/**
	 * shut down the simulation
	 * returns list of private states
	 */
	public static HashMap<String,PrivateState> end() {
		try {
			actorTP.shutdown();
		} catch (InterruptedException exc){
			exc.printStackTrace();
		}
		return actorTP.getActors();
	}


	public static int main(String [] args){
		try {
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new FileReader(args[0]));
			JsonObjs json = gson.fromJson(reader, JsonObjs.class);

			// initial the threads in ActorThreadPool
			attachActorThreadPool(new ActorThreadPool(json.getThreads().intValue()));

			// This section initialize the wareHouse.
			ArrayList<String> pcToWh = new ArrayList<>();
			for (JsonComp jsonComputer : json.getComputers()) {
				pcToWh.add(jsonComputer.getCompType());
			}
			warehouse = new Warehouse(pcToWh);
			for (JsonComp jsonComputer : json.getComputers()) {
				warehouse.getComputers().get(jsonComputer.getCompType()).setSuccessSig(jsonComputer.getSuccessSig());
				warehouse.getComputers().get(jsonComputer.getCompType()).setFailSig(jsonComputer.getFailSig());
			}

			start();

			// phase 1

			actionsPerPahse = new CountDownLatch(json.getPhase1().size());
			if (actionsPerPahse.getCount() != 0) {
				for (JsonActs jsonAct: json.getPhase1()) {
					initAction(jsonAct);
				}

				try {
					actionsPerPahse.await();//we dont start phase 2 before phase 1 is completed - countDown for phase1 actions
					System.out.println("pase 1 done");
					actionsPerPahse = new CountDownLatch(json.getPhase2().size());
					if(actionsPerPahse.getCount()!=0) {
						for (JsonActs jsonAct: json.getPhase2()) {
							initAction(jsonAct);
						}

						try {
							actionsPerPahse.await(); //we dont start phase 3 before phase 1 is completed - countDown for phase2 actions
							System.out.println("pase 2 done");
							actionsPerPahse = new CountDownLatch(json.getPhase3().size());
							if (actionsPerPahse.getCount()!=0) {
								for (JsonActs jsonAct: json.getPhase3()) {
									initAction(jsonAct);
								}
								try {
									actionsPerPahse.await(); //we dont end the system before phase 3 is finished
									System.out.println("pase 3 done");
									HashMap<String, PrivateState> ans = end();
									FileOutputStream fout = new FileOutputStream("result.ser");
									try {
										ObjectOutputStream oos = new ObjectOutputStream(fout);
										oos.writeObject(ans);

									}catch (IOException exc5) {
										exc5.printStackTrace();
									}
								}catch (InterruptedException exc4) {
									exc4.printStackTrace();
								}
							}else {
								end();
							}
						}catch (InterruptedException exc3) {
							exc3.printStackTrace();
						}

					}else{
						end();
					}
				}catch (InterruptedException exc2) {
					exc2.printStackTrace();
				}

			}else {
				end();
			}
			end();
		}catch (FileNotFoundException exc){
			exc.printStackTrace();
		}


	return 0;
	}

	private static void initAction(JsonActs jsonAct){

		String actionName = jsonAct.getActionName();
		Action action;
		String actorId;
		PrivateState privateState;

		switch (actionName) {

			case "Open Course": {
				action = new OpenCourse(jsonAct.getDepartmentName(), jsonAct.getCourseName(), jsonAct.getSpace(), jsonAct.getPrerequisites());
				actorId = jsonAct.getDepartmentName();
				privateState = actorTP.getPrivateState(actorId);
				if (privateState == null) {
					privateState = new DepartmentPrivateState();
				}

				break;
			}

			case "Add Student": {
				action = new AddStudent(jsonAct.getStudentId());
				actorId = jsonAct.getDepartmentName();
				privateState = actorTP.getPrivateState(actorId);
				if (privateState == null) {
					privateState = new DepartmentPrivateState();
				}
				break;
			}
			case "Participate In Course": {
				action = new ParticipateInCourse(jsonAct.getStudentId(), jsonAct.getCourseName(), jsonAct.getGrade());
				actorId = jsonAct.getCourseName();
				privateState = actorTP.getPrivateState(actorId);
				if (privateState == null) {
					privateState = new CoursePrivateState();
				}
				break;
			}
			case "Unregister": {
				action = new Unregister(jsonAct.getStudentId(), jsonAct.getCourseName());
				actorId = jsonAct.getCourseName();
				privateState = actorTP.getPrivateState(actorId);
				if (privateState == null) {
					privateState = new CoursePrivateState();
				}
				break;
			}
			case "Administrative Check": {
				action = new AdministrativeCheck(jsonAct.getDepartmentName(), jsonAct.getStudents(), jsonAct.getComputerType(), jsonAct.getConditions(), warehouse);
				actorId = jsonAct.getDepartmentName();
				privateState = actorTP.getPrivateState(actorId);
				if (privateState == null) {
					privateState = new DepartmentPrivateState();
				}
				break;
			}
			case "Add Spaces": {
				action = new AddSpaces(jsonAct.getCourseName(), jsonAct.getPlacesToAdd());
				actorId = jsonAct.getCourseName();
				privateState = actorTP.getPrivateState(actorId);
				if (privateState == null) {
					privateState = new CoursePrivateState();
				}
				break;
			}
			case "Register With Preferences": {
				action = new RegisterWithPreferences(jsonAct.getStudentId(), jsonAct.getPreferences(), jsonAct.getGrade());
				actorId = jsonAct.getStudentId();
				privateState = actorTP.getPrivateState(actorId);
				if (privateState == null) {
					privateState = new StudentPrivateState();
				}
				break;
			}
			default: {
				// If default - have to Close Course
				action = new CloseCourse(jsonAct.getCourseName(), jsonAct.getDepartmentName());
				actorId = jsonAct.getDepartmentName();
				privateState = actorTP.getPrivateState(actorId);
				if (privateState == null) {
					privateState = new DepartmentPrivateState();
				}
			}

		}

		action.getResult().subscribe(new callback() {
			@Override
			public void call() {
				actionsPerPahse.countDown();
			}
		});
		actorTP.submit(action, actorId, privateState);
	}
}