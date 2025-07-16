/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support.jackson.util.scene;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JavaType;
import kunlun.data.json.support.jackson.util.JsonSceneManager.SceneDeserializer;
import kunlun.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * SingleToListDeserializer.
 * @author Zerox
 */
public class SingleToListDeserializer implements SceneDeserializer {

    @Override
    public Object deserialize(Object rawData, JavaType fieldType, ObjectCodec codec) {
        if (rawData == null) { return null; }
        if (rawData instanceof List) { return rawData; }
        if (rawData instanceof String && StrUtil.isBlank((String) rawData)) {
            return emptyList();
        }
        return new ArrayList<Object>(singletonList(rawData));
    }

}
