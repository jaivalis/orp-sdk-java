package nl.recsys;

import nl.shared.RecommenderItem;
import org.slf4j.Logger;

import java.util.*;

public class MostRecentRecommender extends Recommender {

    private Map<Long, DomainTable> domainTableMap;

    public MostRecentRecommender(Logger logger) {
        this.logger = logger;
    }

    protected Set<Integer> getRecommendations(final String _sessionID, int n) {
        HashSet<Integer> ret = new HashSet<Integer>();

        return ret;
    }

    @Override
    public boolean notificationEvent(RecommenderItem ri) {
        if (ri == null || ri.getDomainID() == null) {
            return false;
        }
        return this.domainTableMap.get(ri.getDomainID()).notificationEvent(ri);
    }

    @Override
    public boolean updateEvent(RecommenderItem ri) {
        if (ri == null || ri.getDomainID() == null) {
            return false;
        }
        return this.domainTableMap.get(ri.getDomainID()).updateEvent(ri);
    }

    @Override
    public boolean createEvent(RecommenderItem ri) {
        if (ri == null || ri.getDomainID() == null) {
            return false;
        }
        return this.domainTableMap.get(ri.getDomainID()).createEvent(ri);
    }

    /**
     * Returns the top most recent items
     * @param ri
     * @return
     */
    @Override
    protected List<Long> getRecommendations(RecommenderItem ri) {
        Long domainID = ri.getDomainID();
        if (domainID == null) {
            return new ArrayList<Long>(0);
        }


        return this.domainTableMap.get(domainID).getMostRecent(ri);
    }

}
