/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.validation;

import kunlun.data.validation.support.IsNumericValidator;
import kunlun.data.validation.support.RegexValidator;
import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import org.junit.Test;

public class ValidatorUtilTest {
    private static Logger log = LoggerFactory.getLogger(ValidatorUtilTest.class);

    @Test
    public void testNumeric() {
        ValidatorUtil.registerValidator("numeric", new IsNumericValidator());
        log.info("{}", ValidatorUtil.validate("numeric", "888.666"));
        log.info("{}", ValidatorUtil.validate("numeric", "-888.666"));
        log.info("{}", ValidatorUtil.validate("numeric", "+888.666"));
        log.info("{}", ValidatorUtil.validate("numeric", "888.666w"));
        log.info("{}", ValidatorUtil.validate("numeric", "hello, world! "));
    }

    @Test
    public void testEmail() {
        String emailRegex = "^[0-9A-Za-z][\\.-_0-9A-Za-z]*@[0-9A-Za-z]+(?:\\.[0-9A-Za-z]+)+$";
        ValidatorUtil.registerValidator("regex:email", new RegexValidator(emailRegex));
        log.info("{}", ValidatorUtil.validate("regex:email", "hello@email.com"));
        log.info("{}", ValidatorUtil.validate("regex:email", "hello@vip.email.com"));
        log.info("{}", ValidatorUtil.validate("regex:email", "$hello@email.com"));
        log.info("{}", ValidatorUtil.validate("regex:email", "hello@email"));
        log.info("{}", ValidatorUtil.validate("regex:email", "hello@.com"));
    }

    @Test
    public void testPhoneNumber() {
//        String phoneNumberRegex = "^1[3|4|5|6|7|8|9]\\d{9}$";
        String phoneNumberRegex = "^1\\d{10}$";
        ValidatorUtil.registerValidator("regex:phone_number", new RegexValidator(phoneNumberRegex));
        log.info("{}", ValidatorUtil.validate("regex:phone_number", "12000000000"));
        log.info("{}", ValidatorUtil.validate("regex:phone_number", "18000000000"));
        log.info("{}", ValidatorUtil.validate("regex:phone_number", "19999999999"));
    }

}
