/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.util;

import org.junit.Test;

public class PhoneNumberUtilTest {

    @Test
    public void test1() {
        String phoneCarrierZh = PhoneNumberUtil.phoneCarrierZh("18658258192");
        System.out.println(phoneCarrierZh);
    }

}
