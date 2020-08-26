package casia.isiteam.recommendsystem.model;

/**
 * 信息项
 */
public class Item {

    // 信息id
    private long id;
    // 摘要
    private String infoDesc;
    // 发布时间
    private String publishTime;
    // 标题
    private String infoTitle;
    // 所属模块名
    private String classifySubName;


    public Item() {
    }

    public Item(long id, String infoDesc, String publishTime, String infoTitle, String classifySubName) {
        this.id = id;
        this.infoDesc = infoDesc;
        this.publishTime = publishTime;
        this.infoTitle = infoTitle;
        this.classifySubName = classifySubName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInfoDesc() {
        return infoDesc;
    }

    public void setInfoDesc(String infoDesc) {
        this.infoDesc = infoDesc;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getInfoTitle() {
        return infoTitle;
    }

    public void setInfoTitle(String infoTitle) {
        this.infoTitle = infoTitle;
    }

    public String getClassifySubName() {
        return classifySubName;
    }

    public void setClassifySubName(String classifySubName) {
        this.classifySubName = classifySubName;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", infoDesc='" + infoDesc + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", infoTitle='" + infoTitle + '\'' +
                ", classifySubName='" + classifySubName + '\'' +
                '}';
    }
}
