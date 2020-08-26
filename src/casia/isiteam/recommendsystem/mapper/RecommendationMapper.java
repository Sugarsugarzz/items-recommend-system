package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.Recommendation;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

public interface RecommendationMapper {

    long findTodayRecommendationCountByUser(@Param("today") Timestamp today, @Param("userID") long userID);

    List<Recommendation> findRecommendedItemsByUser(@Param("userID") Long userID, @Param("date") String date);

    void saveRecommendation(Recommendation obj);
}
