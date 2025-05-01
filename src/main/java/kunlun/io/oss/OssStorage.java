/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.io.oss;

import kunlun.io.DataStorage;

import java.util.Date;
import java.util.Map;

/**
 * The storage interface of the object storage service
 * @author Kahle
 */
public interface OssStorage extends DataStorage {

    /**
     * Get the native storage object.
     * @return The native storage object
     */
    Object getNative();

    /**
     * getDefaultBucketName.
     * @return getDefaultBucketName
     */
    String getDefaultBucketName();

    /**
     * getObjectUrlPrefixes.
     * @return getObjectUrlPrefixes
     */
    Map<String, String> getObjectUrlPrefixes();

    /**
     * Generate parameters for browser-based authorized access.
     * @param bucketName The bucket name
     * @param params The parameters required for signature
     * @return The temporary signature
     */
    Map<String, Object> createUploadSign(String bucketName, Map<String, Object> params);

    /**
     * createSignedUrl.
     * @param bucketName The bucket name
     * @param objectKey objectKey
     * @param expireTime expireTime
     * @param others others
     * @return SignedUrl
     */
    String createSignedUrl(String bucketName, String objectKey, Date expireTime, Map<String, Object> others);

    /**
     * createSignedUrl.
     * @param unsignedUrl unsignedUrl
     * @param expireTime expireTime
     * @param others others
     * @return SignedUrl
     */
    String createSignedUrl(String unsignedUrl, Date expireTime, Map<String, Object> others);

    /**
     * removeUrlSign.
     * @param objectUrl objectUrl
     * @return unsignedUrl
     */
    String removeUrlSign(String objectUrl);

    /**
     * refreshUrlSign
     * @param objectUrl objectUrl
     * @param expireTime expireTime
     * @param others others
     * @return SignedUrl
     */
    String refreshUrlSign(String objectUrl, Date expireTime, Map<String, Object> others);

    /**
     * Obtain the oss object based on the resource information (key).
     * @param key The resource information (key)
     * @return The oss object
     */
    @Override
    OssObject get(Object key);

    /**
     * Put the data.
     * @param data The data
     * @return The oss information or null
     */
    @Override
    OssInfo put(Object data);

}
