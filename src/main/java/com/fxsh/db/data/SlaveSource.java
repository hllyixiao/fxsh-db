package com.fxsh.db.data;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 从数据库配置
 * @author hell
 * @date 2018/6/28
 * @since 1.0
 */
@Configuration
@ConditionalOnProperty(name = "fxsh.datasource.slave.enable", havingValue = "true")
@ConfigurationProperties(prefix = "fxsh.datasource.slave.config")
public class SlaveSource extends Base{
}
