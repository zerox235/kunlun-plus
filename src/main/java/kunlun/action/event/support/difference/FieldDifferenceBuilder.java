/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.event.support.difference;

import kunlun.common.constant.Nil;
import kunlun.core.Builder;
import kunlun.core.function.BiFunction;
import kunlun.data.bean.BeanUtil;
import kunlun.reflect.ReflectUtil;
import kunlun.util.Assert;
import kunlun.util.CollUtil;
import kunlun.util.function.difference.FieldCompareResult;
import kunlun.util.function.difference.FieldDifferenceComparator;

import java.util.List;

import static kunlun.common.constant.Symbols.EMPTY_STRING;
import static kunlun.util.StrUtil.isNotBlank;

/**
 * The field difference builder.
 * @author Kahle
 */
public class FieldDifferenceBuilder implements Builder {
    private static String oldToNewSeparator = " > ";
    private static String fieldSeparator = ": ";
    private static String lineSeparator = "\r\n";

    public static String getOldToNewSeparator() {

        return oldToNewSeparator;
    }

    public static void setOldToNewSeparator(String oldToNewSeparator) {

        FieldDifferenceBuilder.oldToNewSeparator = Assert.notNull(oldToNewSeparator);
    }

    public static String getFieldSeparator() {

        return fieldSeparator;
    }

    public static void setFieldSeparator(String fieldSeparator) {

        FieldDifferenceBuilder.fieldSeparator = Assert.notNull(fieldSeparator);
    }

    public static String getLineSeparator() {

        return lineSeparator;
    }

    public static void setLineSeparator(String lineSeparator) {

        FieldDifferenceBuilder.lineSeparator = Assert.notNull(lineSeparator);
    }

    // ====

    protected final BiFunction<Object, Object, List<FieldCompareResult>> comparator;
    protected final Object oldData;
    protected final Object newData;

    public FieldDifferenceBuilder(Object oldData, Object newData, Class<?> targetClz,
                                  BiFunction<Object, Object, List<FieldCompareResult>> comparator) {
        Assert.isFalse(newData == null && oldData == null);
        if (newData == null) { newData = ReflectUtil.newInstance(oldData.getClass()); }
        if (oldData == null) { oldData = ReflectUtil.newInstance(newData.getClass()); }
        if (targetClz != null && !targetClz.equals(Object.class)) {
            if (!targetClz.isInstance(newData)) { newData = BeanUtil.beanToBean(newData, targetClz); }
            if (!targetClz.isInstance(oldData)) { oldData = BeanUtil.beanToBean(oldData, targetClz); }
        }
        this.comparator = Assert.notNull(comparator);
        this.oldData = oldData;
        this.newData = newData;
    }

    public FieldDifferenceBuilder(Object oldData, Object newData, Class<?> targetClz, boolean ignoreNullNewValue) {

        this(oldData, newData, targetClz, new FieldDifferenceComparator(ignoreNullNewValue));
    }

    public FieldDifferenceBuilder(Object oldData, Object newData, Class<?> targetClz) {

        this(oldData, newData, targetClz, new FieldDifferenceComparator());
    }

    @Deprecated
    public FieldDifferenceBuilder(Object oldData, Object newData, boolean ignoreNullNewValue) {

        this(oldData, newData, Nil.CLZ, new FieldDifferenceComparator(ignoreNullNewValue));
    }

    @Deprecated
    public FieldDifferenceBuilder(Object oldData, Object newData) {

        this(oldData, newData, Nil.CLZ);
    }

    protected void preProcess(List<FieldCompareResult> results) {

    }

    @Override
    public String build() {
        List<FieldCompareResult> results = comparator.apply(oldData, newData);
        if (CollUtil.isEmpty(results)) { return EMPTY_STRING; }
        preProcess(results);
        StringBuilder builder = new StringBuilder();
        for (FieldCompareResult result : results) {
            if (result == null) { continue; }
            builder.append(isNotBlank(result.getDescription()) ? result.getDescription() : result.getName());
            builder.append(fieldSeparator);
            builder.append(result.getOldValue() != null ? result.getOldValue() : EMPTY_STRING)
                    .append(oldToNewSeparator)
                    .append(result.getNewValue() != null ? result.getNewValue() : EMPTY_STRING);
            builder.append(lineSeparator);
        }
        return builder.toString();
    }

}
