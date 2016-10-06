/**
  * This file is part of VoteBox.
  * 
  * VoteBox is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License version 3 as published by
  * the Free Software Foundation.
  * 
  * You should have received a copy of the GNU General Public License
  * along with VoteBox, found in the root of any distribution or
  * repository containing all or part of VoteBox.
  * 
  * THIS SOFTWARE IS PROVIDED BY WILLIAM MARSH RICE UNIVERSITY, HOUSTON,
  * TX AND IS PROVIDED 'AS IS' AND WITHOUT ANY EXPRESS, IMPLIED OR
  * STATUTORY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, WARRANTIES OF
  * ACCURACY, COMPLETENESS, AND NONINFRINGEMENT.  THE SOFTWARE USER SHALL
  * INDEMNIFY, DEFEND AND HOLD HARMLESS RICE UNIVERSITY AND ITS FACULTY,
  * STAFF AND STUDENTS FROM ANY AND ALL CLAIMS, ACTIONS, DAMAGES, LOSSES,
  * LIABILITIES, COSTS AND EXPENSES, INCLUDING ATTORNEYS' FEES AND COURT
  * COSTS, DIRECTLY OR INDIRECTLY ARISING OUR OF OR IN CONNECTION WITH
  * ACCESS OR USE OF THE SOFTWARE.
 */

package auditorium;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This is an implementation of a synchronized queue (LIFO push/pop policy)
 * which holds S-Expressions. I wrote it because Java's collections API is
 * inherently confusing, and if I used Collections.synchronizedList(), my
 * intuition tells me that something dangerous might happen.<br>
 * <br>
 * This class is implemented using a java.util.LinkedList, interpreting it as a
 * java.util.Queue, and synchronizing push and pop on "this."<br>
 * <br>
 * A call to pop(), when the queue is empty, will block until something gets
 * placed in the queue.
 * 
 * @author Kyle Derr
 * 
 */
public class SynchronizedQueue<T> {

    /** The actual data structure */
    private final Queue<T> queue = new LinkedList<>();

    /** A boolean denoting if the queue has become released and can no longer receive data */
    private volatile boolean release = false;

    /**
     * Call this method to add a new S-Expression to the queue.
     * 
     * @param exp       The expression that wants to be added to the queue.
     * @return          True if the add was a success, or false otherwise.
     */
    public synchronized boolean push(T exp) {
        notifyAll();
        return queue.offer( exp );
    }

    /**
     * Call this method to remove the least recently added S-Expression from the
     * queue.
     * 
     * @return This method returns the least recently added S-Expression, or returns null if the queue is empty.
     *
     * @throws ReleasedQueueException Thrown if it deems it cannot ever get any input. This determination is made if another thread calls releaseThreads().
     */
    public synchronized T pop() throws ReleasedQueueException {
        while (queue.isEmpty() && !release) {
            try {
                wait();
            }
            catch (InterruptedException e) {
                throw new FatalNetworkException("Couldn't wait on the synchronized queue.", e);
            }
        }

        if (release)
            throw ReleasedQueueException.SINGLETON;

        return queue.poll();
    }

    /**
     * Get the number of elements that are in the queue.
     * 
     * @return This method returns the number of elements that are in the queue.
     */
    public synchronized int size() {
        return queue.size();
    }

    /**
     * If any threads are waiting on a pop operation, release them. This
     * operation is not recoverable (subsequent pop operations will not block).
     */
    public synchronized void releaseThreads() {
        release = true;
        notifyAll();
    }
}
