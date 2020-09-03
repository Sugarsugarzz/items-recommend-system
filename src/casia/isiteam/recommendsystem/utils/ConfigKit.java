package casia.isiteam.recommendsystem.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 常量参数配置
 */
public class ConfigKit {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    public static Properties configKit = new Properties();

    static {

        try {
            InputStream inputStream;
            try {
                inputStream = new FileInputStream(new File(System.getProperty("user.dir") + "/recommend_config/config.properties"));
            } catch (Exception e) {
                inputStream = ConfigKit.class.getClassLoader().getResourceAsStream("recommend_config/config.properties");
            }
            configKit.load(inputStream);
        } catch (FileNotFoundException e) {
            logger.error("读取参数配置文件失败！原因：文件路径错误或者文件不存在。");
        } catch (IOException e) {
            logger.error("读取参数配置文件失败！");
        }
    }

    public static String getString(String key) {
        return configKit.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(configKit.getProperty(key));
    }

    public static double getDouble(String key) {
        return Double.parseDouble(configKit.getProperty(key));
    }

}
