/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.renderer.support;

import kunlun.data.Dict;
import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import kunlun.renderer.RenderUtil;
import org.junit.Test;

import static kunlun.core.Renderer.Tpl;

public class FreemarkerTextRendererTest {
    private static final Logger log = LoggerFactory.getLogger(FreemarkerTextRendererTest.class);
    private static final String rendererName = "freemarker";
    private static final Dict data = Dict.of();
    private String source = "This is test string \"${testStr}\", \nTest string is \"${testStr}\". ";
    private String source1 = "You name is \"${data.name}\", \nAnd you age is \"${data.age}\". ";

    static {
        RenderUtil.registerRenderer(rendererName, new FreemarkerTextRenderer());
        data.set("testStr", "hello, world! ").set("nullVal", null);
        data.set("data", Dict.of("name", "zhangsan").set("age", "19"));
    }

    @Test
    public void test1() {
        log.info(RenderUtil.renderToString(rendererName, source, data));
        log.info(RenderUtil.renderToString(rendererName, source1, data));
        log.info(RenderUtil.renderToString(rendererName, Tpl.of("testFreemarker.ftl"), data));
    }

}
