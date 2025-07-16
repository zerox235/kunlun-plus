/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support.jackson.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import kunlun.data.json.support.jackson.JsonSceneSerializer;
import kunlun.data.json.support.jackson.model.Scene;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JsonSceneSerialize
 * @see Scene
 * @author Zerox
 */
@JacksonAnnotationsInside
@JsonSerialize(using = JsonSceneSerializer.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface JsonSceneSerialize {

    /**
     * scene
     * @return scene
     */
    Scene value() default Scene.DEFAULT;

    /**
     * custom
     * @return custom
     */
    String custom() default "";

}
