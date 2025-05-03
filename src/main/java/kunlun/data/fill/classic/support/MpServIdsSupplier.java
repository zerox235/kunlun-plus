package kunlun.data.fill.classic.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import kunlun.core.function.Function;
import kunlun.spring.util.SpringUtil;

import java.io.Serializable;
import java.util.*;

import static java.util.Collections.emptyList;
import static kunlun.util.Assert.notNull;

/**
 * The data supplier of the "listByIds" method in the mybatis plus service.
 * @author Kahle
 */
public class MpServIdsSupplier<T> implements Function<Collection<?>, Map<String, Map<String, Object>>> {

    public static <T> MpServIdsSupplier<T> of(Class<? extends IService<T>> clazz, Function<T, Object> keyMapper) {

        return new MpServIdsSupplier<T>(SpringUtil.getBean(clazz), keyMapper);
    }

    public static <T> MpServIdsSupplier<T> of(IService<T> iService, Function<T, Object> keyMapper) {

        return new MpServIdsSupplier<T>(iService, keyMapper);
    }

    private final Function<T, Object> keyMapper;
    private final IService<T> iService;

    public MpServIdsSupplier(IService<T> iService, Function<T, Object> keyMapper) {
        this.keyMapper = notNull(keyMapper);
        this.iService = notNull(iService);
    }

    @Override
    public Map<String, Map<String, Object>> apply(Collection<?> coll) {
        // Parameter conversion and deduplication.
        if (coll == null) { coll = emptyList(); }
        List<Serializable> list = new ArrayList<Serializable>();
        for (Object obj : coll) {
            if (obj == null) { continue; }
            if (obj instanceof Number || obj instanceof CharSequence) {
                if (!list.contains((Serializable) obj)) {
                    list.add((Serializable) obj);
                }
            } else {
                throw new IllegalArgumentException("Collection is illegal. ");
            }
        }
        /*List<Long> list = Optional.ofNullable(coll).map(Collection::stream)
                .orElseGet(Stream::empty).filter(Objects::nonNull)
                .map(item -> Long.valueOf(String.valueOf(item)))
                .distinct().collect(Collectors.toList());*/
        if (CollUtil.isEmpty(list)) { return Collections.emptyMap(); }
        // Data query.
        List<T> data = iService.listByIds(list);
        if (CollUtil.isEmpty(data)) { return Collections.emptyMap(); }
        // Convert to map.
        /*return data.stream()
                .filter(Objects::nonNull).collect(Collectors.toMap(
                        item -> String.valueOf(keyMapper.apply(item)),
                        BeanUtil::beanToMap,
                        (k1, k2) -> k2
                ));*/
        Map<String, Map<String, Object>> map = new LinkedHashMap<String, Map<String, Object>>();
        for (T datum : data) {
            if (datum == null) { continue; }
            map.put(String.valueOf(keyMapper.apply(datum)), BeanUtil.beanToMap(datum));
        }
        return map;
    }
}
