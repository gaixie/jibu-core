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

package org.gaixie.jibu.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.gaixie.jibu.utils.SQLBuilder;
import org.gaixie.jibu.dao.UserDAO;
import org.gaixie.jibu.model.Criteria;
import org.gaixie.jibu.model.User;
import org.gaixie.jibu.JibuException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User 数据访问接口的 PostgreSQL 实现。
 */
public class UserDAOPgSQL implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOPgSQL.class);
    private QueryRunner run = null;

    public UserDAOPgSQL() {
        this.run = new QueryRunner();
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，返回 User 的 password 属性为 null。</p>
     */
    public User get( Connection conn, int id) throws SQLException {
        ResultSetHandler<User> h = new BeanHandler<User>(User.class);
        String sql =
            "SELECT id,username,fullname,type,emailaddress,registered_ts,invited_by,enabled \n"+
            "FROM userbase \n"+
            "WHERE id=?";
        return run.query(conn, sql, h, id);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，返回 User 的 password 属性为 null。</p>
     */
    public User get( Connection conn, String username) throws SQLException {
        ResultSetHandler<User> h = new BeanHandler<User>(User.class);
        String sql =
            "SELECT id,username,fullname,type,emailaddress,registered_ts,invited_by,enabled \n"+
            "FROM userbase \n"+
            "WHERE username=?";
        return run.query(conn, sql, h, username);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，返回 User 的 password 属性为 null。</p>
     */
    public User get(Connection conn,String loginname, String cryptpassword) throws SQLException {
        ResultSetHandler<User> h = new BeanHandler<User>(User.class);
        String sql =
            "SELECT id,username,fullname,type,emailaddress,registered_ts,invited_by,enabled \n"+
            "FROM userbase \n";
        if (loginname.indexOf("@") < 0)
            sql = sql + "WHERE username=? AND password=? and enabled=true";
        else
            sql = sql + "WHERE emailaddress=? AND password=? and enabled=true";
        return run.query(conn, sql, h, loginname, cryptpassword);
    }


    public void save(Connection conn, User user) throws SQLException {
        run.update(conn
                   , "INSERT INTO userbase (fullname,username,password,emailaddress,type,invited_by) VALUES (?,?,?,?,?,?)"
                   , user.getFullname()
                   , user.getUsername()
                   , user.getPassword()
                   , user.getEmailaddress()
                   , user.getType()
                   , user.getInvited_by());
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，返回 List 中所有 User 的 password 属性为 null。</p>
     */
    public List<User> find(Connection conn, String str, Criteria criteria) throws SQLException {
        ResultSetHandler<List<User>> h = new BeanListHandler<User>(User.class);
        String sql = "\n";
        if (str !=null && !str.trim().isEmpty()) {
            sql = "WHERE username||fullname ~ '"+str+"' \n";
        }

        if (criteria != null && criteria.getLimit() >0) {
            ResultSetHandler<Long> scalar = new ScalarHandler<Long>(1);
            String totalSql = "SELECT COUNT(id) FROM userbase \n"+sql;
            int total = ((Long)run.query(conn, totalSql, scalar)).intValue();
            criteria.setTotal(total);
        }

        sql =
            "SELECT id,username,fullname,type,emailaddress,registered_ts,invited_by,enabled \n"+
            "FROM userbase \n"+sql;

        sql = SQLBuilder.getSortClause(sql,criteria);
        sql = SQLBuilder.getPagingClause(sql,criteria);
        return run.query(conn, sql, h);
    }
}
