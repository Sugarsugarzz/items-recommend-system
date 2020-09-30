package casia.isiteam.recommendsystem.model;

/**
 * 用户
 */
public class User {

    // 用户id
    private long id;
    // 用户名
    private String username;
    // 头条偏好
    private String pref_list;
    // 百科偏好
    private String wiki_pref_list;
    // 专题偏好
    private String subject_pref_list;
    // 期刊偏好
    private String periodical_pref_list;
    // 报告偏好
    private String report_pref_list;

    public User() {
    }

    public User(long id, String username, String pref_list, String wiki_pref_list, String subject_pref_list, String periodical_pref_list, String report_pref_list) {
        this.id = id;
        this.username = username;
        this.pref_list = pref_list;
        this.wiki_pref_list = wiki_pref_list;
        this.subject_pref_list = subject_pref_list;
        this.periodical_pref_list = periodical_pref_list;
        this.report_pref_list = report_pref_list;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPref_list() {
        return pref_list;
    }

    public void setPref_list(String pref_list) {
        this.pref_list = pref_list;
    }

    public String getWiki_pref_list() {
        return wiki_pref_list;
    }

    public void setWiki_pref_list(String wiki_pref_list) {
        this.wiki_pref_list = wiki_pref_list;
    }

    public String getSubject_pref_list() {
        return subject_pref_list;
    }

    public void setSubject_pref_list(String subject_pref_list) {
        this.subject_pref_list = subject_pref_list;
    }

    public String getPeriodical_pref_list() {
        return periodical_pref_list;
    }

    public void setPeriodical_pref_list(String periodical_pref_list) {
        this.periodical_pref_list = periodical_pref_list;
    }

    public String getReport_pref_list() {
        return report_pref_list;
    }

    public void setReport_pref_list(String report_pref_list) {
        this.report_pref_list = report_pref_list;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", pref_list='" + pref_list + '\'' +
                ", wiki_pref_list='" + wiki_pref_list + '\'' +
                ", subject_pref_list='" + subject_pref_list + '\'' +
                ", periodical_pref_list='" + periodical_pref_list + '\'' +
                ", report_pref_list='" + report_pref_list + '\'' +
                '}';
    }
}
