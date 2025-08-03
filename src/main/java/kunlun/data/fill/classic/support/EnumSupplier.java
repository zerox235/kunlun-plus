/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.fill.classic.support;

import kunlun.common.constant.Nil;
import kunlun.core.function.Function;
import kunlun.data.CodeDefinition;
import kunlun.data.bean.BeanUtil;
import kunlun.data.fill.DataSupplier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static kunlun.util.Assert.isAssignable;
import static kunlun.util.Assert.notNull;

/**
 * The data supplier based on enumeration.
 * @author Zerox
 */
public class EnumSupplier<E extends Enum<E>> implements DataSupplier {

    public static <E extends Enum<E>> EnumSupplier<E> of(Class<E> enumClass, Function<E, Object> keyMapper) {

        return new EnumSupplier<E>(enumClass, keyMapper);
    }

    public static <E extends Enum<E>> EnumSupplier<E> of(Class<E> enumClass) {

        return new EnumSupplier<E>(enumClass, Nil.<Function<E, Object>>g());
    }

    private final Function<E, Object> keyMapper;
    private final Class<E> enumClass;

    public EnumSupplier(Class<E> enumClass, Function<E, Object> keyMapper) {
        if (keyMapper == null) { isAssignable(CodeDefinition.class, enumClass); }
        this.enumClass = notNull(enumClass);
        this.keyMapper = keyMapper;
    }

    @Override
    public Map<String, Map<String, Object>> acquire(Collection<?> coll) {
        Map<String, Map<String, Object>> enumMap = new LinkedHashMap<String, Map<String, Object>>();
        for (E eEnum : enumClass.getEnumConstants()) {
            if (keyMapper != null) {
                enumMap.put(String.valueOf(keyMapper.apply(eEnum)), BeanUtil.beanToMap(eEnum));
            } else {
                enumMap.put(String.valueOf(((CodeDefinition) eEnum).getCode()), BeanUtil.beanToMap(eEnum));
            }
        }
        return enumMap;
    }
}
