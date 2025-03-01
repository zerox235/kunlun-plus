/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.mock;

import com.alibaba.fastjson.JSON;
import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import kunlun.mock.support.*;
import kunlun.test.pojo.entity.other.Book;
import kunlun.test.pojo.entity.system.User;
import kunlun.util.TypeUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockUtilTest {
    private static final Logger log = LoggerFactory.getLogger(MockUtilTest.class);

    static {

        MockUtil.registerHandler(MockUtil.getDefaultHandlerName(), new JMockDataHandler());
    }

    @Test
    public void testMock1() {
        Book book = MockUtil.mock(Book.class);
        log.info(JSON.toJSONString(book));
        User user = MockUtil.mock(User.class);
        log.info(JSON.toJSONString(user));
    }

    @Test
    public void testMock2() {
        List<Book> bookList = MockUtil.mock(TypeUtil.parameterizedOf(List.class, Book.class));
        log.info(JSON.toJSONString(bookList, Boolean.TRUE));
    }

    @Test
    public void testMock3() {
//        Menu menu = MockUtil.mock(Menu.class);
//        System.out.println(JSON.toJSONString(menu));
    }

    @Test
    public void testMock11() {
        MockUtil.registerHandler(MockUtil.getDefaultHandlerName(), new SimpleMockHandler1());
        ClassMockerConfig classMockerConfig = new ClassMockerConfig();
        classMockerConfig.setType(User.class);
        Map<String, Mocker> map = new HashMap<String, Mocker>();
        map.put("name", new NameMocker());
        map.put("author", new NameMocker());
        classMockerConfig.setMockerMap(map);

        User[] bookArray = MockUtil.mock(User[].class, classMockerConfig);
        System.out.println(JSON.toJSONString(bookArray, Boolean.TRUE));
    }

}
