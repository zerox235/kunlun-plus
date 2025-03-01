/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.desensitize.support;

import kunlun.data.desensitize.DesensitizeUtil;
import kunlun.exception.ExceptionUtil;
import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import org.junit.Test;

public class DesensitizeUtilTest {
    private static final Logger log = LoggerFactory.getLogger(DesensitizeUtilTest.class);

    static {
        try {
            new DesensitizeAutoConfiguration().afterPropertiesSet();
        }
        catch (Exception e) {
            throw ExceptionUtil.wrap(e);
        }
    }

    @Test
    public void testPhoneNumber() {
        log.info("{}", DesensitizeUtil.desensitize("PhoneNumber", "13600006666"));
        log.info("{}", DesensitizeUtil.desensitize("PhoneNumber", "13888889999"));
    }

    @Test
    public void testWithPhoneNumber() {
        DesensitizeUtil.register("WithPhoneNumber", new WithPhoneNumberDesensitizer());
        log.info("{}", DesensitizeUtil.desensitize("WithPhoneNumber", "Hello13600006666Hel13888889999"));
        log.info("{}", DesensitizeUtil.desensitize("WithPhoneNumber", "1360000666613888889999"));
        log.info("{}", DesensitizeUtil.desensitize("WithPhoneNumber", "Hello1354385689476556"));
    }

}
