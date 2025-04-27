package kunlun.action.sms;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * The short messaging service message.
 * @see <a href="https://en.wikipedia.org/wiki/List_of_country_calling_codes">Country calling code</a>
 * @author Kahle
 */
public class SmsMessage implements Serializable {
    /**
     * Cell-phone number.
     */
    private Collection<String> phoneNumbers;
    /**
     * Sender name (SMS signature).
     */
    private Collection<String> senderNames;
    /**
     * SMS template content (if the support).
     */
    private Collection<String> contents;
    /**
     * SMS template code.
     */
    private String templateCode;
    /**
     * SMS template parameters.
     */
    private Collection<Map<String, Object>> parameters;
    /**
     * others others
     */
    private Map<String, Object> others;
    /**
     * The config id.
     */
    private String configId;

    public Collection<String> getPhoneNumbers() {

        return phoneNumbers;
    }

    public void setPhoneNumbers(Collection<String> phoneNumbers) {

        this.phoneNumbers = phoneNumbers;
    }

    public Collection<String> getSenderNames() {

        return senderNames;
    }

    public void setSenderNames(Collection<String> senderNames) {


        this.senderNames = senderNames;
    }

    public Collection<String> getContents() {

        return contents;
    }

    public void setContents(Collection<String> contents) {

        this.contents = contents;
    }

    public String getTemplateCode() {

        return templateCode;
    }

    public void setTemplateCode(String templateCode) {

        this.templateCode = templateCode;
    }

    public Collection<Map<String, Object>> getParameters() {

        return parameters;
    }

    public void setParameters(Collection<Map<String, Object>> parameters) {

        this.parameters = parameters;
    }

    public Map<String, Object> getOthers() {

        return others;
    }

    public void setOthers(Map<String, Object> others) {

        this.others = others;
    }

    public String getConfigId() {

        return configId;
    }

    public void setConfigId(String configId) {

        this.configId = configId;
    }
}
