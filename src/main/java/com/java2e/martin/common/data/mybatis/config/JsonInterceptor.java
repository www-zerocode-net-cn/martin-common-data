package com.java2e.martin.common.data.mybatis.config;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 零代科技
 * @version: 1.0
 * @date: 2023/2/26 20:01
 * @describtion: JsonInterceptor
 */
@Slf4j
@Setter
@Accessors(chain = true)
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class JsonInterceptor extends AbstractSqlParserHandler implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        // SQL 解析
        this.sqlParser(metaObject);

        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        if (SqlCommandType.UPDATE != mappedStatement.getSqlCommandType()
                || StatementType.CALLABLE == mappedStatement.getStatementType()) {
            return invocation.proceed();
        }
        // 针对定义了rowBounds，做为mapper接口方法的参数
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Object paramObj = boundSql.getParameterObject();
        if (paramObj instanceof MapperMethod.ParamMap) {
            Object ew = ((MapperMethod.ParamMap<?>) paramObj).get("ew");
            if (ew instanceof UpdateJsonWrapper) {
                String originalSql = boundSql.getSql();
                ew = (UpdateJsonWrapper) ew;
                String sqlSet = ((UpdateJsonWrapper<?>) ew).getSqlSet();
                if (StringUtils.isNotBlank(sqlSet)) {
                    List<ParameterMapping> mappings = new ArrayList<>(boundSql.getParameterMappings());
                    metaObject.setValue("delegate.boundSql.sql", originalSql);
                    metaObject.setValue("delegate.boundSql.parameterMappings", mappings);
                }
            }
        }
        return invocation.proceed();
    }
}
