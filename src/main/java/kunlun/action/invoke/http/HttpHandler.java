/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.invoke.http;

import kunlun.core.Action;

import java.lang.reflect.Type;

@Deprecated // TODO: can delete
public interface HttpHandler extends Action {

    <T> T execute(HttpParameters httpParams, Type type);

}
