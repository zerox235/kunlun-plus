package kunlun.action.sms;

import kunlun.action.AbstractAction;
import kunlun.core.function.Function;
import kunlun.util.StrUtil;

import java.io.Serializable;

import static kunlun.common.constant.Words.BATCH_SEND;
import static kunlun.common.constant.Words.SEND;

public abstract class AbstractSmsAction extends AbstractAction {
    private Function<String, SmsConfig> configSupplier;

    public Function<String, SmsConfig> getConfigSupplier() {

        return configSupplier;
    }

    public void setConfigSupplier(Function<String, SmsConfig> configSupplier) {

        this.configSupplier = configSupplier;
    }

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {
        if (StrUtil.isBlank(strategy) || SEND.equals(strategy)) {
            SmsMessage smsMsg = (SmsMessage) input;
            return send(getConfigSupplier().apply(smsMsg.getConfigId()), smsMsg);
        } else if (BATCH_SEND.equals(strategy)) {
            SmsMessage smsMsg = (SmsMessage) input;
            return batchSend(getConfigSupplier().apply(smsMsg.getConfigId()), smsMsg);
        } else {
            throw new UnsupportedOperationException("The strategy name is not supported! ");
        }
    }

    public abstract Object send(SmsConfig config, SmsMessage smsMsg);

    public abstract Object batchSend(SmsConfig config, SmsMessage smsMsg);

    public static class SmsConfig implements Serializable {
        private Boolean debug;

        public Boolean getDebug() {

            return debug;
        }

        public void setDebug(Boolean debug) {

            this.debug = debug;
        }
    }

}
