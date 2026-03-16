package com.glowxq.triflow.base.captcha.service;

import com.glowxq.common.redis.RedisUtils;
import com.glowxq.triflow.base.captcha.pojo.vo.CaptchaStatusVO;
import com.glowxq.triflow.base.captcha.pojo.vo.CaptchaVO;
import com.glowxq.triflow.base.system.enums.ConfigKeyEnum;
import com.glowxq.triflow.base.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 * <p>
 * 提供图形验证码的生成和验证功能，使用 Java AWT 实现，避免依赖 Hutool。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final SysConfigService configService;

    /**
     * 是否启用验证码
     */
    @Value("${triflow.captcha.enabled:false}")
    private Boolean captchaEnabled;

    /**
     * 验证码过期时间（秒）
     */
    @Value("${triflow.captcha.expire:300}")
    private Integer captchaExpire;

    /**
     * 验证码 Redis Key 前缀
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    /**
     * 验证码字符集
     */
    private static final String CAPTCHA_CHARS = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";

    /**
     * 安全随机数生成器
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 获取验证码状态
     *
     * @return 验证码状态
     */
    public CaptchaStatusVO getCaptchaStatus() {
        return new CaptchaStatusVO(captchaEnabled);
    }

    /**
     * 生成图片验证码
     *
     * @return 验证码信息
     */
    public CaptchaVO generateCaptcha() {
        // 生成验证码文本
        CaptchaPayload payload = generateCaptchaPayload();

        // 生成验证码图片
        String imageBase64 = generateCaptchaImage(payload.text(), 120, 40);

        // 生成唯一 key
        String captchaKey = generateUuid();

        // 存储到 Redis
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        RedisUtils.setValue(redisKey, payload.code().toLowerCase(), (long) captchaExpire, TimeUnit.SECONDS);

        log.debug("生成验证码: key={}, code={}, type={}", captchaKey, payload.code(), payload.type());

        return new CaptchaVO(captchaKey, imageBase64);
    }

    /**
     * 验证验证码
     *
     * @param captchaKey  验证码 key
     * @param captchaCode 用户输入的验证码
     * @return 是否验证成功
     */
    public boolean verifyCaptcha(String captchaKey, String captchaCode) {
        if (!captchaEnabled) {
            return true;
        }

        if (captchaKey == null || captchaCode == null) {
            return false;
        }

        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        Object storedCode = RedisUtils.getValue(redisKey);

        if (storedCode == null) {
            log.warn("验证码已过期或不存在: key={}", captchaKey);
            return false;
        }

        // 验证后删除
        RedisUtils.removeKey(redisKey);

        // 忽略大小写比较
        boolean result = captchaCode.toLowerCase().equals(storedCode.toString().toLowerCase());
        log.debug("验证码校验: key={}, input={}, stored={}, result={}",
                captchaKey, captchaCode, storedCode, result);

        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 生成验证码内容
     */
    private CaptchaPayload generateCaptchaPayload() {
        String type = getCaptchaType();
        if ("math".equals(type)) {
            int numA = 1 + RANDOM.nextInt(9);
            int numB = 1 + RANDOM.nextInt(9);
            int result = numA + numB;
            String text = numA + " + " + numB + " =";
            return new CaptchaPayload(String.valueOf(result), text, type);
        }

        String code = generateCode(4);
        return new CaptchaPayload(code, code, type);
    }

    /**
     * 获取验证码类型
     */
    private String getCaptchaType() {
        String type = configService.getValueByKey(ConfigKeyEnum.SYS_CAPTCHA_TYPE.getKey(), "math");
        if (StringUtils.isBlank(type)) {
            return "math";
        }
        return type.trim().toLowerCase();
    }

    /**
     * 生成验证码文本
     *
     * @param length 验证码长度
     * @return 验证码文本
     */
    private String generateCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CAPTCHA_CHARS.charAt(RANDOM.nextInt(CAPTCHA_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 生成验证码图片并返回 Base64 编码
     *
     * @param code   验证码文本
     * @param width  图片宽度
     * @param height 图片高度
     * @return Base64 编码的图片数据（包含 data URI 前缀）
     */
    private String generateCaptchaImage(String code, int width, int height) {
        // 创建图片缓冲区
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 填充背景
        g2d.setColor(new Color(245, 245, 245));
        g2d.fillRect(0, 0, width, height);

        // 绘制干扰线
        drawInterferenceLines(g2d, width, height, 20);

        // 绘制验证码文字
        drawText(g2d, code, width, height);

        // 添加噪点
        addNoise(image, 50);

        g2d.dispose();

        // 转换为 Base64
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            log.error("生成验证码图片失败", e);
            return "";
        }
    }

    /**
     * 绘制干扰线
     *
     * @param g2d    图形上下文
     * @param width  图片宽度
     * @param height 图片高度
     * @param count  干扰线数量
     */
    private void drawInterferenceLines(Graphics2D g2d, int width, int height, int count) {
        for (int i = 0; i < count; i++) {
            g2d.setColor(getRandomLightColor());
            g2d.setStroke(new BasicStroke(1));
            int x1 = RANDOM.nextInt(width);
            int y1 = RANDOM.nextInt(height);
            int x2 = RANDOM.nextInt(width);
            int y2 = RANDOM.nextInt(height);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * 绘制验证码文字
     *
     * @param g2d    图形上下文
     * @param code   验证码文本
     * @param width  图片宽度
     * @param height 图片高度
     */
    private void drawText(Graphics2D g2d, String code, int width, int height) {
        int fontSize = height - 10;
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g2d.setFont(font);

        int charWidth = width / (code.length() + 1);
        int baseline = height - (height - fontSize) / 2 - 5;

        for (int i = 0; i < code.length(); i++) {
            g2d.setColor(getRandomDarkColor());

            // 添加随机旋转
            double angle = Math.toRadians(RANDOM.nextInt(20) - 10);
            int x = charWidth * (i + 1) - charWidth / 2;

            g2d.rotate(angle, x, baseline);
            g2d.drawString(String.valueOf(code.charAt(i)), x - charWidth / 4, baseline);
            g2d.rotate(-angle, x, baseline);
        }
    }

    /**
     * 添加噪点
     *
     * @param image 图片
     * @param count 噪点数量
     */
    private void addNoise(BufferedImage image, int count) {
        int width = image.getWidth();
        int height = image.getHeight();
        for (int i = 0; i < count; i++) {
            int x = RANDOM.nextInt(width);
            int y = RANDOM.nextInt(height);
            image.setRGB(x, y, getRandomDarkColor().getRGB());
        }
    }

    /**
     * 获取随机浅色
     *
     * @return 随机浅色
     */
    private Color getRandomLightColor() {
        return new Color(
                150 + RANDOM.nextInt(100),
                150 + RANDOM.nextInt(100),
                150 + RANDOM.nextInt(100)
        );
    }

    /**
     * 获取随机深色
     *
     * @return 随机深色
     */
    private Color getRandomDarkColor() {
        return new Color(
                RANDOM.nextInt(100),
                RANDOM.nextInt(100),
                RANDOM.nextInt(100)
        );
    }

    /**
     * 验证码内容
     *
     * @param code  验证码结果（用于校验）
     * @param text  显示文本（用于绘制）
     * @param type  验证码类型
     */
    private record CaptchaPayload(String code, String text, String type) {}

    /**
     * 生成 UUID（不含连字符）
     *
     * @return UUID 字符串
     */
    private String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
