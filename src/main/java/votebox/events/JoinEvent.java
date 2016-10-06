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

package votebox.events;

/**
 * An event that is fired whenever a connection to this machine is established,
 * regardless of who initiated it.  This event is not an IAnnounceEvent and has
 * no SExpression form.
 * @author Corey Shaw
 */
public class JoinEvent{

    /** the serial of the machine that is joining */
	private int serial;

    /** The election keyword to prevent unauthorized machines from joining */
    //private String keyword;

    /**
     * Constructs a new JoinEvent.
     * @param serial the serial number of the joining machine
     */
	public JoinEvent(int serial/* String keyword*/) {
		super();
        //this.keyword = keyword;
		this.serial = serial;
	}

	/**
	 * @return the serial number of the joining machine
	 */
	public int getSerial() {
		return serial;
	}


    /**
     * @return the keyword from the joining machine
     */
    //public String getKeyword(){ return keyword; }

}
