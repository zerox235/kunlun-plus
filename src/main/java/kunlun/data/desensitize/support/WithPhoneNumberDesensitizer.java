/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.desensitize.support;

import kunlun.data.desensitize.Desensitizer;
import kunlun.util.StrUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WithPhoneNumberDesensitizer implements Desensitizer {
    private final Desensitizer phoneNumberMasker;
    private final Pattern pattern;

    public WithPhoneNumberDesensitizer() {

        this(null, null);
    }

    public WithPhoneNumberDesensitizer(String regex, String cover) {
        this.phoneNumberMasker = new PhoneNumberDesensitizer(cover);
        if (StrUtil.isBlank(regex)) {
            regex = "1[3|4|5|6|7|8|9]\\d{9}";
        }
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public String desensitize(CharSequence data) {
        if (StrUtil.isBlank(data)) {
            return String.valueOf(data);
        }
        String dataTmp = String.valueOf(data);
        Matcher matcher = pattern.matcher(dataTmp);
        while (matcher.find()) {
            String phoneNumber = matcher.group();
            String mask = phoneNumberMasker.desensitize(phoneNumber);
            dataTmp = StrUtil.replace(dataTmp, phoneNumber, mask);
        }
        return dataTmp;
    }

}
