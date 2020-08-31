package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.Item;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface ItemMapper {

    List<Item> findItemsByIDs(@Param("itemIDs") Collection<Long> itemIDs);

    List<Item> findItemsByPublishTime(@Param("startDate") String startDate);

    List<Item> findGroupItemsByPublishTime(@Param("startDate") String startDate);

    List<Item> findWikiItemsByIDs(@Param("itemIDs") Collection<Long> itemIDs);

    List<Item> findWikiItemsByPublishTime(@Param("startDate") String startDate);

    List<Item> findGroupWikiItemsByPublishTime(@Param("startDate") String startDate);

}
