/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support.jackson.util;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JavaType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static kunlun.util.Assert.notBlank;
import static kunlun.util.Assert.notNull;

/**
 * SceneManager
 * @author Zerox
 */
public interface JsonSceneManager {

    void registerSerializer(String scene, SceneSerializer serializer);

    void deregisterSerializer(String scene);

    void registerDeserializer(String scene, SceneDeserializer deserializer);

    void deregisterDeserializer(String scene);

    SceneSerializer getSerializer(String scene);

    SceneDeserializer getDeserializer(String scene);


    /**
     * JsonSceneSerializer
     * @author Kahle
     */
    interface SceneSerializer {
        /**
         * serialize
         * @param rawData rawData
         * @param fieldType fieldType
         * @param codec codec
         * @return result
         */
        Object serialize(Object rawData, JavaType fieldType, ObjectCodec codec);
    }

    /**
     * JsonSceneDeserializer
     * @author Kahle
     */
    interface SceneDeserializer {
        /**
         * deserialize
         * @param rawData rawData
         * @param fieldType fieldType
         * @param codec codec
         * @return result
         */
        Object deserialize(Object rawData, JavaType fieldType, ObjectCodec codec);
    }

    /**
     * EmptySceneSerializer
     * @author Kahle
     */
    class EmptySceneSerializer implements SceneSerializer {
        @Override
        public Object serialize(Object rawData, JavaType fieldType, ObjectCodec codec) {

            return null;
        }
    }

    /**
     * EmptySceneDeserializer
     * @author Kahle
     */
    class EmptySceneDeserializer implements SceneDeserializer {
        @Override
        public Object deserialize(Object rawData, JavaType fieldType, ObjectCodec codec) {

            return null;
        }
    }

    /**
     * JsonSceneManagerImpl
     * @author Kahle
     */
    class JsonSceneManagerImpl implements JsonSceneManager {
        private final Map<String, SceneDeserializer> DESERIALIZERS = new ConcurrentHashMap<String, SceneDeserializer>();
        private final Map<String, SceneSerializer> SERIALIZERS = new ConcurrentHashMap<String, SceneSerializer>();

        @Override
        public void registerSerializer(String scene, SceneSerializer serializer) {

            SERIALIZERS.put(notBlank(scene), notNull(serializer));
        }

        @Override
        public void deregisterSerializer(String scene) {

            SERIALIZERS.remove(notBlank(scene));
        }

        @Override
        public void registerDeserializer(String scene, SceneDeserializer deserializer) {

            DESERIALIZERS.put(notBlank(scene), notNull(deserializer));
        }

        @Override
        public void deregisterDeserializer(String scene) {

            DESERIALIZERS.remove(notBlank(scene));
        }

        @Override
        public SceneSerializer getSerializer(String scene) {

            return SERIALIZERS.get(notBlank(scene));
        }

        @Override
        public SceneDeserializer getDeserializer(String scene) {

            return DESERIALIZERS.get(notBlank(scene));
        }
    }

}
