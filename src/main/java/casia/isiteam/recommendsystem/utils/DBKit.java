package casia.isiteam.recommendsystem.utils;

import casia.isiteam.recommendsystem.mapper.ItemLogMapper;
import casia.isiteam.recommendsystem.mapper.ItemMapper;
import casia.isiteam.recommendsystem.mapper.RecommendationMapper;
import casia.isiteam.recommendsystem.mapper.UserMapper;
import casia.isiteam.recommendsystem.model.Item;
import casia.isiteam.recommendsystem.model.ItemLog;
import casia.isiteam.recommendsystem.model.Recommendation;
import casia.isiteam.recommendsystem.model.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;

/**
 * 数据库操作工具
 */
@Service
public class DBKit {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    static UserMapper userMapper;
    static ItemMapper itemMapper;
    static ItemLogMapper itemLogMapper;
    static RecommendationMapper recommendationMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        DBKit.userMapper = userMapper;
    }

    @Autowired
    public void setItemMapper(ItemMapper itemMapper) {
        DBKit.itemMapper = itemMapper;
    }

    @Autowired
    public void setItemLogMapper(ItemLogMapper itemLogMapper) {
        DBKit.itemLogMapper = itemLogMapper;
    }

    @Autowired
    public void setRecommendationMapper(RecommendationMapper recommendationMapper) {
        DBKit.recommendationMapper = recommendationMapper;
    }

    /**
     * 获取所有 User ID
     * @return UserID列表
     */
    public static List<Long> getAllUserIDs() {
        List<Long> userIDs = new ArrayList<>();
        List<User> users = userMapper.findAllUsers();
        for (User user : users) {
            userIDs.add(user.getId());
        }
        return userIDs;
    }

    /**
     * 根据用户ID列表，获取用户的偏好关键词及TF-IDF值列表
     * @param userIDs 用户ID列表
     * @return User对象列表
     */
    public static List<User> getUserPrefList(Collection<Long> userIDs, int infoType) {
        return userMapper.findPrefListByUserIDsAndInfoType(userIDs, infoType);
    }

    /**
     * 根据用户ID，更新用户的偏好关键词列表
     * @param prefList 新的偏好列表
     * @param userID 用户ID
     */
    public static void updateUserPrefList(String prefList, Long userID, int infoType) {
        userMapper.updatePrefListByUserIDAndInfoType(prefList, userID, infoType);
    }

    /**
     * 获取所有 Items
     * @param infoType 类型ID
     */
    public static List<Item> getItems(int infoType) {
        return itemMapper.findItemsByInfoType(infoType);
    }

    /**
     * 根据 ID 获取 Items
     * @param itemIDs 信息项ID列表
     * @return Item列表
     */
    public static List<Item> getItemsByIDs(Collection<Long> itemIDs, int infoType) {
        return itemMapper.findItemsByIDsAndInfoType(itemIDs, infoType);
    }

    /**
     * 获取时效性的 信息项ID
     * @param startDate 有效起始日期
     * @param infoType 类型
     * @return 信息项ID列表
     */
    public static List<Long> getHotItemIDs(String startDate, int infoType) {
        List<Long> hotItemIDs = new ArrayList<>();
        List<ItemLog> itemLogs = itemLogMapper.findBrowsedItemsByDate(startDate, infoType);
        // 根据浏览次数排序生成Map
        Map<Long, Integer> countMap = new HashMap<>();
        for (ItemLog itemLog : itemLogs) {
            if (countMap.containsKey(itemLog.getRef_data_id())) {
                countMap.put(itemLog.getRef_data_id(), countMap.get(itemLog.getRef_data_id()) + 1);
            } else {
                countMap.put(itemLog.getRef_data_id(), 1);
            }
        }
        // 倒序排序
        List<Map.Entry<Long, Integer>> list = new ArrayList<>(countMap.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        for (Map.Entry<Long, Integer> entry : list) {
            hotItemIDs.add(entry.getKey());
        }

        return hotItemIDs;
    }

    /**
     * 随机取一些 Items
     * @param infoType 类型
     * @return Items ID列表
     */
    public static List<Long> getRandomItemsByInfoType(int infoType, int recNum) {
        List<Long> randomItemIDs = new ArrayList<>();
        List<Item> items = itemMapper.findRandomItemsByInfoType(infoType, recNum);
        for (Item item : items) {
            randomItemIDs.add(RecommendKit.getItemId(item, infoType));
        }
        return randomItemIDs;
    }

    /**
     * 根据时间取 Items
     */
    public static List<Long> getItemsByDateAndInfoType(String startDate, int infoType) {
        List<Long> latestItemIDs = new ArrayList<>();
        List<Item> items = itemMapper.findItemsByDateAndInfoType(startDate, infoType);
        for (Item item : items) {
            latestItemIDs.add(RecommendKit.getItemId(item, infoType));
        }
        return latestItemIDs;
    }

    /**
     * 根据用户ID，获取用户浏览记录
     * @param userID 用户ID
     * @param infoType 类型
     * @return ItemLogs
     */
    public static List<ItemLog> getUserBrowsedItems(Long userID, int infoType) {
        return itemLogMapper.findBrowsedItemsByUser(userID, infoType);
    }

    /**
     * 获取当日所有用户的浏览记录
     * @param startDate 日期
     * @param infoType 类型
     * @return ItemLogs
     */
    public static List<ItemLog> getBrowsedItemsByDate(String startDate, int infoType) {
        return itemLogMapper.findBrowsedItemsByDate(startDate, infoType);
    }

    /**
     * 获取当日为某一用户的已推荐的数量
     * @param timestamp 当日时间戳
     * @param userID 用户ID
     * @param infoType 头条 or 百科
     * @return 已推荐数量
     */
    public static long getRecommendationCountByUserAndTime(Timestamp timestamp, long userID, int infoType) {
        return recommendationMapper.findRecommendationCountByUserAndTime(timestamp, userID, infoType);
    }

    /**
     * 获取对用户的推荐记录
     * @param userID 用户ID
     * @param infoType 头条 or 百科
     * @return 推荐记录
     */
    public static List<Recommendation> getUserRecommendedItems(Long userID, int infoType) {
        return recommendationMapper.findRecommendedItemsByUser(userID, infoType);
    }

    /**
     * 将推荐结果存入 recommendations 表
     * @param userID 用户ID
     */
    public static void saveRecommendation(Long userID, List<long[]> candidates) {
        recommendationMapper.saveRecommendation(userID, candidates);
    }

    public static void saveDefaultRecommendation(List<long[]> candidates)  {
        recommendationMapper.saveDefaultRecommendation(candidates);
    }

    /**
     * 清除时效外的推荐项
     * @param date 起始日期
     */
    public static void deleteRecommendationByDate(String date) {
        recommendationMapper.deleteRecommendationByDate(date);
    }

    public static void deleteDefaultRecommendationByDate(String date) {
        recommendationMapper.deleteDefaultRecommendationByDate(date);
    }
}
