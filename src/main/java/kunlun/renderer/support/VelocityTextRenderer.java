/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.renderer.support;

import kunlun.data.Dict;
import kunlun.util.Assert;
import kunlun.util.CloseUtils;
import kunlun.util.ObjUtils;
import kunlun.util.StrUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeSingleton;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import static kunlun.common.constant.Charsets.STR_UTF_8;
import static org.apache.velocity.app.Velocity.FILE_RESOURCE_LOADER_CACHE;
import static org.apache.velocity.app.Velocity.RESOURCE_LOADER;

/**
 * The velocity text renderer.
 * @author Kahle
 */
public class VelocityTextRenderer extends AbstractTextRenderer {
    private static final VelocityEngine DEFAULT_ENGINE = new VelocityEngine();
    private static final String FIXED_LOG_TAG = "fixed-log-tag";

    static {
        Properties properties = new Properties();
        properties.setProperty(RESOURCE_LOADER, "file, class, jar");
        properties.setProperty(FILE_RESOURCE_LOADER_CACHE, "true");
        properties.setProperty("input.encoding", "UTF-8");
        properties.setProperty("output.encoding", "UTF-8");
        properties.setProperty("file.resource.loader.modificationCheckInterval", "86400");
        properties.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        properties.setProperty("jar.resource.loader.class", "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
        DEFAULT_ENGINE.init(properties);
    }

    private VelocityEngine engine;
    private boolean init;

    public VelocityTextRenderer(VelocityEngine engine) {

        this.engine = Assert.notNull(engine);
    }

    public VelocityTextRenderer() {

        this.init = RuntimeSingleton.getRuntimeServices().isInitialized();
    }

    protected Context convert(Object data) {
        if (data == null) { return new VelocityContext(); }
        if (data instanceof Context) { return (Context) data; }
        if (data instanceof Map) {
            Context context = new VelocityContext();
            @SuppressWarnings("rawtypes")
            Dict model = Dict.of((Map) data);
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                context.put(entry.getKey(), entry.getValue());
            }
            return context;
        }
        throw new VelocityException("Parameter \"data\" cannot handle. ");
    }

    @Override
    public void render(Object template, Object data, Object output) {
        if (template == null) { return; }
        Writer writer = (Writer) Assert.isInstanceOf(Writer.class, output);
        Context context = convert(data);
        Reader reader = null;
        try {
            if (template instanceof String) {
                String templateStr = (String) template;
                if (init) {
                    Velocity.evaluate(context, writer, FIXED_LOG_TAG, templateStr);
                } else if (engine != null) {
                    engine.evaluate(context, writer, FIXED_LOG_TAG, templateStr);
                } else {
                    DEFAULT_ENGINE.evaluate(context, writer, FIXED_LOG_TAG, templateStr);
                }
            }
            else if (template instanceof Reader) {
                reader = (Reader) template;
                if (init) {
                    Velocity.evaluate(context, writer, FIXED_LOG_TAG, reader);
                } else if (engine != null) {
                    engine.evaluate(context, writer, FIXED_LOG_TAG, reader);
                } else {
                    DEFAULT_ENGINE.evaluate(context, writer, FIXED_LOG_TAG, reader);
                }
            }
            else if (template instanceof Tpl) {
                Tpl tpl = (Tpl) template;
                if (ObjUtils.isEmpty(tpl.getContent()) && getTemplateLoader() != null) {
                    getTemplateLoader().accept(tpl);
                }
                if (!ObjUtils.isEmpty(tpl.getContent())) {
                    render(tpl.getContent(), data, output); return;
                }
                String charset = tpl.getCharset();
                String tplName = tpl.getName();
                if (StrUtils.isBlank(charset)) { charset = STR_UTF_8; }
                if (init) {
                    Velocity.mergeTemplate(tplName, charset, context, writer);
                } else if (engine != null) {
                    engine.mergeTemplate(tplName, charset, context, writer);
                } else {
                    DEFAULT_ENGINE.mergeTemplate(tplName, charset, context, writer);
                }
            } else { throw new IllegalArgumentException("Unsupported template type! "); }
        } finally {
            CloseUtils.closeQuietly(reader);
            CloseUtils.closeQuietly(writer);
        }
    }

}
