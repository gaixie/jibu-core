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

package org.gaixie.jibu;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.gaixie.jibu.JibuConfig;
import org.gaixie.jibu.dao.DAOModule;
import org.gaixie.jibu.service.ServiceModule;
import org.gaixie.jibu.utils.ConnectionUtils;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;


/**
 * 需要Guice注入的测试类的父类，使测试类取得injector，通过同步化保证injector只被创建一
 * 次，同时通过读取jibu.properties文件的databaseType，使DAO的测试类可以无须修改就
 * 测试多种类型的数据库。
 */
public class JibuTestSupport {
    private static Injector injector;

    public synchronized Injector getInjector() {
        if(injector != null){
            return injector;
        }
        String databaseType = JibuConfig.getProperty("databaseType");
        injector = Guice.createInjector(new ServiceModule(),
                                        new DAOModule(databaseType));
        return injector;
    }

    /**
     * 测试用例运行前需要清空所有表的数据。
     * 清表的顺序参考：
     */
    protected void clearTable() {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            QueryRunner run = new QueryRunner();
            run.update(conn, "DELETE from schema_changes");
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            System.out.println(e.getMessage());
        }
    }
}
