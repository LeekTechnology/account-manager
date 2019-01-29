package com.wx.account.service.impl;

import com.wx.account.mapper.SpreadUserMapper;
import com.wx.account.model.SpreadUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2019/1/29.
 */
@Service
public class SpreadUserServiceImpl {

    @Autowired
    private SpreadUserMapper spreadUserMapper;

    public void save(SpreadUser spreadUser) {
        spreadUserMapper.insert(spreadUser);
    }
}
