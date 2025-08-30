/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support.jackson.util.scene;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JavaType;
import kunlun.data.json.support.jackson.util.JsonSceneManager.SceneDeserializer;

import java.util.Map;

/**
 * StringTrimDeserializer.
 * @author Kahle
 */
public class StrTrimDeserializer implements SceneDeserializer {

    @Override
    public Object deserialize(Object rawData, JavaType fieldType, Map<String, String> configs, ObjectCodec codec) {
        if (!fieldType.isTypeOrSubTypeOf(String.class)) { return null; }
        if (!(rawData instanceof String)) { return null; }
        return ((String) rawData).trim();
    }

}
