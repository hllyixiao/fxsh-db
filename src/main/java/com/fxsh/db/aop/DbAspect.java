package com.fxsh.db.aop;

import com.fxsh.db.anno.MasterDb;
import com.fxsh.db.anno.SlaveDb;
import com.fxsh.db.config.DynamicRoutingDataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @author hell
 * @date 2018/6/28
 * @since 1.0.0
 */
@Aspect
@Component
public class DbAspect {

    @Around(value = "@annotation(masterDb)")
    public void doMasterDb(final ProceedingJoinPoint joinPoint,
                           final MasterDb masterDb) throws Throwable {
        // 切换到master数据源
        DynamicRoutingDataSource.master();
        joinPoint.proceed();
        DynamicRoutingDataSource.clearDB();

    }

    @Before(value = "@annotation(slaveDb)")
    public void doSlaveDb(final JoinPoint point,
                          final SlaveDb slaveDb) throws Throwable {
        // 切换到slave数据源
        DynamicRoutingDataSource.slave();
//        joinPoint.proceed();
//        DynamicRoutingDataSource.clearDB();
    }
}
