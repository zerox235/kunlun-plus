/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.fill.classic.support;

import cn.hutool.core.collection.CollUtil;
import kunlun.core.function.BiFunction;
import kunlun.core.function.Consumer;
import kunlun.core.function.Function;
import kunlun.spring.util.SpringUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 基于 Service 的自定义方法的数据提供者.
 * @author Kahle
 */
public class ServiceCustomSupplier<S, R> extends BaseServDataSupplier<S, R> {

    public static <S, R> ServiceCustomSupplier<S, R> of(Class<? extends S> clazz, Function<R, Object> keyMapper,
                                                        BiFunction<S, Collection<Serializable>, List<R>> querySupplier,
                                                        Consumer<Map<String, Object>> dataProcessor) {

        return new ServiceCustomSupplier<S, R>(SpringUtil.getBean(clazz), keyMapper, querySupplier, dataProcessor);
    }

    public static <S, R> ServiceCustomSupplier<S, R> of(Class<? extends S> clazz, Function<R, Object> keyMapper,
                                                        BiFunction<S, Collection<Serializable>, List<R>> querySupplier) {

        return new ServiceCustomSupplier<S, R>(SpringUtil.getBean(clazz), keyMapper, querySupplier);
    }

    public static <S, R> ServiceCustomSupplier<S, R> of(S service, Function<R, Object> keyMapper,
                                                        BiFunction<S, Collection<Serializable>, List<R>> querySupplier,
                                                        Consumer<Map<String, Object>> dataProcessor) {

        return new ServiceCustomSupplier<S, R>(service, keyMapper, querySupplier, dataProcessor);
    }

    public static <S, R> ServiceCustomSupplier<S, R> of(S service, Function<R, Object> keyMapper,
                                                        BiFunction<S, Collection<Serializable>, List<R>> querySupplier) {

        return new ServiceCustomSupplier<S, R>(service, keyMapper, querySupplier);
    }

    private final BiFunction<S, Collection<Serializable>, List<R>> querySupplier;

    public ServiceCustomSupplier(S service, Function<R, Object> keyMapper,
                                 BiFunction<S, Collection<Serializable>, List<R>> querySupplier,
                                 Consumer<Map<String, Object>> processor) {
        super(service, keyMapper, processor);
        this.querySupplier = querySupplier;
    }

    public ServiceCustomSupplier(S service, Function<R, Object> keyMapper,
                                 BiFunction<S, Collection<Serializable>, List<R>> querySupplier) {
        super(service, keyMapper);
        this.querySupplier = querySupplier;
    }

    @Override
    public Map<String, Map<String, Object>> acquire(Collection<?> coll) {
        // Parameter conversion and deduplication.
        List<Serializable> list = toList(coll, Serializable.class);
        if (CollUtil.isEmpty(list)) { return Collections.emptyMap(); }
        // Data query.
        List<R> data = querySupplier.apply(getService(), list);
        if (CollUtil.isEmpty(data)) { return Collections.emptyMap(); }
        // Convert to map.
        return toMap(data);
    }
}
