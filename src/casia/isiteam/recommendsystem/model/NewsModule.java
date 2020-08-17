package casia.isiteam.recommendsystem.model;

/**
 * 新闻模块
 */
public class NewsModule {

    // 新闻模块id
    private int id;
    // 新闻模块名
    private String name;

    public NewsModule() {
    }

    public NewsModule(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NewsModule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
