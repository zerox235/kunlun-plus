/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The field metadata.
 * @author Kahle
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMeta {

    /**
     * Get the field description.
     * @return The field description
     */
    String description() default "";

    /**
     * Get the extra information for the field.
     * @return The extra information
     */
    String extra() default "";

    /**
     * Get the field sort.
     * @return The field sort
     */
    int    sort()  default 0;

}
