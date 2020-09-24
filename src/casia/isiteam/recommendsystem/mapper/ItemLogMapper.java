package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.ItemLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemLogMapper {

    List<ItemLog> findAllHotItems(@Param("startDate") String startDate, @Param("infoType") int infoType, @Param("recNum") int recNum);

    List<ItemLog> findBrowsedItemsByUser(@Param("userID") Long userID, @Param("infoType") int infoType);

    List<ItemLog> findBrowsedItemsByDate(@Param("startDate") String startDate, @Param("infoType") int infoType);
}
