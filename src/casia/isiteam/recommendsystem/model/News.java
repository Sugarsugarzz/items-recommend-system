package casia.isiteam.recommendsystem.model;

/**
 * 新闻
 */
public class News {

    // 新闻id
    private long id;
    // 新闻内容
    private String content;
    // 新闻发布时间
    private String news_time;
    // 新闻标题
    private String title;
    // 新闻对应模块id
    private long module_id;
    // 新闻url
    private String url;

    public News() {
    }

    public News(long id, String content, String news_time, String title, long module_id, String url) {
        this.id = id;
        this.content = content;
        this.news_time = news_time;
        this.title = title;
        this.module_id = module_id;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNews_time() {
        return news_time;
    }

    public void setNews_time(String news_time) {
        this.news_time = news_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getModule_id() {
        return module_id;
    }

    public void setModule_id(long module_id) {
        this.module_id = module_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", news_time='" + news_time + '\'' +
                ", title='" + title + '\'' +
                ", module_id=" + module_id +
                ", url='" + url + '\'' +
                '}';
    }
}
