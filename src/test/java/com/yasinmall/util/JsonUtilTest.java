package com.yasinmall.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yasinmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * @author Yasin Zhang
 */
@Slf4j
public class JsonUtilTest {

    @Test
    public void jsonUtilTest() {
        User user1 = new User();
        user1.setId(1);
        user1.setEmail("yasinzhang@163.com");
        user1.setCreateTime(new Date());
        User user2 = new User();
        user2.setId(2);
        user2.setEmail("yasinzhang@163.com");

        String userJson = JsonUtil.obj2String(user1);
        String userJsonPretty = JsonUtil.obj2StringPretty(user1);
        log.info("userJson: {}", userJson);
        log.info("userPrettyJson: {}", userJsonPretty);

        User userTest = JsonUtil.string2Obj(userJson, User.class);
        log.info("get obj: {}", user1);

        List<User> userList = Lists.newArrayList();
        userList.add(user1);
        userList.add(user2);

        String userListStr = JsonUtil.obj2StringPretty(userList);
        log.info(userListStr);

        List<User> userListObj1 = JsonUtil.string2Obj(userListStr, new TypeReference<List<User>>() {});
        log.info(userListObj1.toString());

        List<User> userListObj2 = JsonUtil.string2Obj(userListStr, List.class, User.class);
        log.info(userListObj2.toString());
    }
}
