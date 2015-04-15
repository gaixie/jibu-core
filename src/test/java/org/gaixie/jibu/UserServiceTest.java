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
import java.sql.Timestamp;
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
        User user = userService.get("qq");
        Assert.assertNotNull(user);
        User userbyid = userService.get(user.getId());
        Assert.assertTrue(user.equals(userbyid));
    }

    @Test
    public void login() {
        // 成功用用户名登录
        User user = userService.get("qq", "111111");
        Assert.assertNotNull(user);
        // 成功用邮箱登录
        user = userService.get("nodto@qq.com", "111111");
        Assert.assertNotNull(user);
        // 登录失败
        user = userService.get("nodto@qq.com", "wrongpwd");
        Assert.assertNull(user);
    }

    @Test
    public void generateToken() {
        Token token = null;
        // 不存在的用户得到的 token 为 null
        token = userService.generateToken("regist", "wrongusername", "newRegist@test");
        Assert.assertNull(token);
        // 新注册用户的接受 token 的邮箱不能为空，否则返回的 token 为 null
        token = userService.generateToken("regist", "qq", null);
        Assert.assertNull(token);
        // 无效的 Token type 返回的 token 为 null
        token = userService.generateToken("wrongtype", "qq", "newRegist@test");
        Assert.assertNull(token);

        token = userService.generateToken("regist", "qq", "newRegist@test");
        Assert.assertNotNull(token);
        token = userService.generateToken("password", "qq", "nodto@qq.com");
        Assert.assertNotNull(token);
        token = userService.generateToken("signin", "qq", null);
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
        user.setUsername("qq");
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

        // 修改token的 send_to 地址为已存在用户 test1 的邮件地址 nodto@qq.com
        Connection conn = ConnectionUtils.getConnection();
        QueryRunner run = new QueryRunner();
        String sql = "update tokens set send_to = 'nodto@qq.com' where value = '"+tokenValue+"'";
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
        //  qq       | Manager
        //  sohu     | Register
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

    @Test
    public void signinByToken() {
        // 使用初始化好的用于自动登录的 token
        String tokenValue = "cc0256df40cbc924af2b31aeccb869b0";
        // 用户名不匹配，返回 null
        Token token = userService.signinByToken("sohu",tokenValue);
        Assert.assertNull(token);
        token = userService.signinByToken("qq",tokenValue);
        Assert.assertNotNull(token);

        // 更新后的 token，有效期大于更新前的 token
        Token updateToken = userService.signinByToken("qq",tokenValue);
        Assert.assertNotNull(updateToken);
        Timestamp ts = updateToken.getExpiration_ts();
        Assert.assertTrue(ts.after(token.getExpiration_ts()));
    }

    @Test
    public void signout() {
        // 使用初始化好的用于自动登录的 token
        String tokenValue = "cc0256df40cbc924af2b31aeccb869b0";
        Token token = userService.signinByToken("qq",tokenValue);
        Assert.assertNotNull(token);

        // signout后的不能自动登录
        userService.signout("qq",tokenValue);
        token  = userService.signinByToken("qq",tokenValue);
        Assert.assertNull(token);
    }

    @Test (expected = JibuException.class)
    public void resetPasswordExpired() throws SQLException {
        // 使用初始化好的用于密码重置的 token (test3)
        String tokenValue = "5f4dcc3b5aa765d61d8327deb882cf99";

        // 修改token的有效期，设置为当前时间（之前初始化为1天后），让token过期。
        Connection conn = ConnectionUtils.getConnection();
        QueryRunner run = new QueryRunner();
        String sql = "update tokens set expiration_ts = now() where value = '"+tokenValue+"'";
        run.update(conn, sql);
        DbUtils.commitAndClose(conn);
        userService.resetPassword("password", tokenValue);
    }

    @Test
    public void resetPasswordIsNull() {
        // 使用初始化好的用于密码重置的 token (test3)
        String tokenValue = "5f4dcc3b5aa765d61d8327deb882cf99";

        // 密码为空抛出的 exception
        thrown.expect(JibuException.class);
        thrown.expectMessage("[0100]");
        userService.resetPassword(null, tokenValue);
    }

    @Test
    public void resetPasswordTokenValueWrong() {
        // 不存在的 token 抛出的 exception
        thrown.expect(JibuException.class);
        thrown.expectMessage("[0101]");
        userService.resetPassword("password", "wrongtokenvalue");
    }

    @Test
    public void resetPasswordTokenTypeWrong() {
        // token 存在但类型不对时抛出的 exception
        // 使用初始化语句中用于用户注册的 tokenvalue
        thrown.expect(JibuException.class);
        thrown.expectMessage("[0102]");
        userService.resetPassword("password", "9de4a97425678c5b1288aa70c1669a64");
    }

    @Test
    public void resetPassword() throws Exception {
        // 使用初始化好的用于密码重置的 token (test3)
        String tokenValue = "5f4dcc3b5aa765d61d8327deb882cf99";

        userService.resetPassword("newPassword", tokenValue);
        User user = userService.get("sohu", "newPassword");
        Assert.assertNotNull(user);
    }

    @After
    public void tearDown() {
	clearTable();
    }
}
