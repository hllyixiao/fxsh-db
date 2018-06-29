package com.fxsh.db.anno;

import java.lang.annotation.*;

/**
 * @author hell
 * @date 2018/6/28
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MasterDb {
}
