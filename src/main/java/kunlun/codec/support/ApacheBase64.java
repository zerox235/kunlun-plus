/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.codec.support;

import kunlun.util.Assert;

public class ApacheBase64 extends Base64 {
    private final org.apache.commons.codec.binary.Base64
            urlSafeBase64 = new org.apache.commons.codec.binary.Base64(true);
    private final org.apache.commons.codec.binary.Base64
            defBase64 = new org.apache.commons.codec.binary.Base64();

    public ApacheBase64(Cfg cfg) {

        super(cfg);
    }

    public ApacheBase64() {

    }

    protected org.apache.commons.codec.binary.Base64 obtainBase64(Cfg cfg) {
        if (cfg.isUrlSafe()) {
            return urlSafeBase64;
        }
        else if (cfg.isMime()) {
            return new org.apache.commons.codec.binary.Base64(
                    cfg.getLineLength(), cfg.getLineSeparator().getBytes());
        }
        else { return defBase64; }
    }

    @Override
    public String encodeToString(Config config, byte[] source) {
        Assert.notNull(source, "Parameter \"source\" must not null. ");
        Cfg cfg = config != null ? (Cfg) config : getConfig();
        return obtainBase64(cfg).encodeToString(source);
    }

    @Override
    public byte[] decodeFromString(Config config, String source) {
        Assert.notNull(source, "Parameter \"source\" must not null. ");
        Cfg cfg = config != null ? (Cfg) config : getConfig();
        return obtainBase64(cfg).decode(source);
    }

}
