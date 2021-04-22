package casia.isiteam.recommendsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 推荐结果（recommendations表）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Recommendation {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long user_id;

    /**
     * 推荐项id
     */
    private Long item_id;

    /**
     * 信息类型  1：头条 2：百科 3：专题 4：期刊 5：报告
     */
    private Integer info_type;
}
