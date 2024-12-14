/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.nlp.support.similarity;

import kunlun.action.ActionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.xm.Similarity;

@Configuration
@ConditionalOnClass(Similarity.class)
public class XmTextSimilarityAutoConfiguration {

    public XmTextSimilarityAutoConfiguration() {
        XmTextSimilarityAction handler = new XmTextSimilarityAction();
        ActionUtils.registerAction("text-similarity", handler);
    }

}
