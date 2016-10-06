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

package votebox.middle.ballot;

import votebox.middle.driver.DeselectionException;
import votebox.middle.driver.UnknownUIDException;

/**
 * 
 * This is the strategy implementation for radio button voting. In radio button
 * voting, only one candidate can be selected in a race. Once a candidate is
 * selected, he can either be explicitly deselected, by being toggled, or he can
 * be implicitly deselected when the voter chooses another candidate in the
 * race. Selecting any candidate in any race implicitly deselects all other
 * candidates in the race.
 */

public class RadioButton extends ACardStrategy {

	/**
	 * Singleton Design Pattern
	 */
	public static final RadioButton Singleton = new RadioButton();

	/**
	 * Singleton Design Pattern
	 */
	private RadioButton() {
	}

	/**
	 * When a SelectableCardElement has decided that it would like to be
	 * selected, it delegates to this method.
	 * 
	 * @param element This element has chosen to be selected.
     *
	 * @throws CardStrategyException if the view errors during deselection or
     *                               an element doesn't exist during deselection
	 */
	public boolean select(SelectableCardElement element) throws CardStrategyException {

		/* Deselect everyone else except the selection */
		for (SelectableCardElement ce : element.getParentCard().getElements())
			if (ce != element)
				try { ce.getParentCard().getParent().getViewAdapter().deselect(ce.getUniqueID(), false); }
                catch (UnknownUIDException e)  { throw new CardStrategyException("A RadioButton strategy wants to deselect an element in the view that does not exist.", e); }
                catch (DeselectionException e) { throw new CardStrategyException("An error occurred in the view while trying to deselect an element: " + e.getMessage(), e); }

        /* Select the new guy. */
		element.setSelected(true);

        /* TODO WHY BOOLEANS NESTED EVERYWHERE */
		return true;
	}

	/**
	 * This method will only get called when the element is CURRENTLY
	 * selected. Toggling a currently selected element in RadioButton mode
	 * should result in the button being deselected.
	 * 
	 * @param element This element has chosen to be deselected.
	 */
	public boolean deselect(SelectableCardElement element) {
        /*TODO WHY TRUE WHY */
		element.setSelected(false);
		return true;
	}
}