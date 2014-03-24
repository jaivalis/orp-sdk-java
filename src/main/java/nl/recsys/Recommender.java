package nl.recsys;

import nl.shared.RecommenderItem;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public abstract class Recommender {

    protected static Logger logger;

    public abstract boolean notificationEvent(RecommenderItem ri);
    public abstract boolean updateEvent(RecommenderItem ri);
    public abstract boolean createEvent(RecommenderItem ri);

    protected abstract List<Long> getRecommendations(RecommenderItem ri);

    public String getRecommendationJSON(RecommenderItem ri) {
        List<Long> recs = getRecommendations(ri);

        return "{" + "\"recs\": {" + "\"ints\": {" + "\"3\": " + recs + "}" + "}}";
    }

}
