/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support.jackson.model;

/**
 * Scene
 * @author Zerox
 */
public enum Scene {

    // region ======== default ========
    /**
     * default
     */
    DEFAULT,
    // endregion


    // region ======== fixed decimal ========
    /**
     * Zero Decimal
     */
    ZERO_DECIMAL,
    /**
     * One Decimal
     */
    ONE_DECIMAL,
    /**
     * Two Decimal
     */
    TWO_DECIMAL,
    /**
     * Three Decimal
     */
    THREE_DECIMAL,
    /**
     * Four Decimal
     */
    FOUR_DECIMAL,
    /**
     * Five Decimal
     */
    FIVE_DECIMAL,
    /**
     * Six Decimal
     */
    SIX_DECIMAL,
    // endregion


    // region ======== exchange rate ========
    /**
     * ExchangeRate
     */
    EX_RATE,
    /**
     * ExchangeRate1
     */
    EX_RATE1,
    /**
     * ExchangeRate2
     */
    EX_RATE2,
    // endregion


    // region ======== money ========
    /**
     * money default
     */
    MONEY,
    /**
     * money default 1
     */
    MONEY1,
    /**
     * money default 2
     */
    MONEY2,
    /**
     * money cny
     */
    MON_CNY,
    /**
     * money cny 1
     */
    MON_CNY1,
    /**
     * money cny 2
     */
    MON_CNY2,
    /**
     * money usd
     */
    MON_USD,
    /**
     * money usd 1
     */
    MON_USD1,
    /**
     * money usd 2
     */
    MON_USD2,
    // endregion


    // region ======== weight ========
    /**
     * weight default
     */
    WEIGHT,
    /**
     * weight default 1
     */
    WEIGHT1,
    /**
     * weight default 2
     */
    WEIGHT2,
    /**
     * weight kilogram
     */
    WEI_KG,
    /**
     * weight kilogram 1
     */
    WEI_KG1,
    /**
     * weight kilogram 2
     */
    WEI_KG2,
    /**
     * weight gram
     */
    WEI_G,
    /**
     * weight gram 1
     */
    WEI_G1,
    /**
     * weight gram 2
     */
    WEI_G2,
    // endregion


    // region ======== length ========
    /**
     * length default
     */
    LENGTH,
    /**
     * length default 1
     */
    LENGTH1,
    /**
     * length default 2
     */
    LENGTH2,

    /**
     * length meters
     */
    LEN_M,
    /**
     * length meters 1
     */
    LEN_M1,
    /**
     * length meters 2
     */
    LEN_M2,
    // endregion


    // region ======== area ========
    /**
     * area default
     */
    AREA,
    /**
     * area default 1
     */
    AREA1,
    /**
     * area default 2
     */
    AREA2,

    /**
     * area square meters
     */
    ARE_SQ_M,
    /**
     * area square meters 1
     */
    ARE_SQ_M1,
    /**
     * area square meters 2
     */
    ARE_SQ_M2,
    // endregion


    // region ======== volume ========
    /**
     * volume default
     */
    VOLUME,
    /**
     * volume default 1
     */
    VOLUME1,
    /**
     * volume default 2
     */
    VOLUME2,

    /**
     * volume cubic meters
     */
    VOL_CU_M,
    /**
     * volume cubic meters 1
     */
    VOL_CU_M1,
    /**
     * volume cubic meters 2
     */
    VOL_CU_M2,
    // endregion


    // region ======== time ========
    /**
     * time default
     */
    TIME,
    /**
     * time default 1
     */
    TIME1,
    /**
     * time default 2
     */
    TIME2,
    // endregion


    // region ======== string ========
    /**
     * String trim
     */
    STR_TRIM,
    // endregion


    // region ======== json ========
    /**
     * json object
     */
    JSON_STR,
    /**
     * 将文件对象以 Json 字符串的形式保存
     */
    FILES_JSON_STR,
    /**
     * 将文件对象以 Json 字符串的形式保存
     */
    FILES_JSON_STR1,
    // endregion


    /**
     * single to list
     */
    SINGLE_TO_LIST,

}
