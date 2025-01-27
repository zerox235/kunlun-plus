/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.event.support.difference;

import kunlun.core.Builder;
import kunlun.core.function.BiFunction;
import kunlun.util.Assert;
import kunlun.util.CollectionUtils;
import kunlun.util.function.difference.FieldCompareResult;
import kunlun.util.function.difference.FieldDifferenceComparator;

import java.util.List;

import static kunlun.common.constant.Symbols.EMPTY_STRING;
import static kunlun.util.StringUtils.isNotBlank;

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

    public FieldDifferenceBuilder(Object oldData, Object newData,
                                  BiFunction<Object, Object, List<FieldCompareResult>> comparator) {
        this.comparator = comparator;
        this.oldData = oldData;
        this.newData = newData;
    }

    public FieldDifferenceBuilder(Object oldData, Object newData, boolean ignoreNullNewValue) {

        this(oldData, newData, new FieldDifferenceComparator(ignoreNullNewValue));
    }

    public FieldDifferenceBuilder(Object oldData, Object newData) {

        this(oldData, newData, new FieldDifferenceComparator());
    }

    protected void preProcess(List<FieldCompareResult> results) {

    }

    @Override
    public String build() {
        List<FieldCompareResult> results = comparator.apply(oldData, newData);
        if (CollectionUtils.isEmpty(results)) { return EMPTY_STRING; }
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
