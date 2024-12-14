/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support;

import kunlun.action.AbstractAction;
import kunlun.util.Assert;

/**
 * The abstract automated strategy action.
 * @author Kahle
 */
@Deprecated
public abstract class AbstractAutoStrategyAction extends AbstractAction implements AutoAction {
    private final String actionName;

    public AbstractAutoStrategyAction(String actionName) {
        Assert.notBlank(actionName, "Parameter \"actionName\" must not blank. ");
        this.actionName = actionName;
    }

    @Override
    public String getName() {

        return actionName;
    }

}
