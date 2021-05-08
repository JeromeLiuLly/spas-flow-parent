package com.candao.spas.flow.sdk.typehandle;

import com.candao.spas.flow.core.constants.ChainConstants;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class StringToListTypeHandler extends BaseTypeHandler<List<String>> {


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        String str = Joiner.on(",").skipNulls().join(parameter);
        ps.setString(i, str);
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.stringToList(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.stringToList(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.stringToList(cs.getString(columnIndex));
    }


    private List<String> stringToList(String str) {

        // 过滤 [ ] "
        if (!Strings.isNullOrEmpty(str)) {
            str = str.replaceAll(ChainConstants.LEFT_BRACKET, "")
                    .replaceAll(ChainConstants.RIGHT_BRACKET, "")
                    .replaceAll(ChainConstants.QUOTATION, "");
        }
        return Strings.isNullOrEmpty(str) ? new ArrayList<>() : Splitter.on(",").splitToList(str);
    }
}