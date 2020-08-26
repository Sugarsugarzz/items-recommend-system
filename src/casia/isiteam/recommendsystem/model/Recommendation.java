package casia.isiteam.recommendsystem.model;

/**
 * 推荐结果
 */
public class Recommendation {

    // id
    private long id;
    // 用户id
    private long user_id;
    // 推荐项id
    private long item_id;
    // 生成时间
    private String derive_time;
    // 生成所用推荐算法
    private int derive_algorithm;

    public Recommendation() {
    }

    public Recommendation(long id, long user_id, long item_id, String derive_time, int derive_algorithm) {
        this.id = id;
        this.user_id = user_id;
        this.item_id = item_id;
        this.derive_time = derive_time;
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

    public long getItem_id() {
        return item_id;
    }

    public void setItem_id(long item_id) {
        this.item_id = item_id;
    }

    public String getDerive_time() {
        return derive_time;
    }

    public void setDerive_time(String derive_time) {
        this.derive_time = derive_time;
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
                ", item_id=" + item_id +
                ", derive_time='" + derive_time + '\'' +
                ", derive_algorithm=" + derive_algorithm +
                '}';
    }
}
