package com.wx.account.service.impl;

import com.wx.account.mapper.UserMapper;
import com.wx.account.model.User;
import com.wx.account.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by supermrl on 2019/1/17.
 */
@Slf4j
@Service
public class DemoServiceImpl implements DemoService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserByName(String name) {
        User user = userMapper.getUserByName(name);
        return user;
    }
}
