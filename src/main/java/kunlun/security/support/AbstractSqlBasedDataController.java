/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.security.support;

import kunlun.util.Assert;

import java.util.Collection;
import java.util.Collections;

/**
 * The abstract sql-based data controller.
 * @author Kahle
 */
public abstract class AbstractSqlBasedDataController extends AbstractDataController {
    private Collection<String> defaultUserIdFields = Collections.singletonList("owner_id");
    private Collection<String> defaultOrgIdFields  = Collections.singletonList("own_org_id");

    public Collection<String> getDefaultUserIdFields() {

        return defaultUserIdFields;
    }

    public void setDefaultUserIdFields(Collection<String> defaultUserIdFields) {
        Assert.notEmpty(defaultUserIdFields, "Parameter \"defaultUserIdFields\" must not empty. ");
        this.defaultUserIdFields = defaultUserIdFields;
    }

    public Collection<String> getDefaultOrgIdFields() {

        return defaultOrgIdFields;
    }

    public void setDefaultOrgIdFields(Collection<String> defaultOrgIdFields) {
        Assert.notEmpty(defaultOrgIdFields, "Parameter \"defaultOrgIdFields\" must not empty. ");
        this.defaultOrgIdFields = defaultOrgIdFields;
    }

    /**
     * The data control configuration.
     * @author Kahle
     */
    public static class DataConfig {
        private String fromTable;
        private Collection<String> userIdFields;
        private Collection<String> orgIdFields;

        public String getFromTable() {

            return fromTable;
        }

        public void setFromTable(String fromTable) {

            this.fromTable = fromTable;
        }

        public Collection<String> getUserIdFields() {

            return userIdFields;
        }

        public void setUserIdFields(Collection<String> userIdFields) {

            this.userIdFields = userIdFields;
        }

        public Collection<String> getOrgIdFields() {

            return orgIdFields;
        }

        public void setOrgIdFields(Collection<String> orgIdFields) {

            this.orgIdFields = orgIdFields;
        }
    }

    /**
     * The simple rule.
     * @author Kahle
     */
    public static class SimpleRule extends BaseRule {
        private Collection<DataConfig> dataConfigs;

        public Collection<DataConfig> getDataConfigs() {

            return dataConfigs;
        }

        public void setDataConfigs(Collection<DataConfig> dataConfigs) {

            this.dataConfigs = dataConfigs;
        }
    }

}
