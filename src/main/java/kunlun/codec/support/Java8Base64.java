/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.codec.support;

import kunlun.util.Assert;

public class Java8Base64 extends Base64 {

    public Java8Base64(Cfg cfg) {

        super(cfg);
    }

    public Java8Base64() {

    }

    @Override
    public String encodeToString(Config config, byte[] source) {
        Assert.notNull(source, "Parameter \"source\" must not null. ");
        Cfg cfg = config != null ? (Cfg) config : getConfig();
        java.util.Base64.Encoder encoder;
        if (cfg.isUrlSafe()) {
            encoder = java.util.Base64.getUrlEncoder();
        }
        else if (cfg.isMime()) {
            encoder = java.util.Base64.getMimeEncoder(
                    cfg.getLineLength(), cfg.getLineSeparator().getBytes());
        }
        else { encoder = java.util.Base64.getEncoder(); }
        return encoder.encodeToString(source);
    }

    @Override
    public byte[] decodeFromString(Config config, String source) {
        Assert.notNull(source, "Parameter \"source\" must not null. ");
        Cfg cfg = config != null ? (Cfg) config : getConfig();
        java.util.Base64.Decoder decoder;
        if (cfg.isUrlSafe()) {
            decoder = java.util.Base64.getUrlDecoder();
        }
        else if (cfg.isMime()) {
            decoder = java.util.Base64.getMimeDecoder();
        }
        else { decoder = java.util.Base64.getDecoder(); }
        return decoder.decode(source);
    }

}
