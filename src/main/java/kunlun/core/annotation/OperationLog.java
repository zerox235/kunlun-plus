/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.core.annotation;

import java.lang.annotation.*;

/**
 * Record API access information and print parameters logs.
 * @author Kahle
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * The name of the API.
     * @return The name
     */
    String name();

    /**
     * Whether to record the input parameters.
     * @return True or false
     */
    boolean input() default true;

    /**
     * Whether to record the output result.
     * @return True or false
     */
    boolean output() default false;

    /**
     * Whether to print parameters logs.
     * @return True or false
     */
    boolean print() default true;

}
