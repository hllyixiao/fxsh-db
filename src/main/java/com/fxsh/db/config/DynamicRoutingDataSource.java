package com.fxsh.db.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源的路由实现
 * @author hell
 * @date 2018/6/28
 * @since 1.0.0
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    //private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final ThreadLocal<DatabaseType> contextHolder = new ThreadLocal<DatabaseType>();

    @Override
    protected Object determineCurrentLookupKey() {
        //logger.info("Current DataSource is [{}]",
        // DynamicDataSourceContextHolder.getDataSourceKey());
        return contextHolder.get();
    }

    /**
     * 数据源枚举
     */
    public enum DatabaseType {
        Master, Slave
    }

    /**
     * 设置主数据源
     */
    public static void master(){
        contextHolder.set(DatabaseType.Master);
    }

    /**
     * 设置从数据源
     */
    public static void slave(){
        contextHolder.set(DatabaseType.Slave);
    }

    /**
     * 清除数据源名
     */
    public static void clearDB() {
        contextHolder.remove();
    }
}
