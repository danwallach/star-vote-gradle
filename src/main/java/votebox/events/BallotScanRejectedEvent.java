package votebox.events;

import sexpression.*;

/**
 * An event which signals that a scanned ballot was rejected, i.e. not recognized by the supervisor
 *
 * @author Matt Bernhard
 */
public class BallotScanRejectedEvent extends AAnnounceEvent {

    private String bid;

    /**
     * The matcher for the BallotReceivedEvent.
     */
    private static MatcherRule MATCHER = new MatcherRule() {
        private ASExpression pattern = new ListExpression( StringExpression
                .makeString("ballot-rejected"), StringWildcard.SINGLETON);

        public IAnnounceEvent match(int serial, ASExpression sexp) {
            ASExpression res = pattern.match(sexp);
            if (res != NoMatch.SINGLETON) {
                String BID = ((ListExpression) res).get(0).toString();
                return new BallotScanRejectedEvent(serial,  BID );
            }

            return null;
        }
    };

    /**
     *
     * @return a MatcherRule for parsing this event type.
     */
    public static MatcherRule getMatcher(){
        return MATCHER;
    }//getMatcher

    /**
     * Constructs a new BallotReceivedEvent.
     *
     * @param bid
     *          The rejected ballot's id
     */
    public BallotScanRejectedEvent(int serial, String bid) {
        super(serial);
        this.bid = bid;
    }

    /**
     * @return the ballot id
     */
    public String getBID(){
        return bid;
    }


    /**
     * @see votebox.events.IAnnounceEvent#fire(VoteBoxEventListener)
     */
    public void fire(VoteBoxEventListener l) {
       l.ballotRejected( this );
    }

    /** @see votebox.events.IAnnounceEvent#toSExp() */
    public ASExpression toSExp() {
        return new ListExpression( StringExpression
                .makeString( "ballot-rejected" ),
                StringExpression.makeString(bid) );
    }
}
