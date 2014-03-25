package nl.recsys;

import nl.shared.RecommenderItem;
import org.slf4j.Logger;

import java.util.List;

public abstract class Recommender {

    protected static Logger logger;

    public abstract boolean notificationEvent(RecommenderItem ri);
    public abstract boolean updateEvent(RecommenderItem ri);
    public abstract boolean createEvent(RecommenderItem ri);

    protected abstract List<Long> getRecommendations(RecommenderItem ri);

    public String getRecommendationJSON(RecommenderItem ri) {
        List<Long> recs = getRecommendations(ri);

        System.out.println(recs);
        if (recs.size() == 0) {
            return "{" + "\"recs\": {" + "\"ints\": {\"3\": [] }" + "}}";
        }
        return "{" + "\"recs\": {" + "\"ints\": {" + "\"3\": " + recs + "}" + "}}";
    }

}
