package com.polarbookshop.edgeservice.user;

import java.util.List;

/**
 * @Author: WangZhenqi
 * @Description: 创建 User record 以存放认证用户的信息
 * @Date: Created in 2025-11-05 20:03
 * @Modified By:
 */
// 持有用户数据的不可变数据类
public record User(
        String usernname,
        String firstName,
        String lastName,
        List<String> roles
) {
}
