package casia.isiteam.recommendsystem.mapper;

import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

public interface RecommendationMapper {

    long findTodayRecommendationCountByUser(@Param("today") Timestamp today, @Param("userID") long userID);
}
