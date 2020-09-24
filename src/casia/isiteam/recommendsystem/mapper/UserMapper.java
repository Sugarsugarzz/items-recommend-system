package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface UserMapper {

    List<User> findAllUsers();

    List<User> findPrefListByUserIDsAndInfoType(@Param("userIDs") Collection<Long> userIDs, @Param("infoType") int infoType);

    void updatePrefListByUserIDAndInfoType(@Param("prefList") String prefList, @Param("userID") Long userID, @Param("infoType") int infoType);


}
