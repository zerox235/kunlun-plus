/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support.jackson.util.scene;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JavaType;
import kunlun.common.constant.Nil;
import kunlun.data.json.support.jackson.util.JsonSceneManager.SceneSerializer;
import kunlun.util.NumberUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static java.math.RoundingMode.HALF_UP;
import static kunlun.util.Assert.notBlank;
import static kunlun.util.Assert.notNull;
import static kunlun.util.NumberUtil.toBigDecimal;
import static kunlun.util.StrUtil.isNotBlank;

/**
 * BigDecimalSerializer.
 * @author Kahle
 */
public class BigDecimalSerializer implements SceneSerializer {
    private final RoundingMode roundingMode;
    private final String pattern;
    private final BigDecimal threshold;

    public BigDecimalSerializer(String pattern, RoundingMode roundingMode, BigDecimal threshold) {
        this.roundingMode = notNull(roundingMode);
        this.pattern = notBlank(pattern);
        this.threshold = threshold;
    }

    public BigDecimalSerializer(String pattern, RoundingMode roundingMode, Number threshold) {

        this(pattern, roundingMode, threshold != null ? toBigDecimal(threshold) : null);
    }

    public BigDecimalSerializer(String pattern, RoundingMode roundingMode, String threshold) {

        this(pattern, roundingMode, isNotBlank(threshold) ? toBigDecimal(threshold) : null);
    }

    public BigDecimalSerializer(String pattern, RoundingMode roundingMode) {

        this(pattern, roundingMode, Nil.FLT6);
    }

    public BigDecimalSerializer() {

        this("0.00", HALF_UP, Nil.FLT6);
    }

    @Override
    public Object serialize(Object rawData, JavaType fieldType, Map<String, String> configs, ObjectCodec codec) {
        if (rawData == null) { return null; }
        if (!(rawData instanceof BigDecimal)) { return rawData; }
        return NumberUtil.format(rawData, pattern, roundingMode, threshold);
    }
}
