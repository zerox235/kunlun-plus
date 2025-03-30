/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.generator.id.support;

import kunlun.time.DateUtil;
import kunlun.util.Assert;
import kunlun.util.StrUtil;

import static kunlun.common.constant.Numbers.STR_ZERO;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.common.constant.Symbols.EMPTY_STRING;

/**
 * The abstract incremental identifier generator.
 * @author Kahle
 */
public abstract class AbstractIncrementalIdGenerator
        extends AbstractIdGenerator implements StringIdGenerator {
    private final IncrementalIdConfig config;

    public AbstractIncrementalIdGenerator(IncrementalIdConfig config) {
        Assert.notNull(config, "Parameter \"config\" must not null. ");
        Assert.notBlank(config.getName()
                , "Parameter \"config.name\" must not blank. ");
        this.config = config;
        if (config.getSequenceLength() == null) { config.setSequenceLength(8); }
        if (config.getStepLength() == null) { config.setStepLength(1); }
        if (config.getOffset() == null) { config.setOffset(0L); }
        if (config.getDatePattern() == null) {
            config.setDatePattern("yyyyMMdd");
        }
    }

    public IncrementalIdConfig getConfig() {

        return config;
    }

    /**
     * Increment and get the stored value.
     * @param arguments The arguments at generation time
     * @return The value that is incremented and taken out
     */
    protected abstract Long incrementAndGet(Object... arguments);

    /**
     * Obtain the date string.
     * @param arguments The arguments at generation time
     * @return The date string
     */
    protected String obtainDateString(Object... arguments) {
        String datePattern = config.getDatePattern();
        if (StrUtil.isNotBlank(datePattern)) {
            return DateUtil.format(datePattern);
        }
        return null;
    }

    /**
     * Obtain the prefix.
     * @param arguments The arguments at generation time
     * @return The prefix
     */
    protected String obtainPrefix(Object... arguments) {

        return config.getPrefix();
    }

    protected String obtainSuffix(Object... arguments) {

        return EMPTY_STRING;
    }

    @Override
    public String next(Object... arguments) {
        Assert.notBlank(config.getName()
                , "Parameter \"config.name\" must not blank. ");
        // Increment value.
        Long increment = incrementAndGet(arguments);
        Assert.notNull(increment
                , "Failed to invoke \"incrementAndGet\". ");
        // Add offset.
        Long offset = config.getOffset();
        if (offset != null && offset > ZERO) { increment += offset; }
        // Create identifier builder.
        StringBuilder identifier = new StringBuilder();
        identifier.append(increment);
        // Handle number length.
        Integer seqLength = config.getSequenceLength();
        boolean valid = seqLength != null && seqLength > ZERO;
        int count;
        if (valid && (count = seqLength - identifier.length()) > ZERO) {
            for (; count > ZERO; count--) {
                identifier.insert(ZERO, STR_ZERO);
            }
        }
        // Handle date string.
        String dateString = obtainDateString(arguments);
        if (StrUtil.isNotBlank(dateString)) {
            identifier.insert(ZERO, dateString);
        }
        // Handle prefix.
        String prefix = obtainPrefix(arguments);
        if (StrUtil.isNotBlank(prefix)) {
            identifier.insert(ZERO, prefix);
        }
        // Handle suffix.
        String suffix = obtainSuffix(arguments);
        if (StrUtil.isNotBlank(suffix)) {
            identifier.append(suffix);
        }
        // Return result.
        return identifier.toString();
    }

}
