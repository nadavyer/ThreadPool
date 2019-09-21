package bgu.spl.a2;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the action result type
 */
public abstract class Action<R> {

    private Promise myPromise;
    private callback cont;
    protected PrivateState ps;
    protected ActorThreadPool myPool;
    protected String actorId;
    private String name ;
    private boolean finishAct = false;


    public Action() {
        myPromise = new Promise();
    }


    /**
     * start handling the action - note that this method is protected, a thread
     * cannot call it directly.
     */

    protected abstract void start();


    /**
     * start/continue handling the action
     * <p>
     * this method should be called in order to start this action
     * or continue its execution in the case where it has been already started.
     * <p>
     * IMPORTANT: this method is package protected, i.e., only classes inside
     * the same package can access it - you should not change it to
     * public/private/protected
     */

    /*package*/
    final void handle(ActorThreadPool pool, String actorId, PrivateState actorState) {

        this.ps = actorState;
        this.myPool = pool;
        this.actorId = actorId;

        // checking if the second time we are handeling this act
        // if yes - we should call the given callback
       if (finishAct){
           cont.call();
       }
       else{
           start();
       }
    }


    /**
     * add a callback to be executed once *all* the given actions results are
     * resolved
     * <p>
     * Implementors note: make sure that the callback is running only once when
     * all the given actions completed.
     *
     * @param actions
     * @param callback the callback to execute once all the results are resolved
     */
    protected final void then(Collection<? extends Action<?>> actions, callback callback) {

    final Action myAction = this;
    CountDownLatch calls = new CountDownLatch(actions.size());
    if (actions.isEmpty()){
        myPool.submit(myAction, actorId, ps);
    }

    for (Action action:actions) {
            action.getResult().subscribe(new callback() {
                public void call(){
                    //we need this sync' to not miss any countdown!
                    synchronized (calls) {
                        calls.countDown();
                        if (calls.getCount() == 0) { // if we finished the given actions - we change the callcack to be the asked one, and submit again.
                            finishAct = true;
                            cont = callback;
                            myPool.submit(myAction, actorId, ps);
                        }
                    }
                }
            });
        }
    }

    /**
     * resolve the internal result - should be called by the action derivative
     * once it is done.
     *
     * @param result - the action calculated result
     */
    protected final void complete(R result) {
        ps.addRecord(getActionName());
        myPromise.resolve(result);
    }

    /**
     * @return action's promise (result)
     */
    public final Promise<R> getResult() {
        return myPromise;
    }

    /**
     * send an action to an other actor
     *
     * @param action     the action
     * @param actorId    actor's id
     * @param actorState actor's private state (actor's information)
     * @return promise that will hold the result of the sent action
     */


    public Promise<?> sendMessage(Action<?> action, String actorId, PrivateState actorState) {
        myPool.submit(action, actorId, actorState);
        return action.getResult();
    }


    /**
     * set action's name
     *
     * @param actionName
     */
    public void setActionName(String actionName) {
        this.name = actionName;
    }

    /**
     * @return action's name
     */
    public String getActionName() {
        return name;
    }

}
