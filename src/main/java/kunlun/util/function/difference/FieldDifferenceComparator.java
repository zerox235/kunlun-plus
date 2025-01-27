/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.util.function.difference;

import kunlun.core.annotation.FieldMeta;
import kunlun.core.function.BiFunction;
import kunlun.exception.ExceptionUtils;
import kunlun.reflect.ReflectUtils;
import kunlun.util.ArrayUtils;
import kunlun.util.Assert;
import kunlun.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static kunlun.common.constant.Symbols.EMPTY_STRING;

/**
 * The field difference comparator.
 * @author Kahle
 */
public class FieldDifferenceComparator implements BiFunction<Object, Object, List<FieldCompareResult>> {
    protected final boolean ignoreNullNewValue;

    public FieldDifferenceComparator(boolean ignoreNullNewValue) {

        this.ignoreNullNewValue = ignoreNullNewValue;
    }

    public FieldDifferenceComparator() {

        this(false);
    }

    protected String getDescription(Field field) {
        FieldMeta annotation = field.getAnnotation(FieldMeta.class);
        return annotation != null ? annotation.description() : EMPTY_STRING;
    }

    protected List<FieldEntity> getFieldEntities(Object object) {
        try {
            Field[] fields = ReflectUtils.getDeclaredFields(object.getClass());
            if (ArrayUtils.isEmpty(fields)) { return emptyList(); }
            List<FieldEntity> result = new ArrayList<FieldEntity>();
            for (Field field : fields) {
                ReflectUtils.makeAccessible(field);
                FieldEntity entity = new FieldEntity();
                entity.setName(field.getName());
                entity.setValue(field.get(object));
                entity.setDescription(getDescription(field));
                result.add(entity);
            }
            return result;
        } catch (Exception e) { throw ExceptionUtils.wrap(e); }
    }

    protected boolean equals(Object oldValue, Object newValue) {

        return ObjectUtils.equals(oldValue, newValue);
    }

    @Override
    public List<FieldCompareResult> apply(Object oldData, Object newData) {
        // Get field entities.
        List<FieldEntity> newList = getFieldEntities(Assert.notNull(newData));
        List<FieldEntity> oldList = getFieldEntities(Assert.notNull(oldData));
        // Convert leftList to map.
        Map<String, FieldEntity> oldMap = new LinkedHashMap<String, FieldEntity>();
        for (FieldEntity entity : oldList) {
            oldMap.put(entity.getName(), entity);
        }
        // Do compare (Take the data on the newData as the baseline).
        List<FieldCompareResult> list = new ArrayList<FieldCompareResult>();
        for (FieldEntity newEntity : newList) {
            // Get newData name and newData value.
            Object newValue = newEntity.getValue();
            String name = newEntity.getName();
            // Handle case where the value is null.
            if (ignoreNullNewValue && newValue == null) { continue; }
            // Get oldData entity and oldData value.
            FieldEntity oldEntity = oldMap.get(name);
            if (oldEntity == null) { continue; }
            Object oldValue = oldEntity.getValue();
            // Compare oldData value and newData value.
            if (equals(oldValue, newValue)) { continue; }
            // Build result.
            FieldCompareResult result = new FieldCompareResult();
            result.setName(name);
            result.setDescription(newEntity.getDescription());
            result.setOldValue(oldValue);
            result.setNewValue(newValue);
            list.add(result);
        }
        return list;
    }

    // ====

    protected static class FieldEntity {
        private String description;
        private String name;
        private Object value;

        public String getName() {

            return name;
        }

        public void setName(String name) {

            this.name = name;
        }

        public String getDescription() {

            return description;
        }

        public void setDescription(String description) {

            this.description = description;
        }

        public Object getValue() {

            return value;
        }

        public void setValue(Object value) {

            this.value = value;
        }
    }

    // ====
}
