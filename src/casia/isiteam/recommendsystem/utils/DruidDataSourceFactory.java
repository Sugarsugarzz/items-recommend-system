package casia.isiteam.recommendsystem.utils;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Druid DataSource
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    private Properties props;

    @Override
    public DataSource getDataSource() {
        DruidDataSource dds = new DruidDataSource();
        dds.setDriverClassName(this.props.getProperty("driver"));
        dds.setUrl(this.props.getProperty("url"));
        dds.setUsername(this.props.getProperty("username"));
        dds.setPassword(this.props.getProperty("password"));
        dds.setInitialSize(5);
        dds.setMinIdle(5);
        dds.setMaxActive(20);
        // 获取连接等待超时的时间
        dds.setMaxWait(60000);
        // 配置间隔多久进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dds.setTimeBetweenEvictionRunsMillis(60000);
        // 配置一个连接在池中最小生存时间，单位是毫秒
        dds.setMinEvictableIdleTimeMillis(60000);
        dds.setValidationQuery("SELECT 'x'");
        dds.setTestWhileIdle(true);
        dds.setTestOnBorrow(false);
        dds.setTestOnReturn(false);


        try {
            dds.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dds;
    }

    @Override
    public void setProperties(Properties props) {
        this.props = props;
    }
}
