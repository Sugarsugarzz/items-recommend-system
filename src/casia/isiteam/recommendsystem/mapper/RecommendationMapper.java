package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.Recommendation;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

public interface RecommendationMapper {

    void deleteRecommendationByDate(@Param("date") String date);

    void deleteDefaultRecommendationByDate(@Param("date") String date);

    long findRecommendationCountByUserAndTime(@Param("today") Timestamp today, @Param("userID") long userID, @Param("infoType") int infoType);

    List<Recommendation> findRecommendedItemsByUser(@Param("userID") Long userID, @Param("infoType") int infoType);

    void saveRecommendation(@Param("userID") Long userID, @Param("itemID") Long itemID, @Param("infoType") Long infoType);

    void saveDefaultRecommendation(@Param("itemID") Long itemID, @Param("infoType") Long infoType);

}
