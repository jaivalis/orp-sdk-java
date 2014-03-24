package nl.recsys;

import nl.shared.RecommenderItem;

import java.util.*;

public class DomainTable {

    private Stack mostRecent = new Stack<Long>();

    public boolean notificationEvent(RecommenderItem ri) {
        // check the item
        if (ri == null || ri.getItemID() == null || ri.getItemID() == 0L || ri.getDomainID() == null) {
            return false;
        }
        return true;
    }

    public boolean updateEvent(RecommenderItem ri) {
        // check the item
        if (ri.getItemID() == null || ri.getItemID() == 0L) {
            return false;
        }
        // remove and add again so it goes in front of the Stack.
        mostRecent.remove(ri.getItemID());
        mostRecent.add(ri.getItemID());
        return true;
    }

    public boolean createEvent(RecommenderItem ri) {
        // check the item
        if (ri.getItemID() == null || ri.getItemID() == 0L) {
            return false;
        }
        // remove and add again so it goes in front of the Stack.
        mostRecent.add(ri.getItemID());
        return true;
    }

    public List<Long> getMostRecent(RecommenderItem ri) {
        Integer reqs = ri.getNumberOfRequestedResults();
        Long itemID  = ri.getItemID();

        Set<Long> blackListedIDs = new HashSet<Long>();
        blackListedIDs.add(0L);
        blackListedIDs.add(itemID);

        // handle invalid values
        if (reqs == null || reqs.intValue() < 0 || reqs.intValue() > 10) {
            return new ArrayList<Long>(0);
        }
        List<Long> ret = new ArrayList<Long>();

        for (Object l : this.mostRecent) {
            ret.add((Long) l);
            if ( blackListedIDs.contains((Long) l) ) {
                continue;
            }
            if (reqs-- <= 0) {
                break;
            }
        } return ret;
    }

    public static void main(String[] args) {
        DomainTable dt = new DomainTable();

        Long l = Long.valueOf(1);
        dt.mostRecent.add( l );
        l = Long.valueOf(2);
        dt.mostRecent.add( l );
        l = Long.valueOf(12);
        dt.mostRecent.add( l );

        dt.mostRecent.remove(Long.valueOf(3));

        String res = "{" + "\"recs\": {" + "\"ints\": {" + "\"3\": "
                + dt.mostRecent + "}" + "}}";

        System.out.println(res);
    }
}
