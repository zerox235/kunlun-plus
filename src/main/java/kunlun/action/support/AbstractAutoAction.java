/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support;

import kunlun.action.AbstractAction;
import kunlun.util.Assert;

/**
 * The abstract automated action.
 * @author Kahle
 */
public abstract class AbstractAutoAction extends AbstractAction implements AutoAction {
    private final String name;

    public AbstractAutoAction(String actionName) {
        Assert.notBlank(actionName, "Parameter \"actionName\" must not blank. ");
        this.name = actionName;
    }

    @Override
    public String getName() {

        return name;
    }

}
