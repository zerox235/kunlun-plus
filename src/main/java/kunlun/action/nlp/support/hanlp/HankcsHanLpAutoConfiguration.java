/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.nlp.support.hanlp;

import com.hankcs.hanlp.HanLP;
import kunlun.action.ActionUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(HanLP.class)
public class HankcsHanLpAutoConfiguration {

    public HankcsHanLpAutoConfiguration() {
        HankcsSegmentAction handler = new HankcsSegmentAction();
        ActionUtil.registerAction("text-segment", handler);
    }

}
