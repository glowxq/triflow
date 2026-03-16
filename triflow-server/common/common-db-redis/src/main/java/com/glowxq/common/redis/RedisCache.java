package com.glowxq.common.redis;

import com.glowxq.common.core.common.entity.DictVO;
import com.glowxq.common.core.common.entity.PointVO;
import com.glowxq.common.core.util.CommonUtils;
import com.glowxq.common.core.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author glowxq
 * @since 2024/1/8 15:38
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCache {

    private final RedisTemplate<Object, Object> redisTemplate;

    // ---------------sys_dict相关----------------
    public void setDict(String dictType, List<DictVO> list) {
        redisTemplate.opsForHash().put(CommonKeyConstants.SYS_DICT, dictType, list);
        redisTemplate.expire(CommonKeyConstants.SYS_DICT, 2, TimeUnit.HOURS);
    }

    public void putAllDict(Map<String, List<DictVO>> dictMap) {
        redisTemplate.opsForHash().putAll(CommonKeyConstants.SYS_DICT, dictMap);
        redisTemplate.expire(CommonKeyConstants.SYS_DICT, 2, TimeUnit.HOURS);
    }

    public Map<String, List<DictVO>> getAllDict() {
        return redisTemplate.<String, List<DictVO>>opsForHash().entries(CommonKeyConstants.SYS_DICT);
    }

    public List<DictVO> getDictByType(String dictType) {
        if (hasHashKey(dictType)) {
            List<DictVO> result = redisTemplate.<String, List<DictVO>>opsForHash().get(CommonKeyConstants.SYS_DICT, dictType);
            return result != null ? result : new ArrayList<>();
        } else {
            return new ArrayList<>();
        }
    }

    public boolean hasKey() {
        // 检查 redisTemplate 是否为 null
        if (redisTemplate == null) {
            log.error("RedisTemplate is null, cannot check key existence");
            return false;
        }

        // 调用 hasKey 方法并检查返回值是否为 null
        Boolean hasKey = redisTemplate.hasKey(CommonKeyConstants.SYS_DICT);
        return Boolean.TRUE.equals(hasKey);
    }

    public boolean hasHashKey(String dictType) {
        return redisTemplate.opsForHash().hasKey(CommonKeyConstants.SYS_DICT, dictType);
    }

    public void clearDict(String dictType) {
        redisTemplate.opsForHash().delete(CommonKeyConstants.SYS_DICT, dictType);
    }

    public void clearDictAll() {
        redisTemplate.delete(CommonKeyConstants.SYS_DICT);
    }

    // ---------------sys_config相关----------------
    public boolean hasConfKey(String key) {
        return redisTemplate.opsForHash().hasKey(CommonKeyConstants.SYS_CONFIG, key);
    }

    public String getConfValue(String key) {
        return (String) redisTemplate.opsForHash().get(CommonKeyConstants.SYS_CONFIG, key);
    }

    public void putConf(String key, String value) {
        redisTemplate.opsForHash().put(CommonKeyConstants.SYS_CONFIG, key, value);
        redisTemplate.expire(CommonKeyConstants.SYS_CONFIG, 2, TimeUnit.HOURS);
    }

    public void clearConf(String key) {
        redisTemplate.opsForHash().delete(CommonKeyConstants.SYS_CONFIG, key);
    }

    // ---------------sys_user用户认证相关----------------

    public Long countPwdErr(String username, long timeout) {
        String key = RedisUtils.getKey(CommonKeyConstants.SYS_PWD_ERR_CNT, username);
        Long increment = redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, timeout, TimeUnit.MINUTES);
        return increment;
    }

    public String getUserInfoKey(String username) {
        return StringUtils.replacePlaceholders(CommonKeyConstants.TOKEN_SESSION, username);
    }

    public void clearUserInfo(String username) {
        redisTemplate.delete(getUserInfoKey(username));
    }

    public void putCaptcha(String requestId, PointVO vo, long timeout) {
        String key = StringUtils.getRealKey(CommonKeyConstants.CAPTCHA_REQUEST_ID, requestId);
        redisTemplate.opsForValue().set(key, vo);
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    public PointVO getCaptcha(String requestId) {
        String key = StringUtils.getRealKey(CommonKeyConstants.CAPTCHA_REQUEST_ID, requestId);
        if (existCaptcha(requestId)) {
            return (PointVO) redisTemplate.opsForValue().get(key);
        } else {
            return null;
        }
    }

    public void clearCaptcha(String requestId) {
        String key = StringUtils.getRealKey(CommonKeyConstants.CAPTCHA_REQUEST_ID, requestId);
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    public boolean existCaptcha(String requestId) {
        String key = StringUtils.getRealKey(CommonKeyConstants.CAPTCHA_REQUEST_ID, requestId);

        // 检查 redisTemplate 是否为 null
        if (redisTemplate == null) {
            log.error("RedisTemplate is null, cannot check key existence");
            return false;
        }

        Boolean hasKey = redisTemplate.hasKey(key);
        // 检查 hasKey 是否为 null
        return Boolean.TRUE.equals(hasKey);
    }

    /**
     * 初始化验证码请求限制
     *
     * @param requestId
     *            请求ID
     * @param timeout
     *            过期时间（分钟）
     */
    public void initializeCaptchaRequestLimit(String requestId, long timeout) {
        String key = StringUtils.getRealKey(CommonKeyConstants.CAPTCHA_REQUEST_LIMIT, requestId);
        redisTemplate.opsForValue().setIfAbsent(key, 0, timeout, TimeUnit.MINUTES);
    }

    public Long countCaptchaRequestLimit(String requestId) {
        String key = StringUtils.getRealKey(CommonKeyConstants.CAPTCHA_REQUEST_LIMIT, requestId);
        Object o = redisTemplate.opsForValue().get(key);
        return CommonUtils.getLongVal(o);
    }

    public Long limitCaptcha(String requestId) {
        String key = StringUtils.getRealKey(CommonKeyConstants.CAPTCHA_REQUEST_LIMIT, requestId);
        return redisTemplate.opsForValue().increment(key);
    }

    // ---------------短信验证码相关----------------

    /**
     * 短信验证码 Key 前缀
     */
    private static final String SMS_CODE_KEY_PREFIX = "sms:code:";

    /**
     * 短信发送限制 Key 前缀
     */
    private static final String SMS_LIMIT_KEY_PREFIX = "sms:limit:";

    /**
     * 保存短信验证码
     *
     * @param phone   手机号
     * @param type    验证码类型（login/register/reset）
     * @param code    验证码
     * @param timeout 过期时间（秒）
     */
    public void putSmsCode(String phone, String type, String code, long timeout) {
        String key = SMS_CODE_KEY_PREFIX + type + ":" + phone;
        redisTemplate.opsForValue().set(key, code);
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取短信验证码
     *
     * @param phone 手机号
     * @param type  验证码类型
     * @return 验证码，不存在返回 null
     */
    public String getSmsCode(String phone, String type) {
        String key = SMS_CODE_KEY_PREFIX + type + ":" + phone;
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * 验证短信验证码
     *
     * @param phone 手机号
     * @param type  验证码类型
     * @param code  验证码
     * @return 是否验证通过
     */
    public boolean verifySmsCode(String phone, String type, String code) {
        String key = SMS_CODE_KEY_PREFIX + type + ":" + phone;
        String storedCode = (String) redisTemplate.opsForValue().get(key);
        if (storedCode != null && storedCode.equals(code)) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    /**
     * 删除短信验证码
     *
     * @param phone 手机号
     * @param type  验证码类型
     */
    public void clearSmsCode(String phone, String type) {
        String key = SMS_CODE_KEY_PREFIX + type + ":" + phone;
        redisTemplate.delete(key);
    }

    /**
     * 检查是否可以发送短信（频率限制）
     *
     * @param phone        手机号
     * @param type         验证码类型
     * @param intervalSecs 发送间隔（秒）
     * @return 是否可以发送
     */
    public boolean canSendSms(String phone, String type, long intervalSecs) {
        String key = SMS_LIMIT_KEY_PREFIX + type + ":" + phone;
        Boolean hasKey = redisTemplate.hasKey(key);
        return !Boolean.TRUE.equals(hasKey);
    }

    /**
     * 设置短信发送限制
     *
     * @param phone        手机号
     * @param type         验证码类型
     * @param intervalSecs 发送间隔（秒）
     */
    public void setSmsLimit(String phone, String type, long intervalSecs) {
        String key = SMS_LIMIT_KEY_PREFIX + type + ":" + phone;
        redisTemplate.opsForValue().set(key, "1");
        redisTemplate.expire(key, intervalSecs, TimeUnit.SECONDS);
    }

}
