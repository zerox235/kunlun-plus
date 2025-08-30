/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.fill.classic.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import kunlun.common.constant.Nil;
import kunlun.core.function.Consumer;
import kunlun.core.function.Function;
import kunlun.data.fill.DataSupplier;
import kunlun.util.Assert;

import java.util.*;

import static java.util.Collections.emptyList;
import static kunlun.util.Assert.notNull;
import static kunlun.util.CastUtil.cast;

/**
 * 基础的基于 Service 的数据提供者.
 * @author Kahle
 */
public abstract class BaseServDataSupplier<S, T> implements DataSupplier {
    private final Consumer<Map<String, Object>> dataProcessor;
    private final Function<T, Object> keyMapper;
    private final S service;

    public BaseServDataSupplier(S service, Function<T, Object> keyMapper, Consumer<Map<String, Object>> processor) {
        if (processor == null) { processor = new Consumer.Empty<Map<String, Object>>(); }
        this.keyMapper = notNull(keyMapper);
        this.service = notNull(service);
        this.dataProcessor = processor;
    }

    public BaseServDataSupplier(S service, Function<T, Object> keyMapper) {

        this(service, keyMapper, Nil.<Consumer<Map<String, Object>>>g());
    }

    public Consumer<Map<String, Object>> getDataProcessor() {

        return dataProcessor;
    }

    public Function<T, Object> getKeyMapper() {

        return keyMapper;
    }

    public S getService() {

        return service;
    }

    protected <V> List<V> toList(Collection<?> coll, Class<V> clazz) {
        if (coll == null) { coll = emptyList(); }
        List<V> result = new ArrayList<V>();
        for (Object obj : coll) {
            if (obj == null) { continue; }
            Assert.isAssignable(clazz, obj.getClass(), "Collection is illegal. ");
            V val = cast(obj);
            if (!result.contains(val)) { result.add(val); }
        }
        return result;
    }

    protected Map<String, Map<String, Object>> toMap(List<T> data) {
        if (CollUtil.isEmpty(data)) { return Collections.emptyMap(); }
        // Convert to map.
        Map<String, Map<String, Object>> map = new LinkedHashMap<String, Map<String, Object>>();
        for (T datum : data) {
            if (datum == null) { continue; }
            Map<String, Object> beanToMap = BeanUtil.beanToMap(datum);
            getDataProcessor().accept(beanToMap);
            map.put(String.valueOf(getKeyMapper().apply(datum)), beanToMap);
        }
        return map;
    }

}
