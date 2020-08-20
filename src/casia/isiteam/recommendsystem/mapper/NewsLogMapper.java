package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.NewsLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewsLogMapper {

    List<NewsLog> findAllNewsLogs();

    List<NewsLog> findAllHotNews(@Param("startDate") String startDate);

    List<NewsLog> findBrowsedNewsByUser(@Param("userID") Long userID);

    List<NewsLog> findBrowsedNewsByDate(String startDate);
}
