/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support.jackson.util.scene;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JavaType;
import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.jackson.util.JsonSceneManager.SceneDeserializer;

import java.util.Map;

/**
 * ObjToJsonDeserializer.
 * @author Kahle
 */
public class ObjToJsonDeserializer implements SceneDeserializer {
    private static final String NAME = "jackson";

    @Override
    public Object deserialize(Object rawData, JavaType fieldType, Map<String, String> configs, ObjectCodec codec) {
        if (rawData == null) { return null; }
        if (fieldType.isTypeOrSubTypeOf(String.class) && !(rawData instanceof String)) {
            return JsonUtil.toJsonString(NAME, rawData);
        }
        return null;
    }

}
