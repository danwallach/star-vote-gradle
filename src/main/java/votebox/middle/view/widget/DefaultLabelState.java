package votebox.middle.view.widget;

import votebox.middle.view.IViewImage;

/**
 * @author Matt Bernhard
 * @version 0.0.1
 *          Date: 7/25/13
 */
public class DefaultLabelState extends ALabelState{

    /**
     * Singleton Design Pattern.
     *
     */
    public static DefaultLabelState Singleton = new DefaultLabelState();

    /**
     * Singleton Design Pattern
     */
    private DefaultLabelState() {}

    /**
     * When the Button wishes to be focused, switch to the focused state
     */
    public void focus(FocusableLabel context) {
        context.setState(FocusedLabelState.Singleton);
        context.getFocusedEvent().notifyObservers();
    }

    /**
     * When the Button wishes to be unfocused, do nothing, it is already
     * unfocused.
     */
    public void unfocus(FocusableLabel context) { }

    /**
     * @see votebox.middle.view.widget.AButtonState#getImage(votebox.middle.view.widget.Button)
     */
    public IViewImage getImage(FocusableLabel context) {
        return context.getDefaultImage();
    }
}
