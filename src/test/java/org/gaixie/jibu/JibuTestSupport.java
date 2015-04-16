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
     *
     * 清表的顺序参考：
     * http://gaixie.org/jibu/schema-doc/snapshot/deletionOrder.txt
     * https://raw.githubusercontent.com/gaixie/jibu-schema/develop/postgresql/data/truncate-all.sql
     */
    protected void clearTable() {
        StringBuilder sb = new StringBuilder();
        //--------------------------------------------
        //  执行下面的命令生成清表的Java语句：
        //  curl http://gaixie.org/jibu/schema-doc/snapshot/deletionOrder.txt | sed 's/^/sb.append("TRUNCATE /;s/$/ CASCADE;");/'
        //  也可以执行通过 truncate-all.sql 来清空数据。
        //  more ./postgresql/data/truncate-all.sql | sed '/^$\|^\s*\(-\|$\)/d;s/.*/sb.append("&\\n");/'
        //  mac osx 下 sed 语法和 linux 不同，执行下面的命令：
        //  more ./postgresql/data/truncate-all.sql | sed '/^[[:blank:]]*-/d;/^[[:blank:]]*$/d;s/.*/sb.append("&\\n");/'
        //--------------------------------------------
        sb.append("truncate\n");
        sb.append("schema_changes,\n");
        sb.append("tokens,\n");
        sb.append("userbase;\n");

        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            QueryRunner run = new QueryRunner();
            run.update(conn, sb.toString());
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            System.out.println(e.getMessage());
        }
    }

    /**
     * 初始化基本的测试数据。
     *
     * 测试数据可参考：
     * https://raw.githubusercontent.com/gaixie/jibu-schema/develop/postgresql/data/test.sql
     */
    protected void initBaseTestData() {
        StringBuilder sb = new StringBuilder();
        //--------------------------------------------
        //  从 github.com 上 clone jibu-schema 项目后，
        //  从 jibu-schema 项目根目录下执行下面的语句，可生成初始化测试数据的Java语句：
        //  more ./postgresql/data/test.sql | sed '/^$\|^\s*\(-\|$\)/d;s/.*/sb.append("&\\n");/'
        //  mac osx 下 sed 语法和 linux 不同，执行下面的命令：
        //  more ./postgresql/data/test.sql | sed '/^[[:blank:]]*-/d;/^[[:blank:]]*$/d;s/.*/sb.append("&\\n");/'
        //--------------------------------------------
        sb.append("insert into userbase(username,password,fullname,type,emailaddress,invited_by) values ('qq','96e79218965eb72c92a549dd5a330112','Manager', 4,'nodto@qq.com',currval('userbase_id_seq'));\n");
        sb.append("insert into userbase(username,password,fullname,type,emailaddress,invited_by) values ('sina','96e79218965eb72c92a549dd5a330112','Sponsor', 2,'nodto@sina.com',(select id from userbase where username = 'qq'));\n");
        sb.append("insert into userbase(username,password,fullname,type,emailaddress,invited_by) values ('outlook','96e79218965eb72c92a549dd5a330112','Register',1,'nodto@outlook.com',(select id from userbase where username = 'sina'));\n");
        sb.append("insert into tokens(value,type,expiration_ts,send_to,created_by) values ('5f4dcc3b5aa765d61d8327deb882cf99','password',now() + interval '1 day' ,'nodto@outlook.com',(select id from userbase where username = 'outlook'));\n");
        sb.append("insert into tokens(value,type,expiration_ts,send_to,created_by) values ('9de4a97425678c5b1288aa70c1669a64','register',now() + interval '7 day' ,'nodto@test',(select id from userbase where username = 'sina'));\n");
        sb.append("insert into tokens(value,type,expiration_ts,send_to,created_by) values ('cc0256df40cbc924af2b31aeccb869b0','signin'  ,now() + interval '14 day','nodto@qq.com',(select id from userbase where username = 'qq'));\n");

        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            QueryRunner run = new QueryRunner();
            run.update(conn, sb.toString());
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            System.out.println(e.getMessage());
        }
    }
}
