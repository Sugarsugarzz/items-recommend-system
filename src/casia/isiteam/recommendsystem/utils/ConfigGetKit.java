package casia.isiteam.recommendsystem.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 配置文件读取工具类
 */
public class ConfigGetKit {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    public static Properties configGetKit = new Properties();

    public static void loadProperties(String filename) {

        try {
            configGetKit.load(new FileInputStream(System.getProperty("user.dir") + "/resources/" + filename + ".properties"));
        } catch (FileNotFoundException e) {
            logger.error("读取配置文件失败！ - 文件路径错误或文件不存在。");
        } catch (IOException e) {
            logger.error("读取配置文件失败！");
        }
    }

    public static String getString(String key) {
        return configGetKit.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(configGetKit.getProperty(key));
    }



}
