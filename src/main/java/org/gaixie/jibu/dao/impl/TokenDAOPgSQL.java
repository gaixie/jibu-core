package org.gaixie.jibu.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.gaixie.jibu.dao.TokenDAO;
import org.gaixie.jibu.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Token 数据访问接口的 PostgreSQL 实现。
 */
public class TokenDAOPgSQL implements TokenDAO {
    private static final Logger logger = LoggerFactory.getLogger(TokenDAOPgSQL.class);
    private QueryRunner run = null;

    public TokenDAOPgSQL() {
        this.run = new QueryRunner();
    }

    public Token get(Connection conn, int id) throws SQLException {
        ResultSetHandler<Token> h = new BeanHandler<Token>(Token.class);
        return run.query(conn
                         , "SELECT id,value,type,expiration_ts,send_to,created_by FROM tokens WHERE id=? "
                         , h
                         , id);
    }

    public Token get(Connection conn, String value) throws SQLException {
        ResultSetHandler<Token> h = new BeanHandler<Token>(Token.class);
        return run.query(conn
                         , "SELECT id,value,type,expiration_ts,send_to,created_by FROM tokens WHERE value=? "
                         , h
                         , value);
    }

    public void save(Connection conn, Token token) throws SQLException {
        run.update(conn
                   , "INSERT INTO tokens (value,type,expiration_ts,send_to,created_by) VALUES (?,?,?,?,?)"
                   , token.getValue()
                   , token.getType()
                   , token.getExpiration_ts()
                   , token.getSend_to()
                   , token.getCreated_by());
    }

    /**
     * {@inheritDoc}
     * <p>
     * token.getId() 不能为 null。</p>
     */
    public void delete(Connection conn, Token token) throws SQLException {
        run.update(conn
                   , "DELETE FROM tokens WHERE id=?"
                   , token.getId());
    }
}
