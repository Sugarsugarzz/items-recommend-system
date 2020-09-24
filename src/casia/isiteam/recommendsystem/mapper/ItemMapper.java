package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.Item;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface ItemMapper {

    List<Item> findItemsByInfoType(@Param("infoType") int infoType);

    List<Item> findItemsByIDsAndInfoType(@Param("itemIDs") Collection<Long> itemIDs, @Param("infoType") int infoType);

    List<Item> findRandomItemsByInfoType(@Param("infoType") int infoType, @Param("recNum") int recNum);
}
