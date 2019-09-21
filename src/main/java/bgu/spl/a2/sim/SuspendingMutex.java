package bgu.spl.a2.sim;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Warehouse;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * 
 * Note: this class can be implemented without any synchronization. 
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 *
 */
public class SuspendingMutex {

	private ConcurrentLinkedQueue<Promise<Computer>> promiseQueue;
	private AtomicBoolean isFree;
	private Computer myComputer;


	public SuspendingMutex(Computer computer){
		promiseQueue = new ConcurrentLinkedQueue<>();
		isFree = new AtomicBoolean(true);
		this.myComputer=computer;
	}



	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 *
	 * @return a promise for the requested computer
	 */
	public Promise<Computer> down(){

		Promise<Computer> outputPromise = new Promise<>();

		if(isFree.compareAndSet(true, false)){
			outputPromise.resolve(myComputer);
        }
        else {
        	promiseQueue.add(outputPromise);
        }
        return  outputPromise;
	}


	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public void up(){

		if(!promiseQueue.isEmpty()){
			promiseQueue.poll().resolve(myComputer);
		}
		else{
			isFree.set(true);
		}
	}

	public AtomicBoolean getIsFree() {
		return isFree;
	}

	public void setIsFree(boolean isFree) {
		this.isFree.set(isFree);
	}
}
