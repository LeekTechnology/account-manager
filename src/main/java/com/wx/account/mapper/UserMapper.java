package com.wx.account.mapper;

import com.wx.account.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    /**
     * 保存订阅用户
     * @param record
     * @return
     */
    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 取消订阅
     * @param openid
     */
    void unSubscribe(@Param(value = "openid") String openid);

    /**
     * 根据ticket获取用户信息
     * @param ticket
     * @return
     */
    User getUserByTicket(@Param(value = "ticket")String ticket);

}