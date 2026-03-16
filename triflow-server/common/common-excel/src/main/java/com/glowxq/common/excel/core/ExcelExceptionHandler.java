package com.glowxq.common.excel.core;

import cn.idev.excel.exception.ExcelAnalysisException;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.core.common.enums.ErrorCodeEnum;
import com.glowxq.common.core.common.exception.BaseExceptionHandler;
import com.glowxq.common.core.common.feishu.utils.FeishuMessageUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author glowxq
 * @since 2024/12/27 16:21
 */
@Order(Integer.MIN_VALUE)
@RestControllerAdvice
public class ExcelExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(value = ExcelAnalysisException.class)
    public ApiResult<Object> handlerNotPermissionException(ExcelAnalysisException e, HttpServletRequest request, HttpServletResponse response) {
        String msg = super.logAndPackError(request, response, e);
        FeishuMessageUtils.sendInternalMessage(msg);

        // 使用自定义消息返回错误
        return ApiResult.error(ErrorCodeEnum.EXCEL_IMPORT_ERROR.getCode(), e.getMessage());
    }

}
