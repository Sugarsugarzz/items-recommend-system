package casia.isiteam.recommendsystem.model;

import java.util.Date;

/**
 * 用户浏览历史记录
 */
public class ItemLog {

    // 浏览记录id
    private long id;
    // 用户id
    private long user_id;
    // 信息id
    private long ref_data_id;
    // 浏览时间
    private Date insert_time;
    // 信息类型
    private long info_type;


    public ItemLog() {
    }

    public ItemLog(long id, long user_id, long ref_data_id, Date insert_time, long info_type) {
        this.id = id;
        this.user_id = user_id;
        this.ref_data_id = ref_data_id;
        this.insert_time = insert_time;
        this.info_type = info_type;
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

    public long getRef_data_id() {
        return ref_data_id;
    }

    public void setRef_data_id(long ref_data_id) {
        this.ref_data_id = ref_data_id;
    }

    public Date getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(Date insert_time) {
        this.insert_time = insert_time;
    }

    public long getInfo_type() {
        return info_type;
    }

    public void setInfo_type(long info_type) {
        this.info_type = info_type;
    }

    @Override
    public String toString() {
        return "ItemLog{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", ref_data_id=" + ref_data_id +
                ", insert_time=" + insert_time +
                ", info_type=" + info_type +
                '}';
    }
}
