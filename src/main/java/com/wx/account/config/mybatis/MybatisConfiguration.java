package com.wx.account.config.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * @version
 */
//@Configuration
//@EnableConfigurationProperties(MybatisProperties.class)
//@EnableTransactionManagement
//@AutoConfigureAfter({DruidDataSourceConfiguration.class, MybatisAutoConfiguration.class})
public class MybatisConfiguration implements TransactionManagementConfigurer {

    private static Logger log = LoggerFactory.getLogger(MybatisConfiguration.class);


    @Autowired
    private MybatisProperties properties;

    @Autowired
    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Autowired
    private DataSource dataSource;

    @Bean(name = "sessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        log.info("Mybatis SqlSessionFactoryBean set datasource");
        factory.setDataSource(dataSource);
        if (StringUtils.hasText(properties.getConfigLocation())) {
            log.debug("Loading mybatis configuration file");
            factory.setConfigLocation(resourceLoader.getResource(properties.getConfigLocation()));
        } else {
            log.debug("Setting 'flyway.properties' into Mybatis SqlSessionFactoryBean");
            factory.setTypeAliasesPackage(properties.getTypeAliasesPackage());
            factory.setTypeHandlersPackage(properties.getTypeHandlersPackage());
            factory.setMapperLocations(properties.resolveMapperLocations());

        }
        return factory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }
}
