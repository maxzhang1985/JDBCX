package com.bitauto.ep.fx.jdbcx.Query;

import org.apache.commons.lang.NullArgumentException;

import java.util.ArrayList;
import java.util.List;


/**
 * 查询参数对象，封装常用的查询语法
 * =, <>, >, >=, <, <=, like, in, not in, between
 *
 * @author zhanglei13
 * @date 2018-9-25 下午2:36:27
 *
 */
public class QueryCondition {

    public QueryMode getQueryMode()
    {
        return queryMode;
    }

    public void setQueryMode(QueryMode queryMode)
    {
        this.queryMode = queryMode;
    }

    /**
     * 条件类型
     * NEQ: <>
     * GT: >
     * GE: >=
     * LT: <
     * LE: <=
     * EQ: =
     * LIKE: like
     * IN: in
     * NOTIN: not in
     * BETWEEN: between
     *
     * @author fengli
     * @date 2014-7-8 下午4:41:43
     *
     */
    public static enum Type {
        NEQ,GT,GE,LE,LT,EQ,LIKE,IN,NOTIN,BETWEEN
    }
    private QueryMode queryMode;
    private final String column;
    private final Object value;
    private final Type opt;

    public QueryCondition(String column, Type opt, Object value) {
        this.column = column;
        this.value = value;
        this.opt = opt;
    }

    public List<Object> getValues() {
        List<Object> buf = new ArrayList<Object>();
        if (value instanceof Object[]) {
            for (Object o : (Object[])value) {
                buf.add(o);
            }
        }
        else if(value instanceof QFunc){
            return  buf;
        }
        else {
            buf.add(value);
        }
        return buf;
    }

    /**
     * 转换成sql
     * @return
     */
    public String toSql() {

        String sqlVal = "?";
        if(value instanceof QFunc){
            sqlVal = value.toString();
        }

        if (QueryCondition.Type.EQ == opt) {
            return column + "= " + sqlVal;
        } else if (QueryCondition.Type.NEQ == opt) {
            return column + "<> " + sqlVal;
        } else if (QueryCondition.Type.GT == opt) {
            return column + "> " + sqlVal;
        } else if (QueryCondition.Type.GE == opt) {
            return column + ">= " + sqlVal;
        } else if (QueryCondition.Type.LT == opt) {
            return column + "< " + sqlVal;
        } else if (QueryCondition.Type.LE == opt) {
            return column + "<= " + sqlVal;
        } else if (QueryCondition.Type.IN == opt) {
            Object[] buf = (Object[]) value;
            if (buf != null && buf.length > 0) {
                StringBuilder sb = new StringBuilder(column + " in (");
                for (int i = 0; i<buf.length; i++) {
                    sb.append("?");
                    if (i < (buf.length-1)) {
                        sb.append(",");
                    }
                }
                sb.append(")");
                return sb.toString();
            } else {
                throw new NullArgumentException("in语法错误，内部值不能为空！");
            }
        } else if (QueryCondition.Type.NOTIN == opt) {
            Object[] buf = (Object[]) value;
            if (buf != null && buf.length > 0) {
                StringBuilder sb = new StringBuilder(column + " not in (");
                for (int i = 0; i<buf.length; i++) {
                    sb.append("?");
                    if (i < (buf.length - 1)) {
                        sb.append(",");
                    }
                }
                sb.append(")");
                return sb.toString();
            } else {
                throw new NullArgumentException("not in语法错误，内部值不能为空！");
            }
        } else if (QueryCondition.Type.LIKE == opt) {
            return column + " like '?'";
        } else if (QueryCondition.Type.BETWEEN == opt) {
            return column + " between ? and ?";
        } else {
            return "";
        }
    }

    /**
     * == 等于
     * @param column
     * @param value
     * @return
     */
    public static QueryCondition EQ(String column, Object value) {
        return new QueryCondition(column, QueryCondition.Type.EQ, value);
    }

    /**
     * <> or != 不等于
     * @param column
     * @param value
     * @return
     */
    public static QueryCondition NEQ(String column, Object value) {
        return new QueryCondition(column, QueryCondition.Type.NEQ, value);
    }

    /**
     * > 大于
     * @param column
     * @param value
     * @return
     */
    public static QueryCondition GT(String column, Object value) {
        return new QueryCondition(column, QueryCondition.Type.GT, value);
    }
    /**
     * >= 大于等于
     * @param column
     * @param value
     * @return
     */
    public static QueryCondition GE(String column, Object value) {
        return new QueryCondition(column, QueryCondition.Type.GE, value);
    }

    /**
     * < 小于
     * @param column
     * @param value
     * @return
     */
    public static QueryCondition LT(String column, Object value) {
        return new QueryCondition(column, QueryCondition.Type.LT, value);
    }
    /**
     * <= 小于等于
     * @param column
     * @param value
     * @return QueryCondition
     */
    public static QueryCondition LE(String column, Object value) {
        return new QueryCondition(column, QueryCondition.Type.LE, value);
    }

    /**
     * like 字符检索 %w%
     * @param column
     * @param value
     * @return
     */
    public static QueryCondition LIKE(String column, Object value) {
        return new QueryCondition(column, QueryCondition.Type.IN, value);
    }
    /**
     * In 是否在集合内
     * @param column
     * @param value
     * @return
     */
    public static QueryCondition IN(String column, Object[] values) {
        return new QueryCondition(column, QueryCondition.Type.IN, values);
    }
    /**
     * Not In 不在集合内
     * @param column
     * @param value
     * @return
     */
    public static QueryCondition NOTIN(String column, Object[] values) {
        return new QueryCondition(column, QueryCondition.Type.NOTIN, values);
    }

    /**
     * BETWEEN 是否在区间内
     * @param column
     * @param value
     * @return
     */
    public static QueryCondition BETWEEN(String column, Object start, Object end) {
        return new QueryCondition(column, QueryCondition.Type.BETWEEN, new Object[]{start, end});
    }
}