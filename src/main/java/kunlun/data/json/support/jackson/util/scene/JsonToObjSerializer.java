/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support.jackson.util.scene;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JavaType;
import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.jackson.util.JsonSceneManager.SceneSerializer;

import java.util.List;
import java.util.Map;

/**
 * JsonToObjSerializer.
 * @author Zerox
 */
public class JsonToObjSerializer implements SceneSerializer {
    private static final String NAME = "jackson";

    @Override
    public Object serialize(Object rawData, JavaType fieldType, ObjectCodec codec) {
        if (!(rawData instanceof String)) { return null; }
        String data = (String) rawData;
        if (JsonUtil.isJsonArray(NAME, data)) {
            return JsonUtil.parseObject(NAME, data, List.class);
        } else if (JsonUtil.isJsonObject(NAME, data)) {
            return JsonUtil.parseObject(NAME, data, Map.class);
        } else { return null; }
    }

}
