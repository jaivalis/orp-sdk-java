package nl.shared;


public class Article {

    private Long    domainID;
    private Long    itemID;
    private boolean recommendable;

    public Article(RecommenderItem ri) {
        this.domainID = ri.getDomainID();
        this.itemID   = ri.getItemID();
        this.recommendable = ri.getRecommendable();

    }


}
