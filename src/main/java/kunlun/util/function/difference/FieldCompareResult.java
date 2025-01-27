/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.util.function.difference;

/**
 * The field compare result.
 * @author Kahle
 */
public class FieldCompareResult {
    private String name;
    private String description;
    private Object oldValue;
    private Object newValue;

    public FieldCompareResult(String name, String description) {
        this.description = description;
        this.name = name;
    }

    public FieldCompareResult() {

    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public Object getOldValue() {

        return oldValue;
    }

    public void setOldValue(Object oldValue) {

        this.oldValue = oldValue;
    }

    public Object getNewValue() {

        return newValue;
    }

    public void setNewValue(Object newValue) {

        this.newValue = newValue;
    }
}
