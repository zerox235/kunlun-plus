/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support;

import kunlun.core.Action;

/**
 * The automated action.
 * @author Kahle
 */
public interface AutoAction extends Action {

    /**
     * Get the action name.
     * @return The action name
     */
    String getName();

}
