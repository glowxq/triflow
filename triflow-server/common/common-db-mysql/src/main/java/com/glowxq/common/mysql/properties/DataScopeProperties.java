package com.glowxq.common.mysql.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "mybatis-flex.data-scope")
public class DataScopeProperties {

    private Boolean enabled = true;

    private List<String> ignoreTables;

}
