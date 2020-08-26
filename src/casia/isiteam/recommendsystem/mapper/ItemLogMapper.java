package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.ItemLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemLogMapper {

    List<ItemLog> findAllItemLogs();

    List<ItemLog> findAllHotItems(@Param("startDate") String startDate);

    List<ItemLog> findBrowsedItemsByUser(@Param("userID") Long userID);

    List<ItemLog> findBrowsedItemsByDate(String startDate);
}
