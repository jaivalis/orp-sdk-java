package nl.recsys;

import nl.shared.RecommenderItem;
import org.slf4j.Logger;

import java.util.*;

public class MostRecentRecommender extends Recommender {

    private Map<Long, DomainTable> domainTableMap;

    public MostRecentRecommender(Logger logger) {
        this.logger = logger;
        this.domainTableMap = new HashMap<Long, DomainTable>();
    }

    @Override
    public boolean notificationEvent(RecommenderItem ri) {
        if (ri == null || ri.getDomainID() == null) {
            return false;
        }
        Long domainID = ri.getDomainID();
        handleColdStart(domainID);
        return this.domainTableMap.get(domainID).notificationEvent(ri);
    }

    @Override
    public boolean updateEvent(RecommenderItem ri) {
        if (ri == null || ri.getDomainID() == null) {
            return false;
        }
        Long domainID = ri.getDomainID();
        handleColdStart(domainID);
        return this.domainTableMap.get(domainID).updateEvent(ri);
    }

    @Override
    public boolean createEvent(RecommenderItem ri) {
        if (ri == null || ri.getDomainID() == null) {
            return false;
        }
        Long domainID = ri.getDomainID();
        handleColdStart(domainID);
        return this.domainTableMap.get(domainID).createEvent(ri);
    }

    /**
     * Returns the top most recent items
     * @param ri
     * @return
     */
    @Override
    protected List<Long> getRecommendations(RecommenderItem ri) {
        if (ri == null || ri.getDomainID() == null) {
            return new ArrayList<Long>(0);
        }
        Long domainID = ri.getDomainID();
        handleColdStart(domainID);
        return this.domainTableMap.get(domainID).getMostRecent(ri);
    }

    private void handleColdStart(Long domainID) {
        if (this.domainTableMap.get(domainID) == null) {
            this.domainTableMap.put(domainID, new DomainTable());
        }
    }

}
