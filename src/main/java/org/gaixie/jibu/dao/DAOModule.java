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

import com.google.inject.AbstractModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据访问层的 Bind 类，根据 jibu.properties 中的 databaseType
 * 将 数据访问接口与实现进行绑定。
 * <p>
 * 默认为 PostgreSQL，需要手动创建数据库及表</p>
 */
public class DAOModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(DAOModule.class);
    private final String databaseType;

    public DAOModule() {
        this("PostgreSQL");
    }

    public DAOModule(String databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    protected void configure() {
        if ("PostgreSQL".equalsIgnoreCase(databaseType)){
        }
    }
}
