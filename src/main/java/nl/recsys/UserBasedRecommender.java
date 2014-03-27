package nl.recsys;


import nl.shared.RecommenderItem;

import java.util.List;

public class UserBasedRecommender extends Recommender {

    @Override
    public boolean notificationEvent(RecommenderItem ri) {
        return false;
    }

    @Override
    public boolean updateEvent(RecommenderItem ri) {
        return false;
    }

    @Override
    public boolean createEvent(RecommenderItem ri) {
        return false;
    }

    @Override
    protected List<Long> getRecommendations(RecommenderItem ri) {
        return null;
    }
}
