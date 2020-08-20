package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.News;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface NewsMapper {
    List<News> findNewsByIDs(@Param("newsIDs") Collection<Long> newsIDs);
}
