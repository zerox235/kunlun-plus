/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import kunlun.common.constant.Nil;
import kunlun.core.annotation.Kv;
import kunlun.data.json.support.jackson.annotation.JsonSceneSerialize;
import kunlun.data.json.support.jackson.model.Scene;
import kunlun.data.json.support.jackson.util.JsonSceneManager;
import kunlun.data.json.support.jackson.util.JsonSceneManager.SceneSerializer;
import kunlun.data.json.support.jackson.util.JsonSceneUtil;
import kunlun.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JsonSceneSerializer
 * @author Kahle
 */
public class JsonSceneSerializer extends JsonSerializer<Object> implements ContextualSerializer {
    private static final Logger log = LoggerFactory.getLogger(JsonSceneSerializer.class);
    private Map<String, String> configs;
    private JavaType fieldType;
    private String scene;

    public JsonSceneSerializer(String scene, JavaType fieldType, Map<String, String> configs) {
        this.fieldType = fieldType;
        this.configs = configs;
        this.scene = scene;
    }

    public JsonSceneSerializer() {

    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) {
        JsonSceneSerialize jsonScene; Scene sceneEnum; String custom = null, scene;
        JavaType fieldType = property.getType();
        if ((jsonScene = property.getAnnotation(JsonSceneSerialize.class)) == null) {
            return new JsonSceneSerializer(Nil.STR, fieldType, Collections.<String, String>emptyMap());
        }
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (Kv kv : jsonScene.configs()) { map.put(kv.k(), kv.v()); }
        if (Scene.DEFAULT.equals(sceneEnum = jsonScene.value()) &&
                StrUtil.isBlank(custom = jsonScene.custom())) {
            return new JsonSceneSerializer(Nil.STR, fieldType, map);
        }
        if (!Scene.DEFAULT.equals(sceneEnum)) {
            scene = sceneEnum.name();
        } else { scene = custom; }
        return new JsonSceneSerializer(scene, fieldType, map);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        //
        JsonSceneManager manager; SceneSerializer serializer;
        if (StrUtil.isBlank(scene) || (manager = JsonSceneUtil.getManager()) == null ||
                (serializer = manager.getSerializer(scene)) == null) {
            gen.writeObject(value); return;
        }
        //
        Object result = serializer.serialize(value, fieldType, configs, gen.getCodec());
        if (result == null) { gen.writeObject(value); return; }
        gen.writeObject(result);
    }

}
