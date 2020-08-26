package casia.isiteam.recommendsystem.utils;

import casia.isiteam.recommendsystem.algorithms.RecommendAlgorithm;
import casia.isiteam.recommendsystem.model.ItemLog;
import casia.isiteam.recommendsystem.model.Recommendation;
import casia.isiteam.recommendsystem.model.User;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 供算法调用的工具方法
 */
public class RecommendKit {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    // 推荐信息项的时效性天数，即从推荐当天开始到之前的 beforeDays 天的信息项仍然具有时效性，予以推荐
    private static final int beforeDays = ConfigGetKit.getInt("recommendBeforeDays");
//    private static final int beforeDays = -1;

    /**
     * 在当日基础上增加/减少天数后的日期字符串
     * @return yyyy-MM-dd 日期字符串
     */
    public static String getInRecDate() {
        return getSpecificDayFormat(beforeDays);
    }

    /**
     * 在当日基础上增加/减少天数后的日期字符串
     * @param beforeDays 增加/减少的天数
     * @return yyyy-MM-dd 日期字符串
     */
    public static String getInRecDate(int beforeDays) {
        return getSpecificDayFormat(beforeDays);
    }

    /**
     * getInRecDate 方法的具体实现
     */
    public static String getSpecificDayFormat(int beforeDays) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, beforeDays);  // 增加/减少天数，取决beforeDays的正负
        return sdf.format(calendar.getTime());
    }

    /**
     * 获取特定的时间戳
     */
    public static Timestamp getCertainTimestamp(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return new Timestamp(calendar.getTime().getTime());
    }

    /**
     * 在当日基础上增加/减少天数后的日期时间戳，便于推荐算法在比较时间前后时调用
     * @param beforeDays 增加/减少的天数
     * @return 日期时间戳
     */
    public static Timestamp getInRecTimestamp(int beforeDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, beforeDays);
        return new Timestamp(calendar.getTime().getTime());
    }

    /**
     * 用户偏好为 null，根据模块名补充默认偏好
     */
    public static String getDefaultPrefList() {

        List<String> moduleNames = DBKit.getAllModuleNames();
        JSONObject jsonObject = new JSONObject();
        for (String name : moduleNames) {
            jsonObject.put(name, new JSONObject());
        }
        return jsonObject.toJSONString();
    }

    /**
     * 获取 用户与偏好列表 的键值对
     * @param userIDs 用户ID列表
     */
    public static Map<Long, String> getUserPreListMap(Collection<Long> userIDs) {
        Map<Long, String> map = new HashMap<>();
        List<User> users = DBKit.getUserPrefList(userIDs);
        for (User user : users) {
            map.put(user.getId(), user.getPref_list());
        }

        return map;
    }

    /**
     * 过滤用户已经看过的信息项
     * @param recItemList 信息项推荐列表
     * @param userID 用户ID
     * @param infoType 头条 or 百科
     */
    public static void filterBrowsedItems(Collection<Long> recItemList, Long userID, int infoType) {

        if (recItemList.size() == 0)
            return;

        List<ItemLog> userBrowsedItems = DBKit.getUserBrowsedItems(userID, infoType);
        for (ItemLog itemLog : userBrowsedItems) {
            System.out.println("用户浏览过的信息项id - " + itemLog.getRef_data_id());
            recItemList.remove(itemLog.getRef_data_id());
        }
    }

    /**
     * 过滤已向用户推荐过的信息项
     * @param recItemList 信息项推荐列表
     * @param userID 用户ID
     * @param infoType 头条 or 百科
     */
    public static void filterRecommendedItems(Collection<Long> recItemList, Long userID, int infoType) {

        if (recItemList.size() == 0)
            return;

        List<Recommendation> userRecommendedItems = DBKit.getUserRecommendedItems(userID, getInRecDate(), infoType);
        for (Recommendation recommendation : userRecommendedItems) {
            System.out.println("已向用户推荐过的信息项id - " + recommendation.getItem_id());
            recItemList.remove(recommendation.getItem_id());
        }
    }

    /**
     * 去除推荐结果中超出数量限制的部分
     * @param toBeRecommended 推荐候选列表
     * @param recNum 最大推荐数量限制
     */
    public static void removeOverSizeItems(Collection<Long> toBeRecommended, int recNum) {

        if (toBeRecommended.size() <= recNum)
            return;

        int i = 0;
        Iterator<Long> iterator = toBeRecommended.iterator();
        while (iterator.hasNext()) {
            if (i >= recNum) {
                iterator.remove();
            }
            iterator.next();
            i++;
        }
    }

    /**
     * 将推荐结果存入 recommendations 表
     * @param userID 用户ID
     * @param recommendItemIDs 待存入的推荐信息项ID列表
     * @param algorithm_type 标注推荐结果来自哪个推荐算法
     * @param infoType 头条 or 百科
     */
    public static void insertRecommendations(Long userID, Collection<Long> recommendItemIDs, int algorithm_type, int infoType) {

        for (Long recommendItemID : recommendItemIDs) {
            System.out.println("本次向用户推荐的信息项id - " + recommendItemID);
            DBKit.saveRecommendation(userID, recommendItemID, algorithm_type, infoType);
        }
    }
}
