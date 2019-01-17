package com.wx.account.mapper;

import com.wx.account.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface UserMapper {

    User getUserByName(@Param(value = "name") String name);

}