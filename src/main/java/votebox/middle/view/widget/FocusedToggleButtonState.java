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

package votebox.middle.view.widget;

import votebox.middle.view.IViewImage;

/**
 * This class represents the focused state of a ToggleButton.
 * 
 * @author Kyle
 * 
 */
public class FocusedToggleButtonState extends AToggleButtonState {

	/**
	 * Singleton Design Pattern
	 */
	public static final FocusedToggleButtonState Singleton = new FocusedToggleButtonState();
	
	/**
	 * Singleton Design Pattern
	 */
	private FocusedToggleButtonState(){
	}
	
	/**
	 * @see votebox.middle.view.widget.AToggleButtonState#getImage(votebox.middle.view.widget.ToggleButton)
	 */
	@Override
	public IViewImage getImage(ToggleButton context){
		return context.getFocusedImage();
	}

	/**
	 * @see votebox.middle.view.widget.AToggleButtonState#select(ToggleButton) TODO check this was #toggle
	 */
	@Override
	public void select(ToggleButton context) {
		context.getGroup().select(context);
	}

	/**
	 * @see votebox.middle.view.widget.AToggleButtonState#makeSelected(ToggleButton) TODO check this wsa #select
	 */
	@Override
	public void makeSelected(ToggleButton context) {
		context.setState(FocusedSelectedToggleButtonState.Singleton);
		context.getSelectedEvent().notifyObservers();
	}

	/**
	 * @see votebox.middle.view.widget.AToggleButtonState#makeDeselected(ToggleButton) TODO check this was #deselect
	 */
	@Override
	public void makeDeselected(ToggleButton context) { }

	/**
	 * @see votebox.middle.view.widget.AToggleButtonState#focus(votebox.middle.view.widget.ToggleButton)
	 */
	@Override
	public void focus(ToggleButton context) { }

	/**
	 * @see votebox.middle.view.widget.AToggleButtonState#unfocus(votebox.middle.view.widget.ToggleButton)
	 */
	@Override
	public void unfocus(ToggleButton context) {
		context.setState(DefaultToggleButtonState.Singleton);
		context.getUnfocusedEvent().notifyObservers();
	}

}
