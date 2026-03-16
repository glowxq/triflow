package com.glowxq.common.mysql.listener;

import cn.dev33.satoken.stp.StpUtil;
import com.glowxq.common.security.core.util.LoginUtils;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.logicdelete.impl.DefaultLogicDeleteProcessor;
import com.mybatisflex.core.table.TableInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;



/**
 * 逻辑删除处理： 填充删除人、 删除时间
 */
@Slf4j
public class EntityLogicDeleteListener extends DefaultLogicDeleteProcessor {

    private static final String FIELD_DELETE_TIME = "delete_time";

    private static final String FIELD_DELETE_ID = "delete_id";

    @Override
    public String buildLogicDeletedSet(String logicColumn, TableInfo tableInfo, IDialect iDialect) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(iDialect.wrap(logicColumn)).append(SqlConsts.EQUALS).append(prepareValue(getLogicDeletedValue()));

        List<String> columns = Arrays.asList(tableInfo.getAllColumns());

        if (columns.contains(FIELD_DELETE_TIME)) {
            sqlBuilder.append(", ").append(iDialect.wrap(FIELD_DELETE_TIME)).append(SqlConsts.EQUALS).append("now()");
        }

        boolean isLogin = StpUtil.isLogin();
        if (isLogin && columns.contains(FIELD_DELETE_ID)) {
            sqlBuilder.append(", ").append(iDialect.wrap(FIELD_DELETE_ID)).append(SqlConsts.EQUALS)
                    .append(Objects.requireNonNull(LoginUtils.getLoginUser()).getUserInfo().getId());
        }

        return sqlBuilder.toString();
    }

    private static Object prepareValue(Object value) {
        return (!(value instanceof Number) && !(value instanceof Boolean)) ? "'" + value + "'" : value;
    }
}
