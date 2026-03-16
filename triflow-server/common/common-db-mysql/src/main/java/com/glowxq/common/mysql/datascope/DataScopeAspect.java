package com.glowxq.common.mysql.datascope;

import com.glowxq.common.core.common.entity.LoginUser;
import com.glowxq.common.core.common.enums.DataScopeEnum;
import com.glowxq.common.mysql.context.DataScopeThreadLocal;
import com.glowxq.common.mysql.properties.DataScopeProperties;
import com.glowxq.common.security.core.util.LoginUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据权限AOP切面
 * <p>
 * 处理标注了 {@link DataScopeHandler} 注解的方法，
 * 在方法执行前后设置和清理数据权限相关的ThreadLocal变量。
 * </p>
 *
 * @author glowxq
 * @since 2024/7/12
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class DataScopeAspect {

    @Autowired
    DataScopeProperties dataScopeProperties;

    /**
     * 定义方法级别切点
     */
    @Pointcut("@annotation(com.glowxq.common.mysql.datascope.DataScopeHandler)")
    public void dataScopePointCut() {
        // 定义切点，不需要实现
    }

    /**
     * 定义类级别切点
     */
    @Pointcut("@within(com.glowxq.common.mysql.datascope.DataScopeHandler)")
    public void dataScopeClassPointCut() {
        // 定义切点，不需要实现
    }

    /**
     * 环绕通知
     *
     * @param point 连接点
     * @return 目标方法执行结果
     * @throws Throwable 执行异常
     */
    @Around("dataScopePointCut() || dataScopeClassPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        try {
            if (LoginUtils.isNotLogin()) {
                log.info("用户未登录,不进行数据权限过滤");
                return point.proceed();
            }
            LoginUser loginUser = LoginUtils.getLoginUser();
            // 获取方法上的数据权限注解
            DataScopeHandler dataScopeHandler = getDataScopeHandle(point);
            if (dataScopeHandler != null && loginUser != null) {
                // 设置ThreadLocal变量
                setupDataScope(dataScopeHandler, loginUser);
            }
            // 执行目标方法
            return point.proceed();
        } finally {
            // 清理ThreadLocal，防止内存泄漏
            DataScopeThreadLocal.clear();
        }
    }

    /**
     * 获取数据权限注解（优先方法级别，回退到类级别）
     *
     * @param joinPoint 连接点
     * @return 数据权限注解
     */
    private DataScopeHandler getDataScopeHandle(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 优先检查方法级别注解
        DataScopeHandler annotation = method.getAnnotation(DataScopeHandler.class);
        if (annotation != null) {
            return annotation;
        }

        // 回退到类级别注解
        return joinPoint.getTarget().getClass().getAnnotation(DataScopeHandler.class);
    }

    /**
     * 设置数据权限ThreadLocal变量
     *
     * @param dataScopeHandler 数据权限注解
     * @param loginUser
     */
    private void setupDataScope(DataScopeHandler dataScopeHandler, LoginUser loginUser) {
        log.debug("loginUser:{},dataScopeProperties:{}", loginUser, dataScopeProperties.toString());
        log.debug("设置数据权限过滤: enabled={}, filterSuperAdmin={}, ignoreTables={}", dataScopeHandler.enabled(), dataScopeHandler.filterSuperAdmin(), dataScopeHandler.ignoreTables());

        if (dataScopeProperties.getEnabled()) {
            // 设置是否启用数据权限
            DataScopeThreadLocal.setDataScopeEnabled(dataScopeHandler.enabled());
        } else {
            DataScopeThreadLocal.setDataScopeEnabled(Boolean.FALSE);
        }

        // 设置是否对超级管理员进行过滤
        DataScopeThreadLocal.setSuperAdminFilter(dataScopeHandler.filterSuperAdmin());

        // 设置忽略的表
        DataScopeThreadLocal.addIgnoreTables(dataScopeProperties.getIgnoreTables());
        DataScopeThreadLocal.addIgnoreTables(dataScopeHandler.ignoreTables());

        DataScopeEnum dataScopeEnum = loginUser.dataScope();
        switch (dataScopeEnum) {
            case All -> {return;}
            case Dept -> DataScopeThreadLocal.addScopeId(loginUser.getDeptId());
            case UserCreate -> DataScopeThreadLocal.addScopeId(loginUser.getUserId());
            case DeptAndChildren -> DataScopeThreadLocal.addScopeIds(loginUser.getDeptAndChildren());
            case JoinGroup -> DataScopeThreadLocal.addScopeIds(loginUser.getGroupIds());
        }
        DataScopeThreadLocal.setDataScopeEnum(dataScopeEnum);
    }
}
