/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.bean.support;

import com.alibaba.fastjson.JSON;
import kunlun.data.bean.BeanUtil;
import kunlun.data.mock.MockUtil;
import kunlun.entity.Person;
import kunlun.entity.Student;
import org.junit.Test;

/**
 * The bean tools Test.
 * @author Kahle
 */
public class BeanUtilTest {

    @Test
    public void testIgnoreCglibCopy() {
        Person person = MockUtil.mock(Person.class);
        // BeanUtil.setBeanCopier(new CglibBeanCopier());
        BeanUtil.setBeanCopier(new SpringCglibBeanCopier());
        Student student = new Student();
        BeanUtil.copy(person, student);
        System.out.println(JSON.toJSONString(student));
    }

}
