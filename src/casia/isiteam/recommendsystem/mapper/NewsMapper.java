package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.News;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface NewsMapper {
    List<News> findNewsByIDs(@Param("newsIDs") Collection<Long> newsIDs);

    List<News> findNewsByPublishTime(@Param("startDate") String startDate);

    List<News> findGroupNewsByPublishTime(@Param("startDate") String startDate);
}
