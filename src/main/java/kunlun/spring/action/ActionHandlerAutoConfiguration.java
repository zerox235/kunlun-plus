/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.action;

import kunlun.action.ActionUtil;
import kunlun.action.support.AutoAction;
import kunlun.core.Action;
import kunlun.core.annotation.Name;
import kunlun.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Map;

/**
 * The action handler autoconfiguration.
 * @author Kahle
 */
@Configuration
public class ActionHandlerAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ActionHandlerAutoConfiguration.class);

    public ActionHandlerAutoConfiguration(ApplicationContext appContext) {
        // If not have beans, handlerMap is empty map, not is null.
        /*Map<String, AutoAction> handlerMap = appContext.getBeansOfType(AutoAction.class);
        for (AutoAction autoAction : handlerMap.values()) {
            if (autoAction == null) { continue; }
            String actionName = autoAction.getName();
            if (StrUtil.isBlank(actionName)) {
                log.warn("The action \"{}\"'s name is blank, it will be ignored. "
                        , autoAction.getClass());
                continue;
            }
            ActionUtil.registerAction(actionName, autoAction);
        }*/
        Map<String, Action> actionMap = appContext.getBeansOfType(Action.class);
        for (Action action : actionMap.values()) {
            if (action == null) { continue; }
            String actionName;
            if (action instanceof AutoAction) {
                actionName = ((AutoAction) action).getName();
            } else {
                Name name = AnnotationUtils.findAnnotation(action.getClass(), Name.class);
                if (name == null) { continue; }
                actionName = name.value();
            }
            if (StrUtil.isBlank(actionName)) {
                log.warn("The action \"{}\"'s name is blank, it will be ignored. "
                        , action.getClass());
                continue;
            }
            ActionUtil.registerAction(actionName, action);
        }
    }

}
