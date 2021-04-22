package casia.isiteam.recommendsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * 用户浏览历史记录（user_read_record表）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemLog {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 信息类型  1：头条 2：百科 3：期刊 4：报告
     */
    private Long info_type;

    /**
     * 信息项id
     */
    private Long ref_data_id;

    /**
     * 用户id
     */
    private Long user_id;

    /**
     * 浏览时间
     */
    private Date insert_time;
}
