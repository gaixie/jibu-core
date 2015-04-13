/**
 * Copyright (C) 2014 Gaixie.ORG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gaixie.jibu.service;

import java.util.List;

import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.model.Criteria;
import org.gaixie.jibu.model.User;
import org.gaixie.jibu.model.Token;

/**
 * User 服务接口。
 */
public interface UserService {

    /**
     * 通过 User Id 得到 User。
     *
     * @param id User Id
     * @return 一个 User，如果没有对应的数据，返回 null。
     */
    public User get(int id);

    /**
     * 通过 username 得到 User。
     *
     * @param username User username。
     * @return 一个 User，如果没有对应的数据，返回 null。
     */
    public User get(String username) ;

    /**
     * 通过 loginname 和明文 password 得到 User。
     * 用于用户身份验证。
     *
     * @param loginname 用户的 username 或 emailaddress。
     * @param password 用户的明文 password。
     * @return 一个 User，如果没有对应的数据，返回 null。如果两个参数任意一个为空，也返回 null。
     */
    public User get(String loginname, String password) ;

    /**
     * 得到一个新生成的令牌，可用于密码找回，用户注册等。
     *
     * @param type 令牌的使用类型。
     * @param createdBy 生成这个令牌的用户 username。
     * @param sendTo 如果用于注册用户，为新注册用户接受令牌的邮箱。
     * @return 一个有效的 Token，如果生成失败，返回null。
     */
    public Token generateToken(String type, String createdBy, String sendTo);

    /**
     * 通过 Token 注册一个新 User，password 不能为空。
     *
     * @param user 要注册的用户，至少需要有效的 username 和明文 password。
     * @param tokenValue 用于邀请用户注册的 token。
     * @exception JibuException 注册失败时抛出。
     */
    public void regist(User user, String tokenValue) throws JibuException;

    /**
     * 根据给定字符串模糊匹配的用户名或者用户全名，并可以 Criteria 给定的约束分页返回。
     *
     * @param str 要在用户名和用户全名中匹配查找的字符串。
     * @param criteria 传递分页，排序等附加的查询条件，如果不分页该参数为 null。
     * @return 一个包含 User 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<User> find(String str, Criteria criteria);

    /**
     * 通过 Token 及对应的 username 进行登录验证。
     *
     * @param username 要登录的用户名。
     * @param tokenValue 用于自动登录的 token 值。
     * @return 一个过期时间被重置的 Token，如果登录验证失败，返回null。
     */
    public Token signinByToken(String username, String tokenValue);

    /**
     * 用户登出时清空自动登录的 token。
     *
     * @param username 要登出的用户名。
     * @param tokenValue 用于自动登录的 token 值。
     */
    public void signout(String username, String tokenValue);
}
