package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.NewsLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewsLogMapper {

    List<NewsLog> findAllHotNews(@Param("startDate") String startDate);
}
