package casia.isiteam.recommendsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 用户（app_user表）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头条偏好
     */
    private String pref_list;

    /**
     * 百科偏好
     */
    private String wiki_pref_list;

    /**
     * 专题偏好
     */
    private String subject_pref_list;

    /**
     * 期刊偏好
     */
    private String periodical_pref_list;

    /**
     * 报告偏好
     */
    private String report_pref_list;
}
