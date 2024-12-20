/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.config.action;

import kunlun.action.ActionUtils;
import kunlun.action.support.AutoAction;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * The action handler auto-configuration.
 * @author Kahle
 */
@Configuration
public class ActionHandlerAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ActionHandlerAutoConfiguration.class);

    public ActionHandlerAutoConfiguration(ApplicationContext appContext) {
        // If not have beans, handlerMap is empty map, not is null.
        Map<String, AutoAction> handlerMap = appContext.getBeansOfType(AutoAction.class);
        for (AutoAction autoAction : handlerMap.values()) {
            if (autoAction == null) { continue; }
            String actionName = autoAction.getName();
            if (StringUtils.isBlank(actionName)) {
                log.warn("The action \"{}\"'s name is blank, it will be ignored. "
                        , autoAction.getClass());
                continue;
            }
            ActionUtils.registerAction(actionName, autoAction);
        }
    }

}
