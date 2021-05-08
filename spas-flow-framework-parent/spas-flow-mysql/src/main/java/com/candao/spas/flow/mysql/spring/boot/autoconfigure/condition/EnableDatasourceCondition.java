package com.candao.spas.flow.mysql.spring.boot.autoconfigure.condition;

import com.candao.spas.flow.mysql.spring.boot.autoconfigure.constant.RouteDatasourceTypeEnum;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

public class EnableDatasourceCondition extends AllNestedConditions {
    private final RouteDatasourceTypeEnum ROUTE_DATASOURCE_TYPE;

    public EnableDatasourceCondition(RouteDatasourceTypeEnum routeDatasourceTypeEnum) {
        super(ConfigurationPhase.REGISTER_BEAN);
        ROUTE_DATASOURCE_TYPE = routeDatasourceTypeEnum;
    }

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("spring.datasource.route.enableDatasource");
        if (!StringUtils.isEmpty(property)) {
            String[] strings = StringUtils.split(property, ",");
            assert strings != null;
            for (String string : strings) {
                if (string.startsWith(ROUTE_DATASOURCE_TYPE.getName())) {
                    return ConditionOutcome.match("匹配到开启的数据源 " + ROUTE_DATASOURCE_TYPE.getName());
                }
            }
        }
        return ConditionOutcome.noMatch("未匹配到开启的数据源 " + ROUTE_DATASOURCE_TYPE.getName());
    }
}