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

package org.gaixie.jibu.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.gaixie.jibu.model.Criteria;
import org.gaixie.jibu.model.User;

/**
 * User 数据访问对象接口。
 *
 */
public interface UserDAO {

    /**
     * 通过 id 得到一个 User。
     *
     * @param conn 一个有效的数据库链接。
     * @param id User id。
     *
     * @throws SQLException SQLException
     * @return 一个 User，如果没有对应的数据，返回 null。
     */
    public User get(Connection conn, int id) throws SQLException;

    /**
     * 通过 username 得到一个 User。
     *
     * @param conn 一个有效的数据库链接。
     * @param username User username。
     *
     * @throws SQLException SQLException
     * @return 一个 User，如果没有对应的数据，返回 null。
     */
    public User get(Connection conn, String username) throws SQLException;

    /**
     * 通过 email 得到一个 User。
     *
     * @param conn 一个有效的数据库链接。
     * @param email 用户主邮箱。
     *
     * @throws SQLException SQLException
     * @return 一个 User，如果没有对应的数据，返回 null。
     */
    public User getByEmail(Connection conn, String email) throws SQLException;

    /**
     * 通过 loginname 和密文 password 得到一个 User。
     *
     * @param conn 一个有效的数据库链接。
     * @param loginname 可以是 username 或 emailaddress。
     * @param cryptpassword User 密文 password。
     *
     * @throws SQLException SQLException
     * @return 一个 User，如果没有对应的数据，返回 null。
     */
    public User get(Connection conn, String loginname, String cryptpassword) throws SQLException;

    /**
     * 增加一个新 User。
     *
     * @param conn 一个有效的数据库链接。
     * @param user User。
     *
     * @throws SQLException SQLException
     */
    public void save(Connection conn, User user) throws SQLException;

    /**
     * 根据给定字符串模糊匹配的用户名或者用户全名，并可以 Criteria 给定的约束分页返回。
     *
     * @param conn 一个有效的数据库链接。
     * @param str 要在用户名和用户全名中匹配查找的字符串。
     * @param criteria 传递分页，排序等附加的查询条件，如果不分页该参数为 null。
     *
     * @throws SQLException SQLException
     * @return 一个包含 User 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<User> find(Connection conn, String str, Criteria criteria) throws SQLException;

    /**
     * 更新 User。
     *
     * @param conn 一个有效的数据库链接。
     * @param user 所有非空的属性除了 id 以外都会被更新。
     *
     * @throws SQLException SQLException
     */
    public void update(Connection conn, User user) throws SQLException;
}
