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

package votebox.middle.view;

import votebox.middle.IBallotVars;
import votebox.middle.Properties;
import votebox.middle.ballot.IBallotLookupAdapter;
import votebox.middle.driver.IAdapter;
import votebox.middle.view.widget.ToggleButtonGroup;

/**
 * 
 * From the perspective of the view, an IDrawable is any object which can be
 * drawn to the display. This means that it has a UniqueID (which is unique
 * across all drawable elements) and it also offers a method from which an image
 * can be gotten.
 */

public interface IDrawable {

    /**
     * From this method, the view can access the image for the IDrawable.
     * 
     * @return      the image that is associated with this drawable for the given size index.
     */
    public abstract IViewImage getImage();

    /**
     * Call this method to get the "review screen version" of any particular
     * drawable. The review screen version of a drawable is scaled differently
     * to fit a particular review screen layout. Note that this method should be
     * called on drawables that aren't, themselves, members of the review
     * screen. This method should be called by behavior in said review screen
     * drawables, in the process of determining which image represents the
     * "currently selected drawable" of any given group.
     * 
     * @return      the review screen version of any drawable.
     */
    public IViewImage getReviewImage();

    /**
     * Call this method to get the properties that were defined for this
     * drawable in the layout XML.
     * 
     * @return      the properties that were defined for this drawable in the layout XML.
     */
    public Properties getProperties();

    /**
     * Call this method to get the associated group of this drawable.
     *
     * @return      the group for this drawable
     *
     * @throws MeaninglessMethodException if this method does is not meaningful for the class.
     */
    public ToggleButtonGroup getGroup() throws MeaninglessMethodException;

    /**
     * The ViewManager will call this method on every drawable after it is
     * created. If the drawable needs to do something, it can here.
     * 
     * @param viewManagerAdapter    an adapter that can be used by the drawable
     *                              to make calls to the view manager.
     *
     * @param ballotLookupAdapter   an adapter that can be used by the drawable
     *                              to look up information in the ballot. This
     *                              adapter cannot affect the ballot's state.
     *
     * @param ballotAdapter         an adapter that can be used to change the state
     *                              of the ballot
     * @param viewFactory           a factory that can be used to construct the new image
     *
     * @param ballotVars            a container holding the path to the image file
     */
    public void initFromViewManager(IViewManager viewManagerAdapter, IBallotLookupAdapter ballotLookupAdapter,
                                    IAdapter ballotAdapter, IViewFactory viewFactory, IBallotVars ballotVars);

    /**
     * Call this method to get the x-coordinate at which this drawable should be
     * drawn.
     * 
     * @return      the x-coordinate at which this drawable should be drawn.
     */
    public int getX();

    /**
     * Call this method to set the x-coordinate at which this drawable should be
     * drawn.
     * 
     * @param x     an int that will be set as the x-coordinate at which this
     *              drawable should be drawn.
     */
    public void setX(int x);

    /**
     * Call this method to get the y-coordinate at which this drawable should be
     * drawn.
     * 
     * @return      the y-coordinate at which this drawable should be drawn.
     */
    public int getY();

    /**
     * Call this method to set the y-coordinate at which this drawable should be
     * drawn.
     * 
     * @param y     the int that will be set as the y-coordinate at which this
     *              drawable should be drawn.
     */
    public void setY(int y);

    /**
     * Call this method to get this drawable's unique ID.
     * 
     * @return      the drawable's uniqueid.
     */
    public String getUniqueID();

    /**
     * This method sets which page is the parent of this drawable.
     * 
     * @param parent    the page that will be set as the parent.
     */
    public void setParent(RenderPage parent);
}
