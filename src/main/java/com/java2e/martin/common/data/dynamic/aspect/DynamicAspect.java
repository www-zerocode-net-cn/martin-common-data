package com.java2e.martin.common.data.dynamic.aspect;

import cn.hutool.core.convert.Convert;
import cn.hutool.http.HttpStatus;
import com.alibaba.druid.DbType;
import com.java2e.martin.common.bean.system.Log;
import com.java2e.martin.common.core.support.SpringContextHelper;
import com.java2e.martin.common.data.dynamic.annotation.Dynamic;
import com.java2e.martin.common.data.dynamic.datasource.ConnectionSubspaceTypeEnum;
import com.java2e.martin.common.data.dynamic.datasource.LogicDsMeta;
import com.java2e.martin.common.data.dynamic.datasource.SqlHelperAutoDbType;
import com.java2e.martin.common.data.dynamic.datasource.SqlHelperDsContextHolder;
import com.java2e.martin.common.data.dynamic.datasource.SupportedConnectionSubspaceChange;
import com.java2e.martin.common.data.dynamic.spring.SpringSqlHelperDsManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.sql.Connection;
import java.util.Map;


/**
 * @author: liangcan
 * @version: 1.0
 * @date: 2022/12/3 13:57
 * @describtion: 动态切换数据源
 */
@Aspect
@Component
@Slf4j
public class DynamicAspect {
    @Autowired
    private SpringSqlHelperDsManager springSqlHelperDsManager;


    @Around("@annotation(dynamic)")
    public Object around(ProceedingJoinPoint point, Dynamic dynamic) throws Throwable {
        Object obj = null;
        try {
            Object[] args = point.getArgs();
            Map params = (Map) args[0];
            String driverClassName = (String) params.get("driverClassName");
            String url = (String) params.get("url");
            String username = (String) params.get("username");
            String password = (String) params.get("password");
            String key = (String) params.get("key");
            if (!springSqlHelperDsManager.contains(key)) {
                PooledDataSource pooledDataSource = new PooledDataSource(driverClassName, url, username, password);
                Connection connection = pooledDataSource.getConnection();
                ConnectionSubspaceTypeEnum subspaceType = SupportedConnectionSubspaceChange.getSupportedSubspaceType(connection);
                springSqlHelperDsManager.put(key, LogicDsMeta.builder()
                        .datasourceId(key)
                        .expectedSubspaceType(subspaceType)
                        .createFunc(() -> {
                            return pooledDataSource;
                        }).build());
            }
            SqlHelperDsContextHolder.switchTo(key);
            obj = point.proceed();
        } catch (Exception e) {
            throw e;
        } finally {
            //切回默认数据源
            SqlHelperDsContextHolder.switchTo(null);
        }
        return obj;
    }
}
