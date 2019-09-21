package bgu.spl.a2;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * this class represents a deferred result i.e., an object that eventually will
 * be resolved to hold a result of some operation, the class allows for getting
 * the result once it is available and registering a callback that will be
 * called once the result is available.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 * @param <T>
 *            the result type, <boolean> resolved - initialized ;
 */
public class Promise<T>{

    private T ans;
    private boolean isRes;
    private ArrayList<callback> calls;

	public Promise() {
	    isRes = false;
	    calls = new ArrayList<>();
    }

	/**
	 *
	 * @return the resolved value if such exists (i.e., if this object has been
	 *         {@link #resolve(java.lang.Object)}ed 
	 * @throws IllegalStateException
	 *             in the case where this method is called and this object is
	 *             not yet resolved
	 */
	//we need this sync' to not return different versions of p.s
	synchronized public T get() throws IllegalStateException{

	    if(!isResolved())
	        throw  new IllegalStateException();
	    else
            return ans;
	}

	/**
	 *
	 * @return true if this object has been resolved - i.e., if the method
	 *         {@link #resolve(java.lang.Object)} has been called on this object
	 *         before.
	 */
	public boolean isResolved() {
        return isRes;
	}


	/**
	 * resolve this promise object - from now on, any call to the method
	 * {@link #get()} should return the given value
	 *
	 * Any callbacks that were registered to be notified when this object is
	 * resolved via the {@link #subscribe(callback)} method should
	 * be executed before this method returns
	 *
     * @throws IllegalStateException
     * 			in the case where this object is already resolved
	 * @param value
	 *            - the value to resolve this promise object with
	 */
	public void resolve(T value) throws IllegalStateException{

		// have to be syncronized - so every callback will be call for sure
		if(isRes)
	        throw new IllegalStateException();
	    else {
			this.ans = value;
			synchronized (this) {
				this.isRes = true;
				for (callback c : calls) {
					c.call();
				}
				calls.clear();
			}
        }
	}

	/**
	 * add a callback to be called when this object is resolved. If while
	 * calling this method the object is already resolved - the callback should
	 * be called immediately
	 *
	 * Note that in any case, the given callback should never get called more
	 * than once, in addition, in order to avoid memory leaks - once the
	 * callback got called, this object should not hold its reference any
	 * longer.
	 *
	 * @param callback
	 *            the callback to be called when the promise object is resolved
	 */
	public void subscribe(callback callback) {

		// have to be syncronize - beacuse the 'resolve' can be change by other
		boolean tryToCall = false;
		synchronized (this) {
			if (!isResolved()) {
				calls.add(callback);
			} else {
				tryToCall = true;
			}
		}
		if(tryToCall){
			callback.call();
		}
	}
}
