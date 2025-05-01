/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.io.oss.support;

import kunlun.common.constant.Charsets;
import kunlun.common.constant.Symbols;
import kunlun.data.tuple.KeyValue;
import kunlun.exception.ExceptionUtil;
import kunlun.io.oss.OssBase;
import kunlun.io.oss.OssInfo;
import kunlun.io.oss.OssObject;
import kunlun.io.oss.OssStorage;
import kunlun.io.storage.AbstractDataStorage;
import kunlun.util.Assert;
import kunlun.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * The abstract oss storage.
 * @author Kahle
 */
public abstract class AbstractOssStorage extends AbstractDataStorage implements OssStorage {
    private static final Logger log = LoggerFactory.getLogger(AbstractOssStorage.class);
    private final Map<String, String> objectUrlPrefixes;
    private final String defaultBucketName;

    public AbstractOssStorage(Map<String, String> objectUrlPrefixes, String defaultBucketName) {
        Assert.notNull(objectUrlPrefixes, "Parameter \"objectUrlPrefixes\" must not null. ");
        this.objectUrlPrefixes = objectUrlPrefixes;
        this.defaultBucketName = defaultBucketName;
    }

    protected OssBase getOssBase(Object key) {
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        if (key instanceof OssBase) {
            OssBase ossBase = (OssBase) key;
            Assert.notBlank(ossBase.getBucketName(), "Parameter \"bucketName\" must not blank. ");
            Assert.notNull(ossBase.getObjectKey(), "Parameter \"objectKey\" must not blank. ");
            return ossBase;
        }
        else if (key instanceof String) {
            OssBaseImpl ossBase = new OssBaseImpl();
            ossBase.setBucketName(getDefaultBucketName());
            ossBase.setObjectKey(String.valueOf(key));
            return ossBase;
        }
        else {
            throw new IllegalArgumentException("Parameter \"key\" is not supported. ");
        }
    }

    protected OssObject convertToOssObject(Object data) {
        Assert.notNull(data, "Parameter \"data\" must not null. ");
        OssObject ossObject;
        if (data instanceof OssObject) {
            ossObject = (OssObject) data;
        }
        else if (data instanceof KeyValue) {
            try {
                @SuppressWarnings("rawtypes")
                KeyValue keyValue = (KeyValue) data;
                Object value = keyValue.getValue();
                Object key = keyValue.getKey();
                OssBase ossBase = getOssBase(key);
                InputStream inputStream = convertToStream(value, Charsets.STR_UTF_8);
                ossObject = new OssObjectImpl(ossBase.getBucketName(), ossBase.getObjectKey());
                ((OssObjectImpl) ossObject).setObjectContent(inputStream);
            }
            catch (IOException e) {
                throw ExceptionUtil.wrap(e);
            }
        }
        else {
            throw new IllegalArgumentException("Parameter \"data\" is not supported. ");
        }
        return ossObject;
    }

    protected String mergePrefixAndKey(String urlPrefix, String objectKey) {
        if (!urlPrefix.endsWith(Symbols.SLASH) &&
                !objectKey.startsWith(Symbols.SLASH)) {
            return urlPrefix + Symbols.SLASH + objectKey;
        }
        else { return urlPrefix + objectKey; }
    }

    protected String buildObjectUrl(String bucketName, String objectKey) {
        Assert.notBlank(bucketName, "Parameter \"bucketName\" must not blank. ");
        Assert.notBlank(objectKey, "Parameter \"objectKey\" must not blank. ");
        String urlPrefix = objectUrlPrefixes.get(bucketName);
        // if prefix is blank, do not build object url.
        if (StrUtil.isBlank(urlPrefix)) { return null; }
        return mergePrefixAndKey(urlPrefix, objectKey);
    }

    protected OssInfo buildOssInfo(String bucketName, String objectKey, String objectUrl, Object original) {
        OssInfoImpl ossInfo = new OssInfoImpl(bucketName, objectKey, objectUrl);
        ossInfo.setOriginal(original);
        String buildObjectUrl = buildObjectUrl(bucketName, objectKey);
        ossInfo.setObjectUrl(StrUtil.isNotBlank(buildObjectUrl) ? buildObjectUrl : objectUrl);
        return ossInfo;
    }

    @Override
    public String getDefaultBucketName() {

        return Assert.notBlank(defaultBucketName, "Parameter \"defaultBucketName\" is not set. ");
    }

    @Override
    public Map<String, String> getObjectUrlPrefixes() {

        return Collections.unmodifiableMap(objectUrlPrefixes);
    }

    @Override
    public Map<String, Object> createUploadSign(String bucketName, Map<String, Object> params) {

        throw new UnsupportedOperationException();
    }

    @Override
    public String createSignedUrl(String bucketName, String objectKey, Date expireTime, Map<String, Object> others) {

        throw new UnsupportedOperationException();
    }

    @Override
    public String createSignedUrl(String unsignedUrl, Date expireTime, Map<String, Object> others) {

        return unsignedUrl;
    }

    @Override
    public String removeUrlSign(String objectUrl) {
        if (StrUtil.isBlank(objectUrl)) { return objectUrl; }
        try {
            URL url = new URL(objectUrl);
            String urlPrefix = url.getProtocol() + "://" + url.getAuthority();
            String objectKey = url.getPath();
            //String urlQuery = url.getQuery()
            return urlPrefix + objectKey;
        } catch (Exception e) { throw ExceptionUtil.wrap(e); }
    }

    @Override
    public String refreshUrlSign(String objectUrl, Date expireTime, Map<String, Object> others) {

        return createSignedUrl(removeUrlSign(objectUrl), expireTime, others);
    }

}
