package cn.parzulpan.mybatis.cfg;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用于封装查询时的必要信息，包括 执行的 SQL 语句 和 封装结果的实体类全限定类名
 */

public class Mapper {
    private String queryString; // sql 语句
    private String resultType;  // 结果的实体类全限定类名

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}
