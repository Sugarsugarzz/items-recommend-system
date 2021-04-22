package casia.isiteam.recommendsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 信息项
 *  1：头条（toutiao_info_ref表）
 *  2：百科（wiki_info_ref表）
 *  3：期刊（reportinfo表）
 *  4：报告（periodical表）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Item {

    /**
     * toutiao_info_ref表、periodical表、reportinfo表 的 id
     */
    private Long id;

    /**
     * subject_info表 的 id
     */
    private Long auto_id;

    /**
     * wiki_info_ref表 的 id
     */
    private Long wiki_info_id;

    /**
     * toutiao_info_ref表 的 infoTitle
     */
    private String infoTitle;

    /**
     * wiki_info_ref表 的 name
     */
    private String name;

    /**
     * subject_info表 的 subjectName
     */
    private String subjectName;

    /**
     * periodical表 的 perName
     */
    private String perName;

    /**
     * reportinfo表 的 reportName
     */
    private String reportName;

    /**
     * 发布时间
     */
    private String publishTime;
}
