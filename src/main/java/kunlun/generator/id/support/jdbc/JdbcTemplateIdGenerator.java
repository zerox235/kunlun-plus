/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.generator.id.support.jdbc;

import kunlun.common.constant.Nil;
import kunlun.exception.ExceptionUtil;
import kunlun.generator.id.support.AbstractIncrementalIdGenerator;
import kunlun.generator.id.support.IncrementalIdConfig;
import kunlun.util.Assert;
import kunlun.util.CollUtil;
import org.springframework.jdbc.core.*;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static kunlun.common.constant.Numbers.ONE;
import static kunlun.util.Assert.state;

/**
 * 基于 JDBC 的字符串 ID 生成器.<br />
 * @author Kahle
 */
public class JdbcTemplateIdGenerator extends AbstractIncrementalIdGenerator {
    private static final String SQL_QUERY_TEMPLATE = "SELECT `%s` FROM `%s` WHERE `%s` = ? FOR UPDATE;";
    private static final String SQL_INSERT_TEMPLATE = "INSERT INTO `%s` (`%s`, `%s`) VALUES (?, ?);";
    private static final String SQL_UPDATE_TEMPLATE = "UPDATE `%s` SET `%s` = ? WHERE `%s` = ?;";
    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;
    private String valueColumn = "value";
    private String nameColumn = "name";
    private String tableName = "t_identifier";

    public JdbcTemplateIdGenerator(TransactionTemplate transactionTemplate,
                                   JdbcTemplate jdbcTemplate,
                                   IncrementalIdConfig config) {
        super(config);
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplateIdGenerator(IncrementalIdConfig config) {

        this(Nil.<TransactionTemplate>g(), Nil.<JdbcTemplate>g(), config);
    }

    public String getValueColumn() {

        return valueColumn;
    }

    public void setValueColumn(String valueColumn) {

        this.valueColumn = Assert.notBlank(valueColumn);
    }

    public String getNameColumn() {

        return nameColumn;
    }

    public void setNameColumn(String nameColumn) {

        this.nameColumn = Assert.notBlank(nameColumn);
    }

    public String getTableName() {

        return tableName;
    }

    public void setTableName(String tableName) {

        this.tableName = Assert.notBlank(tableName);
    }

    protected TransactionTemplate getTransactionTemplate() {
        state(transactionTemplate != null
                , "In \"%s\", Please rewrite the \"getTransactionTemplate\" method! ", getClass().getName());
        return transactionTemplate;
    }

    protected JdbcTemplate getJdbcTemplate() {
        state(jdbcTemplate != null
                , "In \"%s\", Please rewrite the \"getJdbcTemplate\" method! ", getClass().getName());
        return jdbcTemplate;
    }

    protected void insert(String name, Long value) {
        String insertSql = String.format(SQL_INSERT_TEMPLATE, getTableName(), getNameColumn(), getValueColumn());
        int effect = getJdbcTemplate().update(insertSql, name, value);
        if (effect != ONE) {
            throw new IllegalStateException("Failed to insert the value of identifier. ");
        }
    }

    protected void update(String name, Long value) {
        String updateSql = String.format(SQL_UPDATE_TEMPLATE, getTableName(), getValueColumn(), getNameColumn());
        int effect = getJdbcTemplate().update(updateSql, value, name);
        if (effect != ONE) {
            throw new IllegalStateException("Failed to update the value of identifier. ");
        }
    }

    protected List<Long> query(String name) {
        String querySql = String.format(SQL_QUERY_TEMPLATE, getValueColumn(), getTableName(), getNameColumn());
        RowMapper<Long> rowMapper = new SingleColumnRowMapper<Long>(Long.class);
        ResultSetExtractor<List<Long>> resultSetExtractor = new RowMapperResultSetExtractor<Long>(rowMapper, ONE);
        return getJdbcTemplate().query(querySql, new Object[]{name}, resultSetExtractor);
    }

    @Override
    protected String buildQueryKey(Context context) {

        return getConfig().getName();
    }

    @Override
    protected Long onlyGet(Context context) {
        String name = buildQueryKey(context);
        long stepLength = getConfig().getStepLength();
        List<Long> longList = query(name);
        Long result;
        if (CollUtil.isNotEmpty(longList)) {
            result = CollUtil.getFirst(longList);
            result = result != null
                    ? result + stepLength : stepLength;
        }
        else { result = stepLength; }
        return result;
    }

    protected Long doIncrementAndGet(Context context) {
        String name = buildQueryKey(context);
        long stepLength = getConfig().getStepLength();
        List<Long> longList = query(name);
        Long result;
        if (CollUtil.isNotEmpty(longList)) {
            result = CollUtil.getFirst(longList);
            result = result != null
                    ? result + stepLength : stepLength;
            update(name, result);
        }
        else { insert(name, result = stepLength); }
        return result;
    }

    @Override
    protected Long incrementAndGet(final Context context) {
        return getTransactionTemplate().execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(@Nullable TransactionStatus status) {
                if (status == null) {
                    throw new IllegalArgumentException(
                            "This is a mistake that should not have happened. ");
                }
                try { return doIncrementAndGet(context); }
                catch (Exception e) {
                    status.setRollbackOnly();
                    throw ExceptionUtil.wrap(e);
                }
            }
        });
    }

}
