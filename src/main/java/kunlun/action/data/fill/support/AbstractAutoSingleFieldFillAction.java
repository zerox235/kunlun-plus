/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.data.fill.support;

import kunlun.action.data.fill.AbstractSingleFieldFillAction;
import kunlun.action.support.AutoAction;
import kunlun.util.Assert;

/**
 * The abstract auto single field fill action.
 * @author Kahle
 */
public abstract class AbstractAutoSingleFieldFillAction
        extends AbstractSingleFieldFillAction implements AutoAction {
    private final String name;

    public AbstractAutoSingleFieldFillAction(String actionName) {
        Assert.notBlank(actionName, "Parameter \"actionName\" must not blank. ");
        this.name = actionName;
    }

    @Override
    public String getName() {

        return name;
    }

}
