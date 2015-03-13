package org.gaixie.jibu.service;

import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.JibuTestSupport;
import org.gaixie.jibu.model.Criteria;
import org.gaixie.jibu.model.Token;
import org.gaixie.jibu.model.User;
import org.gaixie.jibu.service.UserService;
import org.gaixie.jibu.utils.ConnectionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UserServiceTest extends JibuTestSupport {
    private UserService userService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws Exception {
	clearTable();
        initBaseTestData();
        userService = getInjector().getInstance(UserService.class);
    }


    @Test
    public void get() {
        User user = userService.get("test1");
        Assert.assertNotNull(user);
        User userbyid = userService.get(user.getId());
        Assert.assertTrue(user.equals(userbyid));
    }

    @Test
    public void login() {
        // 成功用用户名登录
        User user = userService.get("test1", "111111");
        Assert.assertNotNull(user);
        // 成功用邮箱登录
        user = userService.get("test1@gaixie.org", "111111");
        Assert.assertNotNull(user);
        // 登录失败
        user = userService.get("test1@gaixie.org", "wrongpwd");
        Assert.assertNull(user);
    }

    @Test
    public void generateToken() {
        Token token = null;
        // 不存在的用户得到的 token 为 null
        token = userService.generateToken("regist", "wrongusername", "newRegist@gaixie.org");
        Assert.assertNull(token);
        // 新注册用户的接受 token 的邮箱不能为空，否则返回的 token 为 null
        token = userService.generateToken("regist", "test1", null);
        Assert.assertNull(token);
        // 无效的 Token type 返回的 token 为 null
        token = userService.generateToken("wrongtype", "test1", "newRegist@gaixie.org");
        Assert.assertNull(token);

        token = userService.generateToken("regist", "test1", "newRegist@gaixie.org");
        Assert.assertNotNull(token);
        token = userService.generateToken("password", "test1", null);
        Assert.assertNotNull(token);
    }

    @Test (expected = JibuException.class)
    public void registTokenExpired() throws SQLException {
        // 使用初始化好的用于用户注册的 token
        String tokenValue = "9de4a97425678c5b1288aa70c1669a64";

        // 修改token的有效期，设置为当前时间（之前初始化为7天后），让token过期。
        Connection conn = ConnectionUtils.getConnection();
        QueryRunner run = new QueryRunner();
        String sql = "update tokens set expiration_ts = now() where value = '"+tokenValue+"'";
        run.update(conn, sql);
        DbUtils.commitAndClose(conn);
        User user = new User();
        user.setUsername("newRegist");
        user.setPassword("password");
        userService.regist(user, tokenValue);
    }

    @Test
    public void registPasswordIsNull() {
        // 使用初始化好的用于用户注册的 token
        String tokenValue = "9de4a97425678c5b1288aa70c1669a64";

        User user = new User();
        user.setUsername("newRegist");
        user.setPassword("password");

        // 密码为空抛出的 exception
        user.setPassword(null);
        thrown.expect(JibuException.class);
        thrown.expectMessage("[0100]");
        userService.regist(user, tokenValue);
    }

    @Test
    public void registTokenValueWrong() {
        // 使用初始化好的用于用户注册的 token
        String tokenValue = "9de4a97425678c5b1288aa70c1669a64";

        User user = new User();
        user.setUsername("newRegist");
        user.setPassword("password");

        // 不存在的 token 抛出的 exception
        thrown.expect(JibuException.class);
        thrown.expectMessage("[0101]");
        userService.regist(user, "wrongtokenvalue");
    }

    @Test
    public void registTokenTypeWrong() {
        // 使用初始化好的用于用户注册的 token
        String tokenValue = "9de4a97425678c5b1288aa70c1669a64";

        User user = new User();
        user.setUsername("newRegist");
        user.setPassword("password");

        // token 存在但类型不对时抛出的 exception
        // 使用初始化语句中用于密码找回的 tokenvalue
        thrown.expect(JibuException.class);
        thrown.expectMessage("[0102]");
        userService.regist(user, "5f4dcc3b5aa765d61d8327deb882cf99");
    }

    @Test
    public void registUsernameExist() {
        // 使用初始化好的用于用户注册的 token
        String tokenValue = "9de4a97425678c5b1288aa70c1669a64";

        User user = new User();
        user.setUsername("test1");
        user.setPassword("password");

        // test1 用户已存在
        thrown.expect(JibuException.class);
        thrown.expectMessage("[0104]");
        userService.regist(user, tokenValue);
    }

    @Test
    public void registEmailExist() throws SQLException {
        // 使用初始化好的用于用户注册的 token
        String tokenValue = "9de4a97425678c5b1288aa70c1669a64";

        // 修改token的 send_to 地址为已存在用户 test1 的邮件地址 test1@gaixie.org
        Connection conn = ConnectionUtils.getConnection();
        QueryRunner run = new QueryRunner();
        String sql = "update tokens set send_to = 'test1@gaixie.org' where value = '"+tokenValue+"'";
        run.update(conn, sql);
        DbUtils.commitAndClose(conn);

        User user = new User();
        user.setUsername("newRegist");
        user.setPassword("password");

        // 邮件地址已存在
        thrown.expect(JibuException.class);
        thrown.expectMessage("[0104]");
        userService.regist(user, tokenValue);
    }

    @Test
    public void regist() throws Exception {
        // 使用初始化好的用于用户注册的 token
        String tokenValue = "9de4a97425678c5b1288aa70c1669a64";
        User user = new User();
        user.setUsername("newRegist");
        user.setPassword("password");
        userService.regist(user, tokenValue);
        user = userService.get(user.getUsername());
        Assert.assertNotNull(user);
    }

    @Test
    public void find() {
        // jibu_db=> select username, fullname from userbase where username||fullname ~ 'er';
        //  username | fullname
        // ----------+----------
        //  test1    | Manager
        //  test3    | Register
        // (2 rows)
        List<User> users = userService.find("er", null);
        Assert.assertTrue(2 == users.size());

        Criteria crt = new Criteria();
        crt.setStart(0);
        crt.setLimit(1);
        crt.setSort("fullname");
        crt.setDir("DESC");

        // 分页测试，fullname 倒序
        Assert.assertTrue(0 == crt.getTotal());
        users = userService.find("er",crt);
        Assert.assertTrue(1 == users.size());
        Assert.assertTrue("Register".equals(users.get(0).getFullname()));
        Assert.assertTrue(2 == crt.getTotal());
        // 如果模糊匹配的字符串为 null ，则查处全部的3个用户。
        users = userService.find(null,crt);
        Assert.assertTrue(3 == crt.getTotal());
    }

    @After
    public void tearDown() {
	clearTable();
    }
}
