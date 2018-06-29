package com.fxsh.db.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.fxsh.db.data.Base;
import com.fxsh.db.data.MasterSource;
import com.fxsh.db.data.SlaveSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author hell
 * @date 2018/6/28
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.fxsh.db.mapper",
        sqlSessionFactoryRef = "sqlSessionFactory" )
public class DataBaseConfiguration {

    @Value("${fxsh.mybatis.xml.mapper.location:}")
    private String xmlMapperLocation;

    @Value("${fxsh.mybatis.xml.config.location:}")
    private String xmlConfigLocation;

    @Autowired(required = false)
    private MasterSource masterSource;

    @Autowired(required = false)
    private SlaveSource slaveSource;

    private DataSource getDataSource(Base base) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(base.getUrl());
        dataSource.setDriverClassName(base.getDriverClassName());
        dataSource.setUsername(base.getUserName());
        dataSource.setPassword(base.getPassword());
        try {
            dataSource.setFilters("stat,wall");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    @Bean
    public DynamicRoutingDataSource dynamicDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>(2);
        DataSource defaultDataSource = null;
        if(Objects.nonNull(masterSource)){
            DataSource master = getDataSource(masterSource);
            targetDataSources.put(DynamicRoutingDataSource.DatabaseType.Master, master);
            defaultDataSource = master;
        }
        if(Objects.nonNull(slaveSource)){
            DataSource slave = getDataSource(slaveSource);
            targetDataSources.put(DynamicRoutingDataSource.DatabaseType.Slave, slave);
            if(Objects.isNull(defaultDataSource)){
                defaultDataSource = slave;
            }
        }
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        // 该方法是AbstractRoutingDataSource的方法
        dataSource.setTargetDataSources(targetDataSources);
        dataSource.setDefaultTargetDataSource(defaultDataSource);
        return dataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(DynamicRoutingDataSource dynamicRoutingDataSource) throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicRoutingDataSource);
        if(!Objects.equals("",xmlMapperLocation)){
            sqlSessionFactoryBean.setMapperLocations(
                    new PathMatchingResourcePatternResolver().getResources(xmlMapperLocation));
        }
        if(!Objects.equals("",xmlConfigLocation)){
            sqlSessionFactoryBean.setConfigLocation(
                    new PathMatchingResourcePatternResolver().getResource(xmlConfigLocation));
        }
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 配置声明式事务管理器
     */
    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(DataSource sqlSessionFactory){
        return new DataSourceTransactionManager(sqlSessionFactory);
    }

    @Bean(name = "sqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("sqlSessionFactory") SqlSessionFactory primarySqlSessionFactory){
        return new SqlSessionTemplate(primarySqlSessionFactory);
    }
}
