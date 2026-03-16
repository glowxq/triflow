package com.glowxq.common.redis.handler;

import com.glowxq.common.core.common.entity.UserPermissionChangeMessage;

public interface UserPermissionChangeMsgHandler {

    void handlerMsg(UserPermissionChangeMessage message);
}