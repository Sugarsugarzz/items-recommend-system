package casia.isiteam.recommendsystem.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constant {

    /**
     * 信号位
     * true：正在执行推荐操作
     * false：未在执行
     */
    public static Boolean isProcessing = false;

    /**
     * 信息类型
     * 1：头条 2：百科 3：期刊 4：报告
     */
    public static final List<Integer> INFO_TYPES = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
}
