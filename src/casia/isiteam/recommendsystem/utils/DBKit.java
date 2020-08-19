package casia.isiteam.recommendsystem.utils;

import casia.isiteam.recommendsystem.mapper.NewsLogMapper;
import casia.isiteam.recommendsystem.mapper.RecommendationMapper;
import casia.isiteam.recommendsystem.mapper.UserMapper;
import casia.isiteam.recommendsystem.model.NewsLog;
import casia.isiteam.recommendsystem.model.Recommendation;
import casia.isiteam.recommendsystem.model.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作工具
 */
public class DBKit {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    static SqlSession sqlSession;
    static UserMapper userMapper;
    static NewsLogMapper newslogMapper;
    static RecommendationMapper recommendationMapper;

    static {
        // 加载 MyBatis配置文件
        InputStream inputStream = DBKit.class.getClassLoader().getResourceAsStream("mybatis-config.xml");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);

        sqlSession = sqlSessionFactory.openSession();
        userMapper = sqlSession.getMapper(UserMapper.class);
        newslogMapper = sqlSession.getMapper(NewsLogMapper.class);
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
     * 获取时效性的 热点新闻 ID
     * @param startDate 有效起始日期
     * @return 热点新闻ID列表
     */
    public static List<Long> getHotNewsIDs(String startDate) {
        List<Long> hotNewsIDs = new ArrayList<>();
        List<NewsLog> news = newslogMapper.findAllHotNews(startDate);
        for (NewsLog newsLog : news) {
            hotNewsIDs.add(newsLog.getNews_id());
        }
        return hotNewsIDs;
    }

    /**
     * 获取用户浏览过的新闻记录
     * @param userID 用户ID
     * @return 浏览过的新闻记录
     */
    public static List<NewsLog> getUserBrowsedNews(Long userID) {
        return newslogMapper.findBrowsedNewsByUser(userID);
    }

    /**
     * 获取已向用户推荐过的新闻推荐记录
     * @param userID 用户ID
     * @param date 时效性起始日期
     * @return 推荐记录
     */
    public static List<Recommendation> getUserRecommendedNews(Long userID, String date) {
        return recommendationMapper.findRecommendedNewsByUser(userID, date);
    }

    /**
     * 获取当日为某一用户的已推荐的新闻数量
     * @param timestamp 当日时间戳
     * @param userID 用户ID
     * @return 已推荐新闻数量
     */
    public static long getUserTodayRecommendationCount(Timestamp timestamp, long userID) {
        return recommendationMapper.findTodayRecommendationCountByUser(timestamp, userID);
    }

    /**
     * 将推荐结果存入 recommendations 表
     * @param userID 用户ID
     * @param recommendNewsID 推荐新闻ID
     * @param algorithm_type 推荐算法类型
     */
    public static void saveRecommendation(Long userID, Long recommendNewsID, int algorithm_type) {

        Recommendation obj = new Recommendation();
        obj.setUser_id(userID);
        obj.setNews_id(recommendNewsID);
        obj.setDerive_algorithm(algorithm_type);
        recommendationMapper.saveRecommendation(obj);
    }
}
