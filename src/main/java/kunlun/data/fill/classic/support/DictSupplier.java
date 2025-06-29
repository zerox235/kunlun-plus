package kunlun.data.fill.classic.support;

import kunlun.common.constant.Nil;
import kunlun.core.function.Function;
import kunlun.data.bean.BeanUtil;
import kunlun.data.dict.DataDict;
import kunlun.data.dict.DictUtil;
import kunlun.util.MapUtil;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static kunlun.util.Assert.notBlank;

/**
 * The data supplier based on data Dict.
 * @author Kahle
 */
public class DictSupplier implements Function<Collection<?>, Map<String, Map<String, Object>>> {

    public static DictSupplier of(String namespace, String groupCode, Function<DataDict, Object> keyMapper) {

        return new DictSupplier(namespace, groupCode, keyMapper);
    }

    public static DictSupplier of(String groupCode, Function<DataDict, Object> keyMapper) {

        return new DictSupplier(groupCode, keyMapper);
    }

    protected final Function<DataDict, Object> keyMapper;
    protected final String namespace;
    protected final String groupCode;

    public DictSupplier(String namespace, String groupCode, Function<DataDict, Object> keyMapper) {
        this.groupCode = notBlank(groupCode);
        this.namespace = namespace;
        this.keyMapper = keyMapper;
    }

    public DictSupplier(String groupCode, Function<DataDict, Object> keyMapper) {

        this(Nil.STR, groupCode, keyMapper);
    }

    @Override
    public Map<String, Map<String, Object>> apply(Collection<?> coll) {
        Map<String, Map<String, Object>> dictMap = new LinkedHashMap<String, Map<String, Object>>();
        List<DataDict> list = DictUtil.listByGroup(namespace, groupCode);
        for (DataDict dataDict : list) {
            if (dataDict == null) { continue; }
            Map<String, Object> map;
            if (MapUtil.isNotEmpty(dataDict.getProperties())) {
                map = new LinkedHashMap<String, Object>(dataDict.getProperties());
            } else { map = new LinkedHashMap<String, Object>(); }
            map.putAll(BeanUtil.beanToMap(dataDict));
            dictMap.put(String.valueOf(keyMapper.apply(dataDict)), map);
        }
        return dictMap;
    }
}
