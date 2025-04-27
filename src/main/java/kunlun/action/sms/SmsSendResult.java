package kunlun.action.sms;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Short messaging service send result.
 * @author Kahle
 */
public class SmsSendResult implements Serializable {
    private String code;
    private String description;
    private String requestId;
    private Collection<String>  messageIds;
    private Map<String, Object> others;

    public String getCode() {

        return code;
    }

    public void setCode(String code) {

        this.code = code;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getRequestId() {

        return requestId;
    }

    public void setRequestId(String requestId) {

        this.requestId = requestId;
    }

    public Collection<String> getMessageIds() {

        return messageIds;
    }

    public void setMessageIds(Collection<String> messageIds) {

        this.messageIds = messageIds;
    }

    public Map<String, Object> getOthers() {

        return others;
    }

    public void setOthers(Map<String, Object> others) {

        this.others = others;
    }
}
