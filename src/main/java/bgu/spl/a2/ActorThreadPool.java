package bgu.spl.a2;

import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {

    private HashMap<String, PrivateState> actorsPs;
    private Map<String,ConcurrentLinkedQueue<Action>> actorsAltQueue;
    private Map<String, AtomicBoolean> isOpenMap;
    private ArrayList<Thread> threads;
    private VersionMonitor actVm;
    private final AtomicBoolean shutDown;
    private final CountDownLatch threadStillWorking;

    /**
     * creates a {@link ActorThreadPool} which has nthreads. Note, threads
     * should not get started until calling to the {@link #start()} method.
     * <p>
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this thread
     *                 pool
     */

    public ActorThreadPool(int nthreads) {

        threadStillWorking = new CountDownLatch(nthreads);
        isOpenMap = new ConcurrentHashMap<>();
        actorsAltQueue = new ConcurrentHashMap<>();
        actorsPs = new HashMap<>();
        threads = new ArrayList<>(nthreads);
        actVm = new VersionMonitor();
        shutDown = new AtomicBoolean(false);


        for (int i = 0; i < nthreads; i++) {
            final ActorThreadPool thisPool = this;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!shutDown.get()) { // while system is running and not shut down
                        int currVer = actVm.getVersion();
                        Pair<String, Action> pair = actionCollector(); // represents the actor id for a spesific action

                        // if we found an action - we will submit it(with handle)
                        // else - thread go to sleep
                            if(pair!=null && pair.getValue()!=null){
                                Action act = pair.getValue();
                                String id = pair.getKey();
                                act.handle(thisPool, id, getPrivateState(id));
                                isOpenMap.get(id).compareAndSet(false, true);
                                actVm.inc();
                        } else {
                            try {
                                actVm.await(currVer);
                            } catch (InterruptedException ignore) {}
                        }
                    }
                    threadStillWorking.countDown(); // if shut down - when thread finish his action turn off (out of scope)
                }

                //this method checks if there is action somewhere.
                // if does - return a pair that represents the actor and the action - with taking out the action from the queue (uses lock and unlock )
                // if not - return null
                private Pair<String,Action> actionCollector(){
                        for (String actorId : actorsAltQueue.keySet()) {
                            if (isOpenMap.get(actorId)!=null && isOpenMap.get(actorId).compareAndSet(true, false)) {
                                if (!actorsAltQueue.get(actorId).isEmpty()) {
                                    Pair<String, Action> output = new Pair<>(actorId, actorsAltQueue.get(actorId).poll());
                                    //actorsAltQueue.get(actorId).isOpen().set(true);
                                    return output;
                                } else {
                                    isOpenMap.get(actorId).set(true);
                                }
                            }
                        }
                    return null;
                }
            });
            threads.add(thread);
        }
    }

    /**
     * getter for actors
     *
     * @return actors
     */
    //we need this sync' to not return different versions of p.s
    public synchronized HashMap<String, PrivateState> getActors() {
        return actorsPs;
    }


    /**
     * getter for actor's private state
     *
     * @param actorId actor's id
     * @return actor's private state
     */
    //we need this sync' to not return different versions of p.s
    public synchronized PrivateState getPrivateState(String actorId) {
        return actorsPs.get(actorId);
    }


    /**
     * submits an action into an actor to be executed by a thread belongs to
     * this thread pool
     *
     * @param action     the action to execute
     * @param actorId    corresponding actor's id
     * @param actorState actor's private state (actor's information)
     */
    //we need this sync' for safe insertions
    synchronized public void submit(Action<?> action, String actorId, PrivateState actorState) {
        if (!actorsAltQueue.containsKey(actorId)) {
            actorsPs.put(actorId, actorState);
            actorsAltQueue.put(actorId, new ConcurrentLinkedQueue<>());
            isOpenMap.put(actorId, new AtomicBoolean(true));
        }
        (actorsAltQueue.get(actorId)).add(action);
        actVm.inc();
    }

    /**
     * closes the thread pool - this method interrupts all the threads and waits
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     * <p>
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is interrupted
     */
    public void shutdown() throws InterruptedException {
        shutDown.set(true);
        actVm.inc();
        threadStillWorking.await();

        }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
        for (Thread thread : threads) {
            thread.start();
        }
    }
}