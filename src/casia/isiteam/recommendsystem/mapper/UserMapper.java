package casia.isiteam.recommendsystem.mapper;

import casia.isiteam.recommendsystem.model.User;

import java.util.List;

public interface UserMapper {

    List<User> findAllUsers();
}
