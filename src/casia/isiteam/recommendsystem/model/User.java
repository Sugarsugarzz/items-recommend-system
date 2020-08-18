package casia.isiteam.recommendsystem.model;

/**
 * 用户
 */
public class User {

    // 用户id
    private long id;
    // 用户名
    private String name;
    // 用户偏好
    private String pref_list;
    // 用户最后登录时间
    private String latest_log_time;

    public User() {
    }

    public User(long id, String name, String pref_list, String latest_log_time) {
        this.id = id;
        this.name = name;
        this.pref_list = pref_list;
        this.latest_log_time = latest_log_time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPref_list() {
        return pref_list;
    }

    public void setPref_list(String pref_list) {
        this.pref_list = pref_list;
    }

    public String getLatest_log_time() {
        return latest_log_time;
    }

    public void setLatest_log_time(String latest_log_time) {
        this.latest_log_time = latest_log_time;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pref_list='" + pref_list + '\'' +
                ", latest_log_time='" + latest_log_time + '\'' +
                '}';
    }
}
