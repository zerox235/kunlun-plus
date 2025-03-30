/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.generator.id.support;

import kunlun.exception.ExceptionUtil;
import kunlun.spring.ApplicationContextUtils;
import kunlun.util.Assert;
import kunlun.util.CollUtil;
import org.springframework.jdbc.core.*;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static kunlun.common.constant.Numbers.ONE;

/**
 * Jdbc string identifier generator.
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
        this.transactionTemplate = Assert.notNull(transactionTemplate);
        this.jdbcTemplate = Assert.notNull(jdbcTemplate);
    }

    public JdbcTemplateIdGenerator(IncrementalIdConfig config) {
        this(ApplicationContextUtils.getBean(TransactionTemplate.class),
                ApplicationContextUtils.getBean(JdbcTemplate.class),
                config
        );
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

    protected void insert(String name, Long value) {
        String insertSql = String.format(SQL_INSERT_TEMPLATE, tableName, nameColumn, valueColumn);
        int effect = jdbcTemplate.update(insertSql, name, value);
        if (effect != ONE) {
            throw new IllegalStateException("Failed to insert the value of identifier. ");
        }
    }

    protected void update(String name, Long value) {
        String updateSql = String.format(SQL_UPDATE_TEMPLATE, tableName, valueColumn, nameColumn);
        int effect = jdbcTemplate.update(updateSql, value, name);
        if (effect != ONE) {
            throw new IllegalStateException("Failed to update the value of identifier. ");
        }
    }

    protected List<Long> query(String name) {
        String querySql = String.format(SQL_QUERY_TEMPLATE, valueColumn, tableName, nameColumn);
        RowMapper<Long> rowMapper = new SingleColumnRowMapper<Long>(Long.class);
        ResultSetExtractor<List<Long>> resultSetExtractor = new RowMapperResultSetExtractor<Long>(rowMapper, ONE);
        return jdbcTemplate.query(querySql, new Object[]{name}, resultSetExtractor);
    }

    protected Long increment(Object... arguments) {
        long stepLength = getConfig().getStepLength();
        String name = getConfig().getName();
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
    protected Long incrementAndGet(final Object... arguments) {
        return transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(@Nullable TransactionStatus status) {
                if (status == null) {
                    throw new IllegalArgumentException(
                            "This is a mistake that should not have happened. ");
                }
                try { return increment(arguments); }
                catch (Exception e) {
                    status.setRollbackOnly();
                    throw ExceptionUtil.wrap(e);
                }
            }
        });
    }

}
