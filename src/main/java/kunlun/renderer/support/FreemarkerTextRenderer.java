/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.renderer.support;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import kunlun.exception.ExceptionUtils;
import kunlun.io.util.IoUtil;
import kunlun.util.Assert;
import kunlun.util.ObjUtils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import static kunlun.common.constant.Charsets.STR_UTF_8;
import static kunlun.common.constant.Numbers.*;
import static kunlun.common.constant.Symbols.DOT;
import static kunlun.common.constant.Symbols.SLASH;
import static kunlun.util.StrUtils.isNotBlank;

/**
 * The freemarker text renderer.
 * @author Kahle
 */
public class FreemarkerTextRenderer extends AbstractTextRenderer {
    private static final String FIXED_NAME = null;
    private final Configuration cfg;

    public FreemarkerTextRenderer(Configuration cfg) {

        this.cfg = Assert.notNull(cfg);
    }

    public FreemarkerTextRenderer() {
        try {
            TemplateLoader[] loaders = new TemplateLoader[TWO];
            loaders[ZERO] = new FileTemplateLoader(new File(DOT));
            loaders[ONE] = new ClassTemplateLoader(FreemarkerTextRenderer.class, SLASH);
            MultiTemplateLoader loader = new MultiTemplateLoader(loaders);
            this.cfg = new Configuration();
            this.cfg.setTemplateLoader(loader);
        } catch (IOException e) { throw ExceptionUtils.wrap(e); }
    }

    @Override
    public void render(Object template, Object data, Object output) {
        if (template == null) { return; }
        Writer writer = (Writer) Assert.isInstanceOf(Writer.class, output);
        Reader reader = null;
        try {
            if (template instanceof String) {
                Template tp = new Template(FIXED_NAME, (String) template, cfg);
                tp.process(data, writer);
            }
            else if (template instanceof Reader) {
                Template tp = new Template(FIXED_NAME, reader = (Reader) template, cfg);
                tp.process(data, writer);
            }
            else if (template instanceof Tpl) {
                Tpl tpl = (Tpl) template;
                if (ObjUtils.isEmpty(tpl.getContent()) && getTemplateLoader() != null) {
                    getTemplateLoader().accept(tpl);
                }
                if (!ObjUtils.isEmpty(tpl.getContent())) {
                    render(tpl.getContent(), data, output); return;
                }
                String charset = isNotBlank(tpl.getCharset()) ? tpl.getCharset() : STR_UTF_8;
                Template tp = cfg.getTemplate(tpl.getName(), charset);
                tp.process(data, writer);
            } else { throw new IllegalArgumentException("Unsupported template type! "); }
        } catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        } finally {
            IoUtil.closeQuietly(reader, writer);
        }
    }

}
