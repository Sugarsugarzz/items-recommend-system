package casia.isiteam.recommendsystem.utils;

import casia.isiteam.recommendsystem.mapper.UserMapper;
import casia.isiteam.recommendsystem.model.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作工具
 */
public class DBKit {

    private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    static SqlSession sqlSession;
    static UserMapper userMapper;

    static {
        // 加载 MyBatis配置文件
        InputStream inputStream = DBKit.class.getClassLoader().getResourceAsStream("mybatis-config.xml");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);

        sqlSession = sqlSessionFactory.openSession();
        userMapper = sqlSession.getMapper(UserMapper.class);
    }

    /**
     * 获得所有 User ID
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

}
