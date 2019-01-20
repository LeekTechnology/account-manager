package com.wx.account.mapper;

import com.wx.account.model.UserOther;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserOtherMapper {
    int deleteByPrimaryKey(Long id);

    /**
     * 保存推广信息
     * @param record
     * @return
     */
    int insert(UserOther record);

    int insertSelective(UserOther record);

    UserOther selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserOther record);

    int updateByPrimaryKey(UserOther record);

    /**
     * 重置用户推广信息
     * @param openid
     */
    void updateSpreadAction(@Param(value = "openid") String openid);

    /**
     * 查询推广次数
     * @param openid
     * @return
     */
    Integer querySpreadNum(@Param(value = "openid")String openid);

    /**
     * 根据订阅者和推广者的openid查询数据
     * 防止相互关注加入 or条件
     * @param openid
     * @param reference
     * @return
     */
    UserOther querySpreadInfoByOpenid(@Param(value = "openid")String openid, @Param(value = "reference")String reference);
}