package com.glowxq.common.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectIdsDTO {

    @Schema(description = "选择的标识数组")
    private List<Long> ids = new ArrayList<>();

}
