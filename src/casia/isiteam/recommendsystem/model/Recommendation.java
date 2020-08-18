package casia.isiteam.recommendsystem.model;

/**
 * 推荐结果
 */
public class Recommendation {

    // 推荐项id
    private long id;
    // 用户id
    private long user_id;
    // 新闻id
    private long news_id;
    // 生成时间
    private String derive_time;
    // 用户反馈 0 未浏览 1 已浏览
    private int feedback;
    // 生成所用推荐算法
    private int derive_algorithm;

    public Recommendation() {
    }

    public Recommendation(long id, long user_id, long news_id, String derive_time, int feedback, int derive_algorithm) {
        this.id = id;
        this.user_id = user_id;
        this.news_id = news_id;
        this.derive_time = derive_time;
        this.feedback = feedback;
        this.derive_algorithm = derive_algorithm;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getNews_id() {
        return news_id;
    }

    public void setNews_id(long news_id) {
        this.news_id = news_id;
    }

    public String getDerive_time() {
        return derive_time;
    }

    public void setDerive_time(String derive_time) {
        this.derive_time = derive_time;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }

    public int getDerive_algorithm() {
        return derive_algorithm;
    }

    public void setDerive_algorithm(int derive_algorithm) {
        this.derive_algorithm = derive_algorithm;
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", news_id=" + news_id +
                ", derive_time='" + derive_time + '\'' +
                ", feedback=" + feedback +
                ", derive_algorithm=" + derive_algorithm +
                '}';
    }
}
