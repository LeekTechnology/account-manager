package com.wx.account.mapper;

import com.wx.account.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    /**
     * 保存订阅用户
     * @param user
     * @return
     */
    int save(User user);

    void update(User user);

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

    /**
     * 查询列表
     *
     * @param user
     * @return
     */
    List<User> selectList(@Param(value = "user")User user);

    /**
     * 根据ID查询用户
     *
     * @param userId
     * @return
     */
    User getById(@Param("id") Long userId);

    /**
     * 根据openid查询用户信息
     * @param openid
     * @return
     */
    User selectUserByOpenid(@Param("openid")String openid);
}