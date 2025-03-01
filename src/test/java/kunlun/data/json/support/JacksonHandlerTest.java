/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support;

import kunlun.common.constant.Words;
import kunlun.data.json.JsonUtil;
import kunlun.data.mock.MockUtil;
import kunlun.entity.Student;
import kunlun.util.TypeUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static kunlun.data.json.JsonFormat.PRETTY_FORMAT;

public class JacksonHandlerTest {
    private final List<Student> data1 = new ArrayList<Student>();
    private Student data = new Student();
    private String jsonString = null;
    private String jsonString1 = null;

    @Before
    public void init() {
        JsonUtil.registerHandler(Words.DEFAULT, new JacksonHandler());
        data = MockUtil.mock(Student.class);
        for (int i = 0; i < 5; i++) {
            data1.add(MockUtil.mock(Student.class));
        }
        jsonString = JsonUtil.toJsonString(data, PRETTY_FORMAT);
        jsonString1 = JsonUtil.toJsonString(data1, PRETTY_FORMAT);
    }

    @Test
    public void test1() {
        System.out.println(JsonUtil.toJsonString(data, PRETTY_FORMAT));
        System.out.println(JsonUtil.toJsonString(data1, PRETTY_FORMAT));
        System.out.println(JsonUtil.toJsonString(data, PRETTY_FORMAT));
    }

    @Test
    public void test2() {
        JsonUtil.registerHandler(Words.DEFAULT, new JacksonHandler());
        Student student = JsonUtil.parseObject(jsonString, Student.class);
        List<Student> list = JsonUtil.parseObject(jsonString1
                , TypeUtil.parameterizedOf(List.class, Student.class));
        System.out.println(JsonUtil.toJsonString(student, PRETTY_FORMAT));
        System.out.println("----");
        for (Student student1 : list) {
            System.out.println(JsonUtil.toJsonString(student1));
        }
    }

}
