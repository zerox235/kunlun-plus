/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.util.function.difference;

import com.alibaba.fastjson.JSON;
import kunlun.data.mock.MockUtil;
import kunlun.entity.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FieldDifferenceComparatorTest {
    private static final FieldDifferenceComparator comparator1 = new FieldDifferenceComparator(true);
    private static final FieldDifferenceComparator comparator = new FieldDifferenceComparator();
    private static final Logger log = LoggerFactory.getLogger(FieldDifferenceComparatorTest.class);

    @Test
    public void test1() {
        User user1 = MockUtil.mock(User.class);
        User user2 = MockUtil.mock(User.class);
        List<FieldCompareResult> list = comparator.apply(user1, user2);
        log.info("compare: {}", JSON.toJSONString(list, true));
    }

    @Test
    public void test2() {
        User user1 = MockUtil.mock(User.class);
        User user2 = MockUtil.mock(User.class);
        user2.setUsername(null);
        user2.setPassword(null);
        List<FieldCompareResult> list = comparator1.apply(user1, user2);
        log.info("compare: {}", JSON.toJSONString(list, true));
    }

}
