/**
 * Copyright (c) 2014 http://www.jieqianhua.com
 */
package com.wx.account.config.datasource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by changing on 16/11/2.
 */

@Configuration
@EnableTransactionManagement
public class DruidDataSourceConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DruidDataSourceConfiguration.class);

    @Autowired
    private DataSourceProperties properties;

//    @Bean
//    public ServletRegistrationBean druidServlet() {
//        LOGGER.debug("Registered druid servlet");
//        return new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
//    }
//
//    @Bean
//    public FilterRegistrationBean druidFilter() {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//        filterRegistrationBean.setFilter(new WebStatFilter());
//        filterRegistrationBean.addUrlPatterns("/*");
//        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
//        LOGGER.debug("Registered druid filter");
//        return filterRegistrationBean;
//    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(properties.getDriverClassName());
        druidDataSource.setUrl(properties.getUrl());
        druidDataSource.setUsername(properties.getUsername());
        druidDataSource.setPassword(properties.getPassword());
        druidDataSource.setInitialSize(properties.getInitialSize());
        druidDataSource.setMinIdle(properties.getMinIdle());
        druidDataSource.setMaxActive(properties.getMaxActive());
        druidDataSource.setValidationQuery(properties.getValidationQuery());

        List<Filter> filterList=new ArrayList<>();
        filterList.add(wallFilter());
        druidDataSource.setProxyFilters(filterList);
        try {
            LOGGER.info("Setting 'application.yml' into druid");
            druidDataSource.setFilters("stat, wall");
        } catch (SQLException e) {
            throw new IllegalStateException("Could not initial Druid DataSource\n" + e);
        }
        return druidDataSource;
    }

    @Bean
    public WallFilter wallFilter(){
        WallFilter wallFilter=new WallFilter();
        wallFilter.setConfig(wallConfig());
        return  wallFilter;
    }
    @Bean
    public WallConfig wallConfig(){
        WallConfig config =new WallConfig();
        config.setMultiStatementAllow(true);//允许一次执行多条语句
//        config.setNoneBaseStatementAllow(true);//允许非基本语句的其他语句
        return config;
    }
}
