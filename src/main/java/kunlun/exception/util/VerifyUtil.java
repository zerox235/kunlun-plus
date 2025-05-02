/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.exception.util;

import kunlun.data.CodeDefinition;
import kunlun.data.validation.ValidatorUtil;
import kunlun.exception.BusinessException;
import kunlun.util.ArrayUtil;
import kunlun.util.IterUtil;
import kunlun.util.MapUtil;
import kunlun.util.StrUtil;

import java.util.Map;

/**
 * The verify tools.
 * @author Kahle
 */
public class VerifyUtil {

    // region ======== is false, is true ========

    public static void isFalse(boolean expression, CodeDefinition errorCode, Object... arguments) {
        if (expression) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isFalse(boolean expression, String message, Object... arguments) {
        if (expression) {
            throw new BusinessException(message, arguments);
        }
    }

    public static void isTrue(boolean expression, CodeDefinition errorCode, Object... arguments) {
        if (!expression) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isTrue(boolean expression, String message, Object... arguments) {
        if (!expression) {
            throw new BusinessException(message, arguments);
        }
    }
    // endregion ======== is false, is true ========


    // region ======== is null, not null ========

    public static void isNull(Object object, CodeDefinition errorCode, Object... arguments) {
        if (object != null) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isNull(Object object, String message, Object... arguments) {
        if (object != null) {
            throw new BusinessException(message, arguments);
        }
    }

    public static <T> T notNull(T object, CodeDefinition errorCode, Object... arguments) {
        if (object == null) {
            throw new BusinessException(errorCode, arguments);
        }
        return object;
    }

    public static <T> T notNull(T object, String message, Object... arguments) {
        if (object == null) {
            throw new BusinessException(message, arguments);
        }
        return object;
    }
    // endregion ======== is null, not null ========


    // region ======== is empty, not empty >> bytes array ========

    public static void isEmpty(byte[] array, CodeDefinition errorCode, Object... arguments) {
        if (ArrayUtil.isNotEmpty(array)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isEmpty(byte[] array, String message, Object... arguments) {
        if (ArrayUtil.isNotEmpty(array)) {
            throw new BusinessException(message, arguments);
        }
    }

    public static byte[] notEmpty(byte[] array, CodeDefinition errorCode, Object... arguments) {
        if (ArrayUtil.isEmpty(array)) {
            throw new BusinessException(errorCode, arguments);
        }
        return array;
    }

    public static byte[] notEmpty(byte[] array, String message, Object... arguments) {
        if (ArrayUtil.isEmpty(array)) {
            throw new BusinessException(message, arguments);
        }
        return array;
    }
    // endregion ======== is empty, not empty >> bytes array ========


    // region ======== is empty, not empty >> objects array ========

    public static <T> void isEmpty(T[] array, CodeDefinition errorCode, Object... arguments) {
        if (ArrayUtil.isNotEmpty(array)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static <T> void isEmpty(T[] array, String message, Object... arguments) {
        if (ArrayUtil.isNotEmpty(array)) {
            throw new BusinessException(message, arguments);
        }
    }

    public static <T> T[] notEmpty(T[] array, CodeDefinition errorCode, Object... arguments) {
        if (ArrayUtil.isEmpty(array)) {
            throw new BusinessException(errorCode, arguments);
        }
        return array;
    }

    public static <T> T[] notEmpty(T[] array, String message, Object... arguments) {
        if (ArrayUtil.isEmpty(array)) {
            throw new BusinessException(message, arguments);
        }
        return array;
    }
    // endregion ======== is empty, not empty >> objects array ========


    // region ======== is empty, not empty >> iterable ========

    public static void isEmpty(Iterable<?> collection, CodeDefinition errorCode, Object... arguments) {
        if (IterUtil.isNotEmpty(collection)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isEmpty(Iterable<?> collection, String message, Object... arguments) {
        if (IterUtil.isNotEmpty(collection)) {
            throw new BusinessException(message, arguments);
        }
    }

    public static <E, T extends Iterable<E>> T notEmpty(T collection, CodeDefinition errorCode, Object... arguments) {
        if (IterUtil.isEmpty(collection)) {
            throw new BusinessException(errorCode, arguments);
        }
        return collection;
    }

    public static <E, T extends Iterable<E>> T notEmpty(T collection, String message, Object... arguments) {
        if (IterUtil.isEmpty(collection)) {
            throw new BusinessException(message, arguments);
        }
        return collection;
    }
    // endregion ======== is empty, not empty >> iterable ========


    // region ======== is empty, not empty >> map ========

    public static void isEmpty(Map<?, ?> map, CodeDefinition errorCode, Object... arguments) {
        if (MapUtil.isNotEmpty(map)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isEmpty(Map<?, ?> map, String message, Object... arguments) {
        if (MapUtil.isNotEmpty(map)) {
            throw new BusinessException(message, arguments);
        }
    }

    public static <K, V, T extends Map<K, V>> T notEmpty(T map, CodeDefinition errorCode, Object... arguments) {
        if (MapUtil.isEmpty(map)) {
            throw new BusinessException(errorCode, arguments);
        }
        return map;
    }

    public static <K, V, T extends Map<K, V>> T notEmpty(T map, String message, Object... arguments) {
        if (MapUtil.isEmpty(map)) {
            throw new BusinessException(message, arguments);
        }
        return map;
    }
    // endregion ======== is empty, not empty >> map ========


    // region ======== is empty, not empty, is blank, not blank ========

    public static void isEmpty(CharSequence text, CodeDefinition errorCode, Object... arguments) {
        if (StrUtil.isNotEmpty(text)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isEmpty(CharSequence text, String message, Object... arguments) {
        if (StrUtil.isNotEmpty(text)) {
            throw new BusinessException(message, arguments);
        }
    }

    public static <T extends CharSequence> T notEmpty(T text, CodeDefinition errorCode, Object... arguments) {
        if (StrUtil.isEmpty(text)) {
            throw new BusinessException(errorCode, arguments);
        }
        return text;
    }

    public static <T extends CharSequence> T notEmpty(T text, String message, Object... arguments) {
        if (StrUtil.isEmpty(text)) {
            throw new BusinessException(message, arguments);
        }
        return text;
    }

    public static void isBlank(CharSequence text, CodeDefinition errorCode, Object... arguments) {
        if (StrUtil.isNotBlank(text)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isBlank(CharSequence text, String message, Object... arguments) {
        if (StrUtil.isNotBlank(text)) {
            throw new BusinessException(message, arguments);
        }
    }

    public static <T extends CharSequence> T notBlank(T text, CodeDefinition errorCode, Object... arguments) {
        if (StrUtil.isBlank(text)) {
            throw new BusinessException(errorCode, arguments);
        }
        return text;
    }

    public static <T extends CharSequence> T notBlank(T text, String message, Object... arguments) {
        if (StrUtil.isBlank(text)) {
            throw new BusinessException(message, arguments);
        }
        return text;
    }
    // endregion ======== is empty, not empty, is blank, not blank ========


    // region ======== is contain, not contain >> string ========

    public static <T extends CharSequence> T isContain(String textToSearch, T substring, CodeDefinition errorCode, Object... arguments) {
        if (!textToSearch.contains(substring)) {
            throw new BusinessException(errorCode, arguments);
        }
        return substring;
    }

    public static <T extends CharSequence> T isContain(String textToSearch, T substring, String message, Object... arguments) {
        if (!textToSearch.contains(substring)) {
            throw new BusinessException(message, arguments);
        }
        return substring;
    }

    public static <T extends CharSequence> T notContain(String textToSearch, T substring, CodeDefinition errorCode, Object... arguments) {
        if (textToSearch.contains(substring)) {
            throw new BusinessException(errorCode, arguments);
        }
        return substring;
    }

    public static <T extends CharSequence> T notContain(String textToSearch, T substring, String message, Object... arguments) {
        if (textToSearch.contains(substring)) {
            throw new BusinessException(message, arguments);
        }
        return substring;
    }
    // endregion ======== is contain, not contain >> string ========


    // region ======== is instance of ========

    public static <T> T isInstanceOf(Class<?> type, T obj, CodeDefinition errorCode, Object... arguments) {
        if (!type.isInstance(obj)) {
            throw new BusinessException(errorCode, arguments);
        }
        return obj;
    }

    public static <T> T isInstanceOf(Class<?> type, T obj, String message, Object... arguments) {
        if (!type.isInstance(obj)) {
            throw new BusinessException(message, arguments);
        }
        return obj;
    }
    // endregion ======== is instance of ========


    // region ======== is assignable ========

    public static void isAssignable(Class<?> superType, Class<?> subType, CodeDefinition errorCode, Object... arguments) {
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void isAssignable(Class<?> superType, Class<?> subType, String message, Object... arguments) {
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new BusinessException(message, arguments);
        }
    }
    // endregion ======== is assignable ========


    // region ======== no null elements ========

    public static void noNullElements(Object[] array, CodeDefinition errorCode, Object... arguments) {
        for (Object element : array) {
            if (element == null) {
                throw new BusinessException(errorCode, arguments);
            }
        }
    }

    public static void noNullElements(Object[] array, String message, Object... arguments) {
        for (Object element : array) {
            if (element == null) {
                throw new BusinessException(message, arguments);
            }
        }
    }
    // endregion ======== no null elements ========


    // region ======== validate ========

    public static void validate(String name, Object target, CodeDefinition errorCode, Object... arguments) {
        if (!ValidatorUtil.validateToBoolean(name, target)) {
            throw new BusinessException(errorCode, arguments);
        }
    }

    public static void validate(String name, Object target, String message, Object... arguments) {
        if (!ValidatorUtil.validateToBoolean(name, target)) {
            throw new BusinessException(message, arguments);
        }
    }
    // endregion ======== validate ========

}
