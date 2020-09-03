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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;

/**
 * 数据库操作工具
 */
public class DBKit {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    static UserMapper userMapper;
    static ItemMapper itemMapper;
    static ItemLogMapper itemLogMapper;
    static RecommendationMapper recommendationMapper;
    static SqlSession sqlSession;

    static {
        // 加载 MyBatis配置文件
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(System.getProperty("user.dir") + "/recommend_config/mybatis-config.xml"));
        } catch (Exception e) {
            inputStream = ConfigKit.class.getClassLoader().getResourceAsStream("recommend_config/mybatis-config.xml");
        }

        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);

        sqlSession = sqlSessionFactory.openSession();
        userMapper = sqlSession.getMapper(UserMapper.class);
        itemMapper = sqlSession.getMapper(ItemMapper.class);
        itemLogMapper = sqlSession.getMapper(ItemLogMapper.class);
        recommendationMapper = sqlSession.getMapper(RecommendationMapper.class);
    }

    /**
     * 获取所有 User
     * @return User列表
     */
    public static List<User> getAllUsers() {
        return userMapper.findAllUsers();
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
    public static List<User> getUserPrefList(Collection<Long> userIDs) {
        return userMapper.findPrefListByUserIDs(userIDs);
    }

    /**
     * 根据用户ID列表，获取用户的wiki偏好
     * @param userIDs 用户ID列表
     * @return User对象列表
     */
    public static List<User> getUserWikiPrefList(Collection<Long> userIDs) {
        return userMapper.findWikiPrefListByUserIDs(userIDs);
    }

    /**
     * 根据用户ID，更新用户的偏好关键词列表
     * @param prefList 新的偏好列表
     * @param userID 用户ID
     */
    public static void updateUserPrefList(String prefList, Long userID) {
        userMapper.updatePrefListByUserID(prefList, userID);
        sqlSession.commit();
    }

    /**
     * 根据用ID，更新用户的wiki偏好
     * @param wikiPrefList 新的偏好列表
     * @param userID 用户ID
     */
    public static void updateUserWikiPrefList(String wikiPrefList, Long userID) {
        userMapper.updateWikiPrefListByUserID(wikiPrefList, userID);
        sqlSession.commit();
    }

    /**
     * 根据信息项ID，获取 Items
     * @param itemIDs 信息项ID列表
     * @return Item对象列表
     */
    public static List<Item> getItemsByIDs(Collection<Long> itemIDs) {
        return itemMapper.findItemsByIDs(itemIDs);
    }

    /**
     * 根据wiki项ID，获取 Items
     * @param itemIDs wiki项ID列表
     * @return Item对象列表
     */
    public static List<Item> getWikiItemsByIDs(Collection<Long> itemIDs) {
        return itemMapper.findWikiItemsByIDs(itemIDs);
    }

    /**
     * 根据起始发布日期，获取 Items
     * @param startDate 起始日期
     * @return Items列表
     */
    public static List<Item> getItemsByPublishTime(String startDate) {
        return itemMapper.findItemsByPublishTime(startDate);
    }

    /**
     * 根据起始发布日期，获取 WikiItems
     * @param startDate 起始日期
     * @return WikiItems列表
     */
    public static List<Item> getWikiItemsByPublishTime(String startDate) {
        return itemMapper.findWikiItemsByPublishTime(startDate);
    }

    /**
     * 根据有效起始日期，从各领域抽取一些 Items
     * @param startDate 有效起始日期
     * @return Items列表
     */
    public static List<Item> getGroupItemsByPublishTime(String startDate) {
        return itemMapper.findGroupItemsByPublishTime(startDate);
    }

    /**
     * 根据有效起始日期，从 wiki_info表 分组随机抽取一些 Items
     * @param startDate 有效起始日期
     * @return Items列表
     */
    public static List<Item> getGroupWikiItemsByPublishTime(String startDate) {
        return itemMapper.findGroupWikiItemsByPublishTime(startDate);
    }

    /**
     * 获取所有 ItemLog（浏览历史）
     * @param infoType 头条 or 百科
     * @return ItemLog列表
     */
    public static List<ItemLog> getAllItemLogs(int infoType) {
        return itemLogMapper.findAllItemLogs(infoType);
    }

    /**
     * 获取时效性的 信息项ID
     * @param startDate 有效起始日期
     * @param infoType 头条 or 百科
     * @return 信息项ID列表
     */
    public static List<Long> getHotItemIDs(String startDate, int infoType) {
        List<Long> hotItemIDs = new ArrayList<>();
        List<ItemLog> itemLogs = itemLogMapper.findAllHotItems(startDate, infoType);
        for (ItemLog itemLog : itemLogs) {
            hotItemIDs.add(itemLog.getRef_data_id());
        }
        return hotItemIDs;
    }

    /**
     * 根据用户ID，获取用户浏览记录
     * @param userID 用户ID
     * @param infoType 头条 or 百科
     * @return ItemLogs
     */
    public static List<ItemLog> getUserBrowsedItems(Long userID, int infoType) {
        return itemLogMapper.findBrowsedItemsByUser(userID, infoType);
    }

    /**
     * 获取今日所有用户的浏览记录
     * @param startDate 今日日期
     * @param infoType 头条 or 百科
     * @return ItemLogs
     */
    public static List<ItemLog> getBrowsedItemsByDate(String startDate, int infoType) {
        return itemLogMapper.findBrowsedItemsByDate(startDate, infoType);
    }

    /**
     * 获取所有模块名
     * @return ModuleNames
     */
    public static List<String> getAllModuleNames() {
        return userMapper.findAllModuleName();
    }

    public static List<String> getAllWikiModuleNames() {
        return userMapper.findAllWikiModuleName();
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
     * @param date 时效性起始日期
     * @param infoType 头条 or 百科
     * @return 推荐记录
     */
    public static List<Recommendation> getUserRecommendedItems(Long userID, String date, int infoType) {
        return recommendationMapper.findRecommendedItemsByUser(userID, date, infoType);
    }

    /**
     * 将推荐结果存入 recommendations 表
     * @param userID 用户ID
     * @param recommendItemID 推荐信息项ID
     * @param algorithm_type 推荐算法类型
     * @param infoType 头条 or 百科
     */
    public static void saveRecommendation(Long userID, Long recommendItemID, int algorithm_type, int infoType) {

        Recommendation obj = new Recommendation();
        obj.setUser_id(userID);
        obj.setItem_id(recommendItemID);
        obj.setDerive_algorithm(algorithm_type);
        obj.setInfo_type(infoType);
        recommendationMapper.saveRecommendation(obj);
        sqlSession.commit();
    }
}
