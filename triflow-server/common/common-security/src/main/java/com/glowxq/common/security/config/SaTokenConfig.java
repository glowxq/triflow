package com.glowxq.common.security.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import com.glowxq.common.security.core.MySaCheckPermissionHandler;
import com.glowxq.common.security.core.interceptor.MySaInterceptor;
import com.glowxq.common.security.pojo.WhitelistProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 配置
 * <p>
 * 白名单配置说明：
 * - 白名单路径从 router.whitelist 配置读取
 * - 路径相对于 context-path，即不含模块前缀（如 /base）
 * - 例如：配置 /public/** 会匹配请求 /base/public/sms/send
 *
 * @author glowxq
 * @since 2024/1/22 16:36
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SaTokenConfig implements WebMvcConfigurer {

    private final WhitelistProperties whitelistProperties;

    @Bean
    public StpLogic getStpLogicJwt() {
        // Sa-Token 整合 jwt (简单模式)
        return new StpLogicJwtForSimple();
    }

    /**
     * 注册 Sa-Token 路由拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册自定义 @SaCheckPermission 注解 Handler
        SaAnnotationStrategy.instance.registerAnnotationHandler(new MySaCheckPermissionHandler());

        // 获取白名单配置
        List<String> whitelist = whitelistProperties.getWhitelist();
        if (whitelist == null) {
            whitelist = new ArrayList<>();
        }

        // 打印白名单配置信息
        log.info("========================================");
        log.info("注册 Sa-Token 路由拦截器");
        log.info("白名单配置数量: {}", whitelist.size());
        whitelist.forEach(path -> log.info("  - {}", path));
        log.info("========================================");

        // 将白名单转换为数组供 SaRouter 使用
        String[] excludePathArray = whitelist.toArray(new String[0]);

        // 注册拦截器
        // 注意：只使用 SaRouter.notMatch 进行白名单排除，不使用 Spring 的 excludePathPatterns
        // 因为 SaRouter 的路径匹配更灵活，支持 Ant 风格
        registry.addInterceptor(new MySaInterceptor(handler ->
                        SaRouter.match("/**")
                                .notMatch(excludePathArray)
                                .check(r -> StpUtil.checkLogin())
                ))
                .addPathPatterns("/**");
    }
}
