package casia.isiteam.recommendsystem.model;

/**
 * 用户浏览历史记录
 */
public class NewsLog {

    // 浏览记录id
    private int id;
    // 用户id
    private int user_id;
    // 新闻id
    private int news_id;
    // 浏览时间
    private String view_time;
    // 用户对新闻的偏好程度  0 浏览  1 评论  2 收藏  3 点赞
    private int prefer_degree;

    public NewsLog() {
    }

    public NewsLog(int id, int user_id, int news_id, String view_time, int prefer_degree) {
        this.id = id;
        this.user_id = user_id;
        this.news_id = news_id;
        this.view_time = view_time;
        this.prefer_degree = prefer_degree;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getNews_id() {
        return news_id;
    }

    public void setNews_id(int news_id) {
        this.news_id = news_id;
    }

    public String getView_time() {
        return view_time;
    }

    public void setView_time(String view_time) {
        this.view_time = view_time;
    }

    public int getPrefer_degree() {
        return prefer_degree;
    }

    public void setPrefer_degree(int prefer_degree) {
        this.prefer_degree = prefer_degree;
    }

    @Override
    public String toString() {
        return "NewsLog{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", news_id=" + news_id +
                ", view_time='" + view_time + '\'' +
                ", prefer_degree=" + prefer_degree +
                '}';
    }
}
