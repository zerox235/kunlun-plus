/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.invoke.http;

import kunlun.action.support.AbstractAutoAction;
import kunlun.util.Assert;

@Deprecated // TODO: can delete
public abstract class AbstractHttpAction extends AbstractAutoAction implements HttpHandler {

    public AbstractHttpAction(String actionName) {

        super(actionName);
    }

    protected abstract Object execute(HttpParameters httpParams);

    @Override
    public Object execute(String strategy, Object input, Object[] arguments) {
        Assert.isInstanceOf(HttpParameters.class, input
                , "Parameter \"input\" must instance of \"HttpParameters\". ");
        return execute((HttpParameters) input, null);
    }

}
