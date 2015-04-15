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

package org.gaixie.jibu.service.impl;

import com.google.inject.Inject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.dao.UserDAO;
import org.gaixie.jibu.dao.TokenDAO;
import org.gaixie.jibu.model.Criteria;
import org.gaixie.jibu.model.User;
import org.gaixie.jibu.model.Token;
import org.gaixie.jibu.service.UserService;
import org.gaixie.jibu.utils.ConnectionUtils;
import org.gaixie.jibu.utils.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User 服务接口的默认实现。
 */
public class UserServiceImpl implements UserService {
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDAO userDAO;
    private final TokenDAO tokenDAO;

    /**
     * 使用 Guice 进行 DAO 的依赖注入。
     * @param userDAO UserDAO
     * @param tokenDAO TokenDAO
     */
    @Inject
    public UserServiceImpl(UserDAO userDAO, TokenDAO tokenDAO) {
        this.userDAO = userDAO;
        this.tokenDAO = tokenDAO;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，password 属性为 null。</p>
     */
    public User get(int id) {
        Connection conn = null;
        User user = null;
        try {
            conn = ConnectionUtils.getConnection();
            user = userDAO.get(conn,id);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return user;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，password 属性为 null。</p>
     */
    public User get(String username) {
        Connection conn = null;
        User user = null;
        try {
            conn = ConnectionUtils.getConnection();
            user = userDAO.get(conn,username);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return user;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，password 属性为 null。</p>
     */
    public User get(String loginname, String password) {
        Connection conn = null;
        User user = null;
        if (loginname == null || password == null) return null;
        String cryptpassword = MD5.encodeString(password,null);
        try {
            conn = ConnectionUtils.getConnection();
            user = userDAO.get(conn,loginname,cryptpassword);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return user;
    }

    public Token generateToken(String type, String createdBy, String sendTo) {
        Connection conn = null;
        Token token = null;
        int duration = 0;

        try {
            conn = ConnectionUtils.getConnection();
            User user = null;
            // 密码重置的 createBy 为 null，用 sendTo 取得 User
            if (type.equals("password"))
                user = userDAO.getByEmail(conn,sendTo);
            else
                user = userDAO.get(conn,createdBy);
            if (user == null) return null;

            token = new Token();

            if (type.equals("regist")) {
                // 如果 type 为 regist， sendTo 参数不能为空。
                // 要求注册用户的 Token 有效期为 7 天
                if (sendTo == null) return null;
                duration = +7;
                token.setSend_to(sendTo);
            } else if (type.equals("password")) {
                // 要求密码找回的 Token 有效期为 1 天
                // 密码找回的 Token 的 send_to 为 创建人邮箱
                duration = +1;
                token.setSend_to(sendTo);
            } else if (type.equals("signin")) {
                // 要求自动登录的 Token 有效期为 14 天
                // 自动登录的 Token 的 send_to 为 创建人邮箱
                duration = +14;
                token.setSend_to(user.getEmailaddress());
            } else {
                return null;
            }

            Random randomGenerator = new Random();
            long randomLong = randomGenerator.nextLong();
            Calendar calendar = Calendar.getInstance();
            calendar.add(calendar.DAY_OF_MONTH, duration);
            long time = calendar.getTimeInMillis();
            Timestamp ts = new Timestamp(time);
            String key = MD5.encodeString(Long.toString(randomLong) + time, null);

            token.setValue(key);
            token.setType(type);
            token.setExpiration_ts(ts);
            token.setCreated_by(user.getId());

            tokenDAO.save(conn,token);
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            logger.error(e.getMessage());
            return null;
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return token;
    }

    public void regist(User user, String tokenValue) throws JibuException {
        Connection conn = null;
        if (null == user.getPassword())
            throw new JibuException("[0100]: password 不能为空。");

        try {
            conn = ConnectionUtils.getConnection();
            Token token = tokenDAO.get(conn,tokenValue);

            if (token == null)
                throw new JibuException("[0101]: token 不存在。");
            // token 的类型必须时 register
            if (!"register".equals(token.getType()))
                throw new JibuException("[0102]: token 不存在。");

            Calendar calendar = Calendar.getInstance();
            long time = calendar.getTimeInMillis();
            Timestamp ts = new Timestamp(time);
            if(ts.after(token.getExpiration_ts()))
                throw new JibuException("[0103]: token 已过期。");

            // ~~~~~~~~~~~~~~~~ 用户属性设置 ~~~~~~~~~~~~~~~~~~~//
            // password 这里不能为 null，但空串需要 View 层检验
            // token 的创建人就是邀请人
            // token 发送的邮箱就是新用户的注册邮箱
            // 注册时的 fullname 默认为 username
            // 新注册用户的 type 都默认为 1（注册用户）
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
            String cryptpassword = MD5.encodeString(user.getPassword(),null);
            user.setPassword(cryptpassword);
            user.setInvited_by(token.getCreated_by());
            user.setEmailaddress(token.getSend_to());
            if (null == user.getFullname())
                user.setFullname(user.getUsername());
            user.setType(1);

            userDAO.save(conn,user);
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            if (e.getMessage().contains("unique constraint"))
                throw new JibuException("[0104]: 注册的用户名或邮箱已存在。", e);
            else
                throw new RuntimeException("[0002]: 未知的数据库访问错误。", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，返回 List 中所有 User 的 password 属性为 null。</p>
     */
    public List<User> find(String str, Criteria criteria) {
        Connection conn = null;
        List<User> users = null;
        try {
            conn = ConnectionUtils.getConnection();
            users = userDAO.find(conn,str,criteria);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return users;
    }

    public Token signinByToken(String username, String tokenValue) {
        Connection conn = null;
        Token token = null;
        User user = null;
        try {
            conn = ConnectionUtils.getConnection();
            user = userDAO.get(conn,username);
            if (user != null) {
                token = tokenDAO.get(conn, user.getId(), tokenValue);
                if (token != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(calendar.DAY_OF_MONTH, 14);
                    long time = calendar.getTimeInMillis();
                    Timestamp ts = new Timestamp(time);
                    token.setExpiration_ts(ts);
                    tokenDAO.update(conn,token);
                    DbUtils.commitAndClose(conn);
                }
            }
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            logger.error(e.getMessage());
            return null;
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return token;
    }

    public void signout(String username, String tokenValue) {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            User user = userDAO.get(conn,username);
            if (user != null) {
                Token token = tokenDAO.get(conn, user.getId(), tokenValue);
                if (token != null) {
                    tokenDAO.delete(conn,token);
                    DbUtils.commitAndClose(conn);
                }
            }
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    public void resetPassword(String password, String tokenValue)
        throws JibuException {
        if (password == null || password.isEmpty()) {
            throw new JibuException("[0100]: password 不能为空。");
        }

        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            Token token = tokenDAO.get(conn,tokenValue);

            if (token == null)
                throw new JibuException("[0101]: token 不存在。");
            // token 的类型必须时 password
            if (!"password".equals(token.getType()))
                throw new JibuException("[0102]: token 不存在。");

            Calendar calendar = Calendar.getInstance();
            long time = calendar.getTimeInMillis();
            Timestamp ts = new Timestamp(time);
            if(ts.after(token.getExpiration_ts()))
                throw new JibuException("[0103]: token 已过期。");

            User user = new User();
            String cryptpassword = MD5.encodeString(password,null);
            user.setPassword(cryptpassword);
            user.setId(token.getCreated_by());
            userDAO.update(conn,user);
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new RuntimeException("[0002]: 未知的数据库访问错误。", e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }
}
