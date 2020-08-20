package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface UserMapper {

    List<User> findAllUsers();

    List<User> findPrefListByUserIDs(@Param("userIDs") Collection<Long> userIDs);

    void updatePrefListByUserID(@Param("prefList") String prefList, @Param("userID") Long userID);
}
