/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.generator.id.support;

import kunlun.time.DateUtil;
import kunlun.util.StrUtil;

import static kunlun.common.constant.Numbers.*;
import static kunlun.util.Assert.*;

/**
 * 抽象的增量 ID 生成器.<br />
 * @author Zerox
 */
public abstract class AbstractIncrementalIdGenerator extends StringIdGenerator {
    protected static final String SEQ_NUM_KEY = "seq-num";
    private final IncrementalIdConfig config;

    public AbstractIncrementalIdGenerator(IncrementalIdConfig config) {
        notNull(config, "Parameter \"config\" must not null. ");
        notBlank(config.getName(), "Parameter \"config.name\" must not blank. ");
        this.config = config;
        if (config.getStepLength() == null) { config.setStepLength(ONE); }
    }

    public IncrementalIdConfig getConfig() {

        return config;
    }

    @Override
    protected String obtainTimeString(Context context) {
        String datePattern = config.getDatePattern();
        if (StrUtil.isNotBlank(datePattern)) {
            return DateUtil.format(datePattern);
        }
        return null;
    }

    @Override
    protected String obtainPrefix(Context context) {

        return config.getPrefix();
    }

    @Override
    protected String generateId(Context context) {
        Long seqNum = (Long) context.getStorage().get(SEQ_NUM_KEY);
        notNull(seqNum, "Parameter \"seqNum\" must not null. ");
        // Add offset.
        Long offset = config.getOffset();
        if (offset != null && offset > ZERO) { seqNum += offset; }
        // Create identifier builder.
        StringBuilder identifier = new StringBuilder();
        identifier.append(seqNum);
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
        String dateString = obtainTimeString(context);
        if (StrUtil.isNotBlank(dateString)) {
            identifier.insert(ZERO, dateString);
        }
        // Handle prefix.
        String prefix = obtainPrefix(context);
        if (StrUtil.isNotBlank(prefix)) {
            identifier.insert(ZERO, prefix);
        }
        // Handle suffix.
        String suffix = obtainSuffix(context);
        if (StrUtil.isNotBlank(suffix)) {
            identifier.append(suffix);
        }
        // Return result.
        return identifier.toString();
    }

    @Override
    public String preview(Object... arguments) {
        // 参数校验
        notBlank(config.getName(), "Parameter \"config.name\" must not blank. ");
        // 构建上下文对象
        ContextImpl context = new ContextImpl(config, arguments);
        // 获取当前的序号
        Long seqNum = onlyGet(context);
        notNull(seqNum, "The \"onlyGet\" method invoke failed. ");
        context.getStorage().put(SEQ_NUM_KEY, seqNum);
        // 生成 ID
        return generateId(context);
    }

    @Override
    public String next(Object... arguments) {
        // 参数校验
        notBlank(config.getName(), "Parameter \"config.name\" must not blank. ");
        // 构建上下文对象
        ContextImpl context = new ContextImpl(config, arguments);
        // 自增并获取
        Long seqNum = incrementAndGet(context);
        notNull(seqNum, "The \"incrementAndGet\" method invoke failed. ");
        context.getStorage().put(SEQ_NUM_KEY, seqNum);
        // 生成 ID
        return generateId(context);
    }

    /**
     * 获取查询 Key.
     * @param context 上下文对象
     * @return 获取到的查询 Key
     */
    protected abstract String buildQueryKey(Context context);

    /**
     * 仅获取当前序号.
     * @param context 上下文对象
     * @return 当前序号
     */
    protected Long onlyGet(Context context) {
        throw new UnsupportedOperationException(renderMessage(
                "In \"%s\", the method \"onlyGet\" is not supported! ", getClass().getName()
        ));
    }

    /**
     * Increment and get the stored value.
     * @param context The arguments at generation time
     * @return The value that is incremented and taken out
     */
    protected abstract Long incrementAndGet(Context context);

}
