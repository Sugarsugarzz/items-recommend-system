package casia.isiteam.recommendsystem.model;

/**
 * 信息项
 */
public class Item {

    // 信息项id
    private long id;
    // wiki项id
    private long wiki_info_id;
    // wiki项名
    private String name;
    // 摘要
    private String infoDesc;
    // wiki摘要
    private String summary;
    // 发布时间
    private String publishTime;
    // 标题
    private String infoTitle;
    // 所属大模块名
    private String classifyName;
    // 所属子模块名
    private String classifySubName;


    public Item() {
    }

    public Item(long id, long wiki_info_id, String name, String infoDesc, String summary, String publishTime, String infoTitle, String classifyName, String classifySubName) {
        this.id = id;
        this.wiki_info_id = wiki_info_id;
        this.name = name;
        this.infoDesc = infoDesc;
        this.summary = summary;
        this.publishTime = publishTime;
        this.infoTitle = infoTitle;
        this.classifyName = classifyName;
        this.classifySubName = classifySubName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getWiki_info_id() {
        return wiki_info_id;
    }

    public void setWiki_info_id(long wiki_info_id) {
        this.wiki_info_id = wiki_info_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfoDesc() {
        return infoDesc;
    }

    public void setInfoDesc(String infoDesc) {
        this.infoDesc = infoDesc;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    public String getClassifySubName() {
        return classifySubName;
    }

    public void setClassifySubName(String classifySubName) {
        this.classifySubName = classifySubName;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", wiki_info_id=" + wiki_info_id +
                ", name='" + name + '\'' +
                ", infoDesc='" + infoDesc + '\'' +
                ", summary='" + summary + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", infoTitle='" + infoTitle + '\'' +
                ", classifyName='" + classifyName + '\'' +
                ", classifySubName='" + classifySubName + '\'' +
                '}';
    }
}
