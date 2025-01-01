/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.security.support.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Map;

import static kunlun.security.support.AbstractSecurityContext.*;

/**
 * The log layout with fixed prefixes (such as user, tenant, platform, etc.).
 * @see ch.qos.logback.classic.PatternLayout
 * @see org.slf4j.TtlLogbackMdcAdapter
 * @author Kahle
 */
public class SecurityMdcPrefixLayout extends PatternLayout {

    protected String createPrefix(Map<String, String> mdc) {
        String result = "";
        if (MapUtil.isEmpty(mdc)) { return result; }
        String appKey = "APP", envKey = "ENV", cliKey = "CLI", servKey = "SERV";
        //
        if (mdc.containsKey(servKey)) {
            result = result + String.format("CLI:%s SERV:%s ",
                    mdc.get(cliKey),
                    mdc.get(servKey)
            );
        }
        //
        result += "TID:" + mdc.get(TRACE_ID_NAME) + " ";
        result += "P:" + mdc.get(PLATFORM_NAME) + " ";
        if (StrUtil.isNotBlank(mdc.get(TENANT_ID_NAME))) {
            result += "T:" + mdc.get(TENANT_ID_NAME) + " ";
        }
        result += "UID:" + mdc.get(USER_ID_NAME) + " ";
        if (StrUtil.isNotBlank(mdc.get(USER_TYPE_NAME))) {
            result += "UTP:" + mdc.get(USER_TYPE_NAME) + " ";
        }

        return result;
    }

    @Override
    public String doLayout(ILoggingEvent event) {

        return createPrefix(event.getMDCPropertyMap()) + super.doLayout(event);
    }

}
