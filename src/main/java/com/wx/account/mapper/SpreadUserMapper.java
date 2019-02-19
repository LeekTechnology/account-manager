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
     * @param uId
     * @param suId
     * @return
     */
    SpreadUser querySpreadInfoByOpenid(@Param(value = "uId")Long uId, @Param(value = "suId")Long suId);

    /**
     * 查询推广次数
     * @param id
     * @return
     */
    Integer querySpreadNum(@Param(value = "id")Long id);

    /**
     * 重置用户的推广信息
     * @param id
     * @return
     */
    int delSpreadInfo(@Param(value = "id")Long id);
}