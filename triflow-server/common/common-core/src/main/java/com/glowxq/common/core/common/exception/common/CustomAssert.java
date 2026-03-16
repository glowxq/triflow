package com.glowxq.common.core.common.exception.common;

import com.glowxq.common.core.util.StringUtils;

public interface CustomAssert {

    /**
     * 创建异常
     *
     * @param args
     *            异常信息参数
     * @return 异常
     */
    BaseException newException(Object... args);

    /**
     * 创建异常
     *
     * @param t
     *            Throwable
     * @param args
     *            异常信息参数
     * @return 异常
     */
    BaseException newException(Throwable t, Object... args);

    /**
     * <p>
     * 断言对象<code>obj</code>为空。如果对象<code>obj</code>为空，则抛出异常
     *
     * @param obj
     *            待判断对象
     */
    default void assertNull(Object obj) {
        if (obj == null) {
            throw newException();
        }
    }

    default void assertBlank(String obj) {
        if (StringUtils.isBlank(obj)) {
            throw newException();
        }
    }

    default void assertNotNull(Object obj) {
        if (obj != null) {
            throw newException();
        }
    }

    default void assertNotNull(Object obj,String message) {
        if (obj != null) {
            throw newException(message);
        }
    }

    /**
     * boolean型的断言 true则抛出异常
     *
     * @param bool
     *            boolean值
     */
    default void assertTrue(boolean bool) {
        if (bool) {
            throw newException();
        }
    }

    /**
     * boolean型的断言 false则抛出异常
     *
     * @param bool
     *            boolean值
     */
    default void assertFalse(boolean bool) {
        if (!bool) {
            throw newException();
        }
    }

    /**
     * boolean型的断言 false则抛出异常（带自定义消息）
     *
     * @param bool    boolean值
     * @param message 自定义错误消息
     */
    default void assertFalse(boolean bool, String message) {
        if (!bool) {
            throw newException(message);
        }
    }

    /**
     * boolean型的断言 true则抛出异常（带自定义消息）
     *
     * @param bool    boolean值
     * @param message 自定义错误消息
     */
    default void assertTrue(boolean bool, String message) {
        if (bool) {
            throw newException(message);
        }
    }

    /**
     * <p>
     * 断言对象<code>obj</code>非空。如果对象<code>obj</code>为空，则抛出异常
     * <p>
     * 异常信息<code>message</code>支持传递参数方式，避免在判断之前进行字符串拼接操作
     *
     * @param obj
     *            待判断对象
     * @param args
     *            message占位符对应的参数列表
     */
    default void assertNull(Object obj, Object... args) {
        if (obj == null) {
            throw newException(args);
        }
    }

    // ==================== 链式调用支持 ====================

    /**
     * 创建一个带自定义消息的断言包装器，支持链式调用
     *
     * @param customMessage 自定义错误消息
     * @return 断言包装器
     */
    default MessagedAssert message(String customMessage) {
        return new MessagedAssert(this, customMessage);
    }

    /**
     * 断言包装器，支持链式调用并携带自定义消息
     */
    class MessagedAssert {

        private final CustomAssert customAssert;

        private final String customMessage;

        public MessagedAssert(CustomAssert customAssert, String customMessage) {
            this.customAssert = customAssert;
            this.customMessage = customMessage;
        }

        public void assertFalse(boolean bool) {
            if (!bool) {
                throw customAssert.newException(customMessage);
            }
        }

        public void assertTrue(boolean bool) {
            if (bool) {
                throw customAssert.newException(customMessage);
            }
        }

        public void assertNull(Object obj) {
            if (obj == null) {
                throw customAssert.newException(customMessage);
            }
        }

        public void assertNotNull(Object obj) {
            if (obj != null) {
                throw customAssert.newException(customMessage);
            }
        }

        public void assertBlank(String str) {
            if (str == null || str.isBlank()) {
                throw customAssert.newException(customMessage);
            }
        }

    }

}
