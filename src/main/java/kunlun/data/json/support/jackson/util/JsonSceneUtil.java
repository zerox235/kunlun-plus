/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support.jackson.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import kunlun.common.constant.Nil;
import kunlun.common.constant.Words;
import kunlun.core.function.Supplier;
import kunlun.data.json.support.jackson.model.Scene;
import kunlun.data.json.support.jackson.util.JsonSceneManager.EmptySceneDeserializer;
import kunlun.data.json.support.jackson.util.JsonSceneManager.EmptySceneSerializer;
import kunlun.data.json.support.jackson.util.JsonSceneManager.JsonSceneManagerImpl;
import kunlun.data.json.support.jackson.util.scene.*;
import kunlun.security.SecurityUtil;
import kunlun.security.support.AbstractSecurityContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.math.RoundingMode.HALF_UP;
import static kunlun.data.json.support.jackson.model.Scene.*;
import static kunlun.util.Assert.notBlank;
import static kunlun.util.Assert.notNull;

/**
 * JsonSceneUtil
 * @author Kahle
 */
public class JsonSceneUtil {
    private static final Map<String, JsonSceneManager> MANAGERS = new ConcurrentHashMap<String, JsonSceneManager>();
    private static final String CURRENT_CONFIG_KEY = "_jsonSceneCurrentConfig";
    private static Supplier<Map<String, Object>> contextStorageSupplier;
    private static String defaultConfig = Words.DEFAULT;

    static {
        setContextStorageSupplier(new Supplier<Map<String, Object>>() {
            @Override
            public Map<String, Object> get() {
                // todo 临时方案
                return ((AbstractSecurityContext) SecurityUtil.getContext()).getBucket();
            }
        });
        register(getDefaultConfig(), createManager());
    }

    public static Supplier<Map<String, Object>> getContextStorageSupplier() {

        return contextStorageSupplier;
    }

    public static void setContextStorageSupplier(Supplier<Map<String, Object>> contextStorageSupplier) {

        JsonSceneUtil.contextStorageSupplier = notNull(contextStorageSupplier);
    }

    public static Map<String, Object> getContextStorage() {
        Supplier<Map<String, Object>> supplier = getContextStorageSupplier();
        Map<String, Object> storage;
        if (supplier == null || (storage = supplier.get()) == null) {
            return null;
        }
        return storage;
    }

    public static String getDefaultConfig() {

        return defaultConfig;
    }

    public static void setDefaultConfig(String defaultConfig) {

        JsonSceneUtil.defaultConfig = notBlank(defaultConfig);
    }

    public static String getCurrentConfig() {
        // todo 临时方案
        Map<String, Object> storage = getContextStorage();
        if (storage == null) { return null; }
        return Convert.toStr(storage.get(CURRENT_CONFIG_KEY));
    }

    public static void setCurrentConfig(String currentConfig) {
        Map<String, Object> storage = getContextStorage();
        if (storage == null) { return; }
        storage.put(CURRENT_CONFIG_KEY, currentConfig);
    }

    public static JsonSceneManager createManager() {
        JsonSceneManager manager = new JsonSceneManagerImpl();
        initManager(manager);
        return manager;
    }

    public static void initManager(JsonSceneManager manager) {
        notNull(manager);
        //
        EmptySceneDeserializer emptySceneDeserializer = new EmptySceneDeserializer();
        EmptySceneSerializer emptySceneSerializer = new EmptySceneSerializer();
        for (Scene value : Scene.values()) {
            manager.registerDeserializer(value.name(), emptySceneDeserializer);
            manager.registerSerializer(value.name(), emptySceneSerializer);
        }
        //
        manager.registerSerializer(ZERO_DECIMAL.name(), new BigDecimalSerializer("0.0", HALF_UP));
        manager.registerSerializer(ONE_DECIMAL.name(), new BigDecimalSerializer("0.0", HALF_UP));
        manager.registerSerializer(TWO_DECIMAL.name(), new BigDecimalSerializer("0.00", HALF_UP));
        manager.registerSerializer(THREE_DECIMAL.name(), new BigDecimalSerializer("0.000", HALF_UP));
        manager.registerSerializer(FOUR_DECIMAL.name(), new BigDecimalSerializer("0.0000", HALF_UP));
        manager.registerSerializer(FIVE_DECIMAL.name(), new BigDecimalSerializer("0.00000", HALF_UP));
        manager.registerSerializer(SIX_DECIMAL.name(), new BigDecimalSerializer("0.000000", HALF_UP));
        //
        manager.registerSerializer(JSON_STR.name(), new JsonToObjSerializer());
        //
        manager.registerDeserializer(STR_TRIM.name(), new StrTrimDeserializer());
        manager.registerDeserializer(JSON_STR.name(), new ObjToJsonDeserializer());
        manager.registerDeserializer(SINGLE_TO_LIST.name(), new SingleToListDeserializer());
    }

    public static void register(String config, JsonSceneManager manager) {

        MANAGERS.put(notBlank(config), notNull(manager));
    }

    public static void deregister(String config) {

        MANAGERS.remove(notBlank(config));
    }

    public static JsonSceneManager getManager(String config) {
        if (StrUtil.isBlank(config)) { config = getCurrentConfig(); }
        if (StrUtil.isBlank(config)) { config = getDefaultConfig(); }
        return MANAGERS.get(config);
    }

    public static JsonSceneManager getManager() {

        return getManager(Nil.STR);
    }

}
