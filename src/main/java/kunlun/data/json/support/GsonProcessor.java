/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kunlun.data.json.JsonFormat;
import kunlun.util.ArrayUtil;
import kunlun.util.Assert;

import java.lang.reflect.Type;

import static kunlun.data.json.JsonFormat.PRETTY_FORMAT;

/**
 * The json processor simple implement by gson.
 * @author Kahle
 */
public class GsonProcessor extends AbstractJsonProcessor {
    private final Gson prettyFormatGson;
    private final Gson gson;

    public GsonProcessor() {

        this(new Gson());
    }

    public GsonProcessor(Gson gson) {
        Assert.notNull(gson, "Parameter \"gson\" must not null. ");
        this.gson = gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        this.prettyFormatGson = gsonBuilder.create();
    }

    protected Gson getGson(Object... arguments) {
        if (ArrayUtil.isEmpty(arguments)) { return gson; }
        for (Object arg : arguments) {
            if (arg == null) { continue; }
            if (arg instanceof JsonFormat
                    && PRETTY_FORMAT.equals(arg)) {
                return prettyFormatGson;
            }
        }
        return gson;
    }

    @Override
    public String toJsonString(Object object, Object... arguments) {

        return getGson(arguments).toJson(object);
    }

    @Override
    public <T> T parseObject(String jsonString, Type type, Object... arguments) {

        return getGson(arguments).fromJson(jsonString, type);
    }

}
