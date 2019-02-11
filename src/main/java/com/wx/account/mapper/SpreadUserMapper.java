package com.wx.account.mapper;

import com.wx.account.model.SpreadUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface SpreadUserMapper {
    int deleteByPrimaryKey(Long id);

    /**
     * 保存推广信息
     * @param record
     * @return
     */
    int insert(@Param(value = "user")SpreadUser record);

    int insertSelective(SpreadUser record);

    SpreadUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SpreadUser record);

    int updateByPrimaryKey(SpreadUser record);

    /**
     * 查询推广次数
     * @param id
     * @return
     */
    Integer querySpreadCount(@Param(value = "id")Long id);

    /**
     * 根据订阅者和推广者的openid查询数据
     * 防止相互关注加入 or条件
     * @param openid
     * @param reference
     * @return
     */
    SpreadUser querySpreadInfoByOpenid(@Param(value = "openid")String openid, @Param(value = "reference")String reference);
}