package com.glowxq.common.excel.convert;

import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.data.WriteCellData;
import com.glowxq.common.core.common.entity.DictVO;
import com.glowxq.common.core.util.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Excel Long 类型转换器
 */
@Getter
@Setter
@Slf4j
public class CustomLongStringConvert extends AbstractExcelDictConvert<Long> {

    public CustomLongStringConvert(Map<String, List<DictVO>> dictmap) {
        super(dictmap);
    }

    private static WriteCellData<Long> formatLongNumber(Long object) {
        String str = CommonUtils.getStringVal(object);
        if (str.length() > 15) {
            return new WriteCellData<>(str);
        } else {
            WriteCellData<Long> cellData = new WriteCellData<>();
            cellData.setType(CellDataTypeEnum.NUMBER);
            cellData.setNumberValue(new BigDecimal(object));
            return cellData;
        }
    }

    @Override
    protected WriteCellData<Long> createWriteCellData(Long object) {
        return formatLongNumber(object);
    }

    @Override
    protected Class<?> getJavaTypeClass() {
        return Long.class;
    }
    @Override
    protected Long convertToJava(String value) {
        return CommonUtils.getLongVal(value);
    }

}
