/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.property.support;

import org.springframework.core.env.Environment;

import java.util.Set;

import static kunlun.util.Assert.notNull;

/**
 * SpringEnvPropertySource
 * @author Kahle
 */
public class SpringEnvPropertySource extends BaseReadOnlyPropertySource {
    private final Environment env;

    public SpringEnvPropertySource(Environment env) {

        this.env = notNull(env);
    }

    @Override
    public boolean containsProperty(String name) {

        return env.containsProperty(name);
    }

    @Override
    public Object getProperty(String name) {

        return env.getProperty(name);
    }

    @Override
    public Set<String> getPropertyNames() {

        throw new UnsupportedOperationException();
    }

}
