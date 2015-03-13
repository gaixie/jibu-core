package org.gaixie.jibu.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.gaixie.jibu.model.Token;

/**
 * Token 数据访问对象接口。
 */
public interface TokenDAO {

    /**
     * 通过 id 得到一个 Token。
     *
     * @param conn 一个有效的数据库链接。
     * @param id Token id。
     *
     * @throws SQLException SQLException
     * @return 一个 Token，如果没有对应的数据，返回 null。
     */
    public Token get(Connection conn, int id) throws SQLException;

    /**
     * 通过 key 得到一个 Token。
     *
     * @param conn 一个有效的数据库链接。
     * @param value Token value。
     *
     * @throws SQLException SQLException
     * @return 一个 Token，如果没有对应的数据，返回 null。
     */
    public Token get(Connection conn, String value) throws SQLException;

    /**
     * 增加一个新的 Token。
     *
     * @param conn 一个有效的数据库链接。
     * @param token 要增加的 Token。
     *
     * @throws SQLException SQLException
     */
    public void save(Connection conn, Token token) throws SQLException;

    /**
     * 删除 Token。
     *
     * @param conn 一个有效的数据库链接。
     * @param token 要删除的 Token。
     *
     * @throws SQLException SQLException
     */
    public void delete(Connection conn, Token token) throws SQLException;
}
