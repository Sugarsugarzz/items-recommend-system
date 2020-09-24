package casia.isiteam.recommendsystem.model;

/**
 * 信息项
 */
public class Item {

    // 信息项id
    private long id;
    private long auto_id;
    private long wiki_info_id;
    // 名
    private String infoTitle;
    private String name;
    private String subjectName;
    private String perName;
    private String reportName;
    // 发布时间
    private String publishTime;


    public Item() {
    }

    public Item(long id, long auto_id, long wiki_info_id, String infoTitle, String name, String subjectName, String perName, String reportName, String publishTime) {
        this.id = id;
        this.auto_id = auto_id;
        this.wiki_info_id = wiki_info_id;
        this.infoTitle = infoTitle;
        this.name = name;
        this.subjectName = subjectName;
        this.perName = perName;
        this.reportName = reportName;
        this.publishTime = publishTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAuto_id() {
        return auto_id;
    }

    public void setAuto_id(long auto_id) {
        this.auto_id = auto_id;
    }

    public long getWiki_info_id() {
        return wiki_info_id;
    }

    public void setWiki_info_id(long wiki_info_id) {
        this.wiki_info_id = wiki_info_id;
    }

    public String getInfoTitle() {
        return infoTitle;
    }

    public void setInfoTitle(String infoTitle) {
        this.infoTitle = infoTitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getPerName() {
        return perName;
    }

    public void setPerName(String perName) {
        this.perName = perName;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", auto_id=" + auto_id +
                ", wiki_info_id=" + wiki_info_id +
                ", infoTitle='" + infoTitle + '\'' +
                ", name='" + name + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", perName='" + perName + '\'' +
                ", reportName='" + reportName + '\'' +
                ", publishTime='" + publishTime + '\'' +
                '}';
    }
}
