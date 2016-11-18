package votebox.events;

import sexpression.*;

/**
 * An event to signify that a ballot print has been successful
 */
public class BallotPrintSuccessEvent extends ABallotEvent{

        /**
         * Matcher for the pinEntered message
         */
        private static MatcherRule MATCHER = new MatcherRule() {
            private ASExpression pattern = new ListExpression( StringExpression
                    .makeString("ballot-print-success"), StringWildcard.SINGLETON, StringWildcard.SINGLETON );

            public IAnnounceEvent match(int serial, ASExpression sexp) {
                ASExpression res = pattern.match( sexp );
                if (res != NoMatch.SINGLETON) {
                    String bid = ((ListExpression) res).get(0).toString();
                    ASExpression nonce = ((ListExpression) res).get( 1 );
                    byte[] doc = ((ListExpression) res).get(2).toVerbatim();
                    return new BallotPrintSuccessEvent( serial, bid, nonce, doc);
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
        }


        /**
         * @see votebox.events.ABallotEvent#ABallotEvent(int, String, sexpression.ASExpression)
         */
        public BallotPrintSuccessEvent(int serial, String bid, ASExpression nonce) {
            super(serial, bid, nonce);
        }

        public BallotPrintSuccessEvent(int serial, String bid, ASExpression nonce, byte[] doc){
            super(serial, nonce, doc, bid);
        }

        /** @see votebox.events.IAnnounceEvent#fire(VoteBoxEventListener) */
        public void fire(VoteBoxEventListener l) {
            l.ballotPrintSuccess(this);
        }

        /** @see votebox.events.IAnnounceEvent#toSExp() */
        public ASExpression toSExp() {
            return new ListExpression( StringExpression.makeString("ballot-print-success"),
                    StringExpression.makeString( getBID() ),
                    getNonce());
        }

}