package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.ItemLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ItemLogMapper {

    List<ItemLog> findBrowsedItemsByUser(@Param("userID") Long userID, @Param("infoType") int infoType);

    List<ItemLog> findBrowsedItemsByDate(@Param("startDate") String startDate, @Param("infoType") int infoType);

    List<ItemLog> findBrowsedItemsByDateAndUsers(@Param("startDate") String startDate, @Param("infoType") int infoType, @Param("userIds") List<Long> userIds);
}
